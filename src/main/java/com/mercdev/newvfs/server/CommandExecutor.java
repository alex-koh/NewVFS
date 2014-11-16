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
 * ������ ������� ������ ��������� ������ ��� ��������� � �������� ������� �
 * ������ �� �� �����������. �� �����, ��� �������������, �������� ����������
 * ������. � �������� �������� ������ ��� ������ ������������ ���������,
 * ������������ ������ � �������� � ������� ������ ������������, �� �����
 * �������� ����������� ������.
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
	 * ������������� ����������� ������. ����� ��������� ���������� �����������
	 * � ������� �������� �������� � �������� ��������� ��� ���������� �������
	 * ������. ����� �������� ������ � ������� ������������ �������� � �����
	 * �������� fName � ������� {@link Properties}. 
	 * ����� {@link CommandID#name()} ������������� 
	 * ��� ������ {@link Class#getName()}.
	 * @param executor ������ ����������� ������.
	 * @param fName ��� ����� ��������.
	 * @param timeout ���������� ����� �������� ���������� ������ � ��.
	 * @param logger �������� ������.
	 * @throws FileNotFoundException ���� ���� �������� ��������.
	 * @throws IOException ���� ��������� ������ ������� ��� �������� �����.
	 * @throws NullPointerException ���� ���� �� ���������� ���������� == null.
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
	 * ������ ������� �� ����������.
	 * @param command �������.
	 * @param account ������� ������, �� ����� ������� ����������� �������.
	 * @throws NullPointerException ���� ���� �� ���������� ����� null.
	 */
	synchronized public void invoke(Command command, Account account) 
		throws NullPointerException
	{
		// synchronized ����������� ���������� ������������������ ��� ������
		// ������ � �������� �������.
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
	 * ����� ����������� ���������� ������ �������� �������
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
	 * ������������� ������ � ��������� ���������� ������
	 */
	public void close() {
		isWork.set(false);
		//TODO �������� ���������� ��������� ������
	}
}
