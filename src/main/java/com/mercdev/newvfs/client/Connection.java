package com.mercdev.newvfs.client;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.Sender;
import com.mercdev.newvfs.interaction.SenderFactory;

/**
 * Класс описывает соединение с сервером.
 * Соединение выполняет цикл передачи команд на сервер и приёма ответных 
 * сообщений. Работа может происходить в двух вариантах: однакратная посылка 
 * команды на сервер с последующим ожиданием ответа на неё и посылка серии 
 * команд (в зависимости от ключей {@link #setOneStepStart()} и 
 * {@link #setMultyStepStart()} соответственно).
 * Класс использует различные обработчики ответных сообщений, которые могут 
 * менять состояние клиента в зависимости от содержания.
 * @author alex
 *
 */
public class Connection implements Runnable {
	// Флаг постоянной работы
	private AtomicBoolean inProcess;
	// Флаг обязательного выполнения хотя бы одного шага
	private Logger logger;
	private SenderFactory<Command, Message> factory;
	private Collector handler;
	private BlockingQueue<Command> commandsToSending;
	private BlockingQueue<CommandID> commandToReceive;
	private Command empty;
	private int timeout;
	/**
	 * Конструктор соединения.
	 * @param timeout время (в милисекундах), в течении которого 
	 * ожидается пополнение очереди.
	 * @param logger протокол.
	 */
	public Connection(int timeout, Logger logger)
	{
		commandsToSending = new LinkedBlockingQueue<Command>();
		commandToReceive = new LinkedBlockingQueue<CommandID>();
		inProcess = new AtomicBoolean(false);
		this.timeout = timeout;
		this.logger = logger;
	}
	/**
	 * Пытается получить команду из очереди в течении заданного времени. 
	 * Если очередь пуста, возвращает список с пустой командой, иначе возвращает
	 * список всех доступных команд.
	 * @return список доступных команд.
	 */
	private Collection<Command> poll() {
		List<Command> result = new LinkedList<Command>();
		try {
			Command c = commandsToSending.poll(timeout, TimeUnit.MILLISECONDS);
			if (c!=null) {
				result.add(c);
				commandsToSending.drainTo(result);
				return result;
			}
		}
		catch(InterruptedException exc) {}
		result.add(empty);
		return result;
	}
	/**
	 * Тело цикла отправки команд на сервер. Отправляет команду <code>c</code>
	 * на сервер и принимает пакет сообщений.
	 * @param c отправляемая команда.
	 * @throws IOException Ошибка передачи данных.
	 */
	private void body(Command c) throws IOException {
		// подключение
		Sender<Command,Message> sender = factory.getSender();
		try {
			// Отправка команды
			sender.writeObjects(Collections.singleton(c));
			// Добавить команду в список ожидающих ответ
			if (c.getID()!=CommandID.EMPTY)
				commandToReceive.add(c.getID());
			// Цикл считывания ответных сообщений
			for(Message m : sender.readObjects()) {
				if (m.getId()==CommandID.EMPTY)
					handler.sendEmpty(m);
				else {
					handler.sendMessage(commandToReceive.poll(), m);
				}
			}
		}
		finally {
			sender.close();
		}
	}
	/**
	 * Проверяет состояние соединения. Если соединение запущено и работает,
	 * то генерирует исключение. Этот проверка делается вначале почти
	 * каждого метода.
	 * @throws IllegalStateException если соединение работает.
	 */
	private void ifInProgress() throws IllegalStateException {
		if (isInProcess())
			throw new IllegalStateException("exception.client.connection.run"); //TODO exception
	}
	/**
	 * Флаг запуска.
	 * @return true, если первая итерация будет выполнена.
	 */
	public boolean isNotEmpty() {
		return !commandsToSending.isEmpty();
	}
	/**
	 * Флаг непосредственной работы метода в текущий момент.
	 * @return true, если работает
	 */
	public boolean isInProcess() {
		return inProcess.get();
	}
	/**
	 *  <p>
	 *  Метод осуществляет отправку команд на сервер и прием ответных сообщений.
	 *  Метод работает в двух вариантах: однократное включение
	 *  (обеспечивается заданием флага {@link #startOneStep()}) и постоянная
	 *  работа (обеспечивается заданием флага {@link #start()}). 
	 *  </p><p>
	 *  В первом варианте метод запускает один цикл отправки команды нв 
	 *  сервер и приема ответных сообщений, после которого обнуляет флаг первого
	 *  включения и останавливает работу.
	 *  </p><p>
	 *  Во втором варианте метод работает пока не будет вызван метод 
	 *  {@link #stop()} и не будет исчерпона очередь команд, ждущих ответных
	 *  сообщений.
	 */
	public void run() {
		try {
			if(inProcess.getAndSet(true))
				throw new IllegalStateException(
						"exception.client.connection.run"); //TODO exception
			if ((factory==null)||(handler==null)||(empty==null))
				throw new NullPointerException(
						"exception.client.connection.null"); //TODO exception
			if (handler.isOneStep()) {
				if(handler.isWork())
					body(commandsToSending.take());
				while (handler.isWork()) {
					Thread.sleep(timeout);
					body(empty);
				}
			}
			else {
				while(handler.isWork()||isNotEmpty()) {
					// В цикле пробегаются все доступные команды
					for(Command c : poll()) {
						if (handler.skipCommand())
							handler.sendSkipCommand(c);
						else
							body(c);
					}
				}
			}
		}
		catch(IOException exc) {
			logger.warning("exception.client.connection.ioexception");//TODO exeption
		}
		catch(Exception exc) {
			logger.warning(exc.getMessage()); //TODO exeption
		}
		finally {
			inProcess.set(false);
		}
	}
	/**
	 * Задает значение обработчика сообщений для текущей сессии.
	 * @param handler обработчик сообщений. 
	 * (проверка аргумента осуществляется при запуске)
	 */
	public void setHandler(Collector handler) {
		this.handler = handler;
	}
	/**
	 * Задает фабрику передатчиков для текущей сессии.
	 * @param factory фабрика. (проверка аргумента осуществляется при запуске)
	 */
	public void setSender(SenderFactory<Command, Message> factory)
			throws IllegalStateException {
		ifInProgress();
		this.factory = factory;
	}
	/**
	 * Добавляет команду в очередь отправки на сервер.
	 * @param command команда.
	 */
	public void sendCommand(Command command) {
		commandsToSending.add(command);
	}
	/**
	 * Устанавливает значение пустой команды для текущей сессии
	 * @param empty пустая команда
	 */
	public void setEmpty(Command empty) throws IllegalStateException {
		ifInProgress();
		this.empty = empty;
	}
}
