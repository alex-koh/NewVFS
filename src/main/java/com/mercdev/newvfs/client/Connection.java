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
 * ����� ��������� ���������� � ��������.
 * ���������� ��������� ���� �������� ������ �� ������ � ����� �������� 
 * ���������. ������ ����� ����������� � ���� ���������: ����������� ������� 
 * ������� �� ������ � ����������� ��������� ������ �� �� � ������� ����� 
 * ������ (� ����������� �� ������ {@link #setOneStepStart()} � 
 * {@link #setMultyStepStart()} ��������������).
 * ����� ���������� ��������� ����������� �������� ���������, ������� ����� 
 * ������ ��������� ������� � ����������� �� ����������.
 * @author alex
 *
 */
public class Connection implements Runnable {
	// ���� ���������� ������
	private AtomicBoolean inProcess;
	// ���� ������������� ���������� ���� �� ������ ����
	private Logger logger;
	private SenderFactory<Command, Message> factory;
	private Collector handler;
	private BlockingQueue<Command> commandsToSending;
	private BlockingQueue<CommandID> commandToReceive;
	private Command empty;
	private int timeout;
	/**
	 * ����������� ����������.
	 * @param timeout ����� (� ������������), � ������� �������� 
	 * ��������� ���������� �������.
	 * @param logger ��������.
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
	 * �������� �������� ������� �� ������� � ������� ��������� �������. 
	 * ���� ������� �����, ���������� ������ � ������ ��������, ����� ����������
	 * ������ ���� ��������� ������.
	 * @return ������ ��������� ������.
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
	 * ���� ����� �������� ������ �� ������. ���������� ������� <code>c</code>
	 * �� ������ � ��������� ����� ���������.
	 * @param c ������������ �������.
	 * @throws IOException ������ �������� ������.
	 */
	private void body(Command c) throws IOException {
		// �����������
		Sender<Command,Message> sender = factory.getSender();
		try {
			// �������� �������
			sender.writeObjects(Collections.singleton(c));
			// �������� ������� � ������ ��������� �����
			if (c.getID()!=CommandID.EMPTY)
				commandToReceive.add(c.getID());
			// ���� ���������� �������� ���������
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
	 * ��������� ��������� ����������. ���� ���������� �������� � ��������,
	 * �� ���������� ����������. ���� �������� �������� ������� �����
	 * ������� ������.
	 * @throws IllegalStateException ���� ���������� ��������.
	 */
	private void ifInProgress() throws IllegalStateException {
		if (isInProcess())
			throw new IllegalStateException("exception.client.connection.run"); //TODO exception
	}
	/**
	 * ���� �������.
	 * @return true, ���� ������ �������� ����� ���������.
	 */
	public boolean isNotEmpty() {
		return !commandsToSending.isEmpty();
	}
	/**
	 * ���� ���������������� ������ ������ � ������� ������.
	 * @return true, ���� ��������
	 */
	public boolean isInProcess() {
		return inProcess.get();
	}
	/**
	 *  <p>
	 *  ����� ������������ �������� ������ �� ������ � ����� �������� ���������.
	 *  ����� �������� � ���� ���������: ����������� ���������
	 *  (�������������� �������� ����� {@link #startOneStep()}) � ����������
	 *  ������ (�������������� �������� ����� {@link #start()}). 
	 *  </p><p>
	 *  � ������ �������� ����� ��������� ���� ���� �������� ������� �� 
	 *  ������ � ������ �������� ���������, ����� �������� �������� ���� �������
	 *  ��������� � ������������� ������.
	 *  </p><p>
	 *  �� ������ �������� ����� �������� ���� �� ����� ������ ����� 
	 *  {@link #stop()} � �� ����� ��������� ������� ������, ������ ��������
	 *  ���������.
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
					// � ����� ����������� ��� ��������� �������
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
	 * ������ �������� ����������� ��������� ��� ������� ������.
	 * @param handler ���������� ���������. 
	 * (�������� ��������� �������������� ��� �������)
	 */
	public void setHandler(Collector handler) {
		this.handler = handler;
	}
	/**
	 * ������ ������� ������������ ��� ������� ������.
	 * @param factory �������. (�������� ��������� �������������� ��� �������)
	 */
	public void setSender(SenderFactory<Command, Message> factory)
			throws IllegalStateException {
		ifInProgress();
		this.factory = factory;
	}
	/**
	 * ��������� ������� � ������� �������� �� ������.
	 * @param command �������.
	 */
	public void sendCommand(Command command) {
		commandsToSending.add(command);
	}
	/**
	 * ������������� �������� ������ ������� ��� ������� ������
	 * @param empty ������ �������
	 */
	public void setEmpty(Command empty) throws IllegalStateException {
		ifInProgress();
		this.empty = empty;
	}
}
