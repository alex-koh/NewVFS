package com.mercdev.newvfs.server;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.Sender;
import com.mercdev.newvfs.interaction.SenderFactory;
/**
 * Объекты класса принимают запрос от пользователя, находят старый или создают
 * новый акаунт, запускают требуемую задачу на выполнение и отправляют обратно
 * список имеющихся на данный момент сообщений.
 * 
 * @author alex
 *
 */
public class Receiver implements Runnable {
	private SenderFactory<Message, Command> factory;
	private AtomicBoolean isWork;
	private Logger logger;
	private Map<CommandID, CommandHandler> handlers;
	/**
	 * Конструктор приемников.
	 * @param sFactory фабрика передатчиков.
	 * @param accounts список акаунтов.
	 * @throws NullPointerException если один из параметров равен null.
	 */
	public Receiver(SenderFactory<Message, Command> sFactory, 
			AccountPool accounts, Logger logger,
			CommandExecutor executor) throws NullPointerException 
	{
		if ((accounts==null)||(sFactory==null)||
				(logger==null)||(executor==null))
			throw new NullPointerException("exception.server.receiver.null");
		this.factory = sFactory;
		isWork = new AtomicBoolean(false);
		handlers = new HashMap<CommandID, CommandHandler>();
		handlers.put(CommandID.CONNECT, new ConnectHandler(accounts));
		handlers.put(CommandID.QUIT, new QuitHandler(accounts));
		handlers.put(CommandID.EMPTY, new DefaultHandler(accounts, executor));
	}
	/**
		Метод осуществляет обмен данных между учетной записью пользователя
		и передатчиком. В случае отсутствия требуемой учетной записи, метод
		пытается создать новую запись. 
	 */
	@Override
	public void run() {
		isWork.set(true);
		try {
			while(isWork.get()) {
				Sender<Message, Command> sender = factory.getSender();
				try {
					try {
						Command command = null;
						for (Command c : sender.readObjects()) {
							command = c;
							break;
						}
						if (command!=null) {
							CommandHandler handler = 
									handlers.get(command.getID());
							if(handler==null)
								handler = handlers.get(CommandID.EMPTY);
							sender.writeObjects(
									handler.invokeAndGetResult(command));
						}
						else {
							logger.warning(
								"exception.receiver.command.not.found"); //TODO exception
						}
					}
					finally {
						sender.close();
					}
				}
				catch (IOException exc) {
					logger.warning(exc.getMessage());
				}
			}
		}			
		catch(IOException exc) {
			logger.warning(
					"exception.server.receiver.sender.factory.error"); //TODO exception
		}
	}
	public void close() {
		isWork.set(false);
	}
}
