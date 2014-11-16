package com.mercdev.newvfs.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mercdev.newvfs.fs.FileSystem;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;

/**
 * Объект данного класса формирует задачи для отработки в файловой системе и
 * следит за их выполнением. Он может, при необходимости, прервать выполнение
 * задачи. В качестве исходных данных для задачи используются параметры,
 * передаваемые вместе с командой и учетная запись пользователя, от имени
 * которого запускается задача.
 * 
 * @author alex
 *
 */
public class CommandExecutor implements Runnable {
	private Map<CommandID, Class<Task>> tasks;
	private BlockingQueue<Observed> observedQueue;
	private AtomicBoolean isWork;
	private int timeout;
	private Logger logger;
	private ExecutorService executor;
	private FileSystem fs;
	
	class Observed implements Runnable {
		private Task task;
		private Account account;
		private Command command;
		@Override
		public void run() {
			task.invoke(fs, account, command);
		}
		public Account getAccount() {
			return account;
		}
		public void setAccount(Account account) {
			this.account = account;
		}
		public Task getTask() {
			return task;
		}
		public void setTask(Task task) {
			this.task = task;
		}
		public Command getCommand() {
			return command;
		}
		public void setCommand(Command command) {
			this.command = command;
		}
	}
	/**
	 * Инициализация обработчика команд. Метод выполняет связывание обработчика
	 * с целевой файловой системой и загрузку доступных для выполнения классов
	 * команд. Связь индексов команд и классов исполнителей задается в файле
	 * настроек fName в формате {@link Properties}. 
	 * Имени {@link CommandID#name()} соответствует 
	 * имя класса {@link Class#getName()}.
	 * @param executor объект выполняющий задачи.
	 * @param fName имя файла настроек.
	 * @param timeout предельное время ожидания выполнения задачи в ФС.
	 * @param logger протокол ошибок.
	 * @throws FileNotFoundException если файл настроек ненайден.
	 * @throws IOException если произошла ошибка доступа при открытие файла.
	 * @throws NullPointerException если один из переданных параметров == null.
	 */
	public CommandExecutor(FileSystem fs, 
			String fName, int timeout, Logger logger) 
		throws FileNotFoundException, IOException, NullPointerException
	{
		if ((executor==null)||(fName==null)||(logger==null))
			throw new NullPointerException("exception.command.handler.null"); //TODO exception
		Properties classes = new Properties();
		InputStream ios = new FileInputStream(fName);
		classes.load(ios);
		tasks = new HashMap<CommandID, Class<Task>>();
		for (CommandID cid : CommandID.values()) {
			String className = classes.getProperty(cid.name());
			try {
				@SuppressWarnings("unchecked")
				Class<Task> taskClass = (Class<Task>) Class.forName(className);
				taskClass.newInstance();
				tasks.put(cid,taskClass);
					
			}
			catch (ClassNotFoundException exc) {
				logger.log(Level.WARNING, 
						"exception.command.handler.class.not.found", //TODO exception
						new Object[] {className});
			}
			catch (InstantiationException exc) {
				logger.log(Level.WARNING, 
						"exception.command.handler.class.instance", //TODO exception
						new Object[] {className});
			}
			catch (IllegalAccessException exc) {
				logger.log(Level.WARNING, 
						"exception.command.handler.class.access", //TODO exception
						new Object[] {className});
			}
		}
		tasks = Collections.unmodifiableMap(tasks);
		executor = Executors.newFixedThreadPool(1);
		this.fs = fs;
		this.timeout = timeout;
		observedQueue = new LinkedBlockingQueue<Observed>();
	}
	/**
	 * Запуск команды на выполнение.
	 * @param command команда.
	 * @param account учетная запись, от имени которой выполняется команда.
	 * @throws NullPointerException если один из параметров равен null.
	 */
	synchronized public void invoke(Command command, Account account) 
		throws NullPointerException
	{
		// synchronized гарантирует правильную последовательность при вызове
		// команд в файловой системе.
		try {
			if ((command==null)||(account==null))
				throw new NullPointerException(
						"exception.command.handler.invoke.null"); //TODO exception
			Class<Task> classTask = tasks.get(command.getID());
			if (classTask!=null) {
				Task task = classTask.newInstance();
				Observed observed = new Observed();
				observed.setAccount(account);
				observed.setCommand(command);
				observed.setTask(task);
				observedQueue.offer(observed);
			}
			else {
				MessageBuilder message = MessageBuilder.getMessageBuilder();
				message.setId(command.getID());
				message.setLevel(Level.WARNING);
				message.setMessage(
						"exception.command.handler.unsupported.operation");//TODO exception 
				account.sendMessage(message.getResult());
			}
		}
		catch (IllegalAccessException exc) { }
		catch (InstantiationException exc) { }
	}
	/**
	 * Метод отслеживает выполнение команд файловой системы
	 */
	@Override
	public void run() {
		try {
			isWork.set(true);
			while(isWork.get()) {
				Observed observed = observedQueue.take();
				String mName = null;
				try {
					Future<?> f = executor.submit(observed);
					f.get(timeout, TimeUnit.MILLISECONDS);
				}
				catch (TimeoutException exc) {
					mName ="exception.command.handler.fs.task.timeout"; //TODO exception
				}
				catch (ExecutionException exc) {
					mName ="exception.command.handler.fs.execution"; //TODO exception
				}
				if (mName!=null) {
					logger.log(Level.WARNING,mName, new Object[] {
							observed.getAccount().getName(),
							observed.getCommand().getID(),
					});
					MessageBuilder message = MessageBuilder.getMessageBuilder();
					message.setId(observed.getCommand().getID());
					message.setLevel(Level.WARNING);
					message.setMessage(mName);
					observed.getAccount().sendMessage(message.getResult());
				}
			}
		}
		catch(InterruptedException exc) {
			logger.warning("exception.command.handler.interrupted"); //TODO exception			
		}
	}
	/**
	 * Останавливает работу и закрывает обработчик команд
	 */
	public void close() {
		isWork.set(false);
		//TODO ожидание выполнения последней задачи
	}
}
