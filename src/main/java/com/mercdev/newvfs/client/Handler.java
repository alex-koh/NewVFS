package com.mercdev.newvfs.client;

import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.mercdev.newvfs.interaction.CommandID;

public class Handler {
	// �������� ������
	private CommandsDescription description;
	// �����������
	private ExecutorService executor;
	// ����������
	private Connection connection;
	// ����������� ������
	private Future<?> task;
	// ���������� ����� ������ ������ ��� ����������
	private int timeout;
	// ������� ������ �������
	private State state;
	// ���� ������
	private boolean work;
	
	private Map<StateID, State> states;
	private Map<StateID, Collector> collectors;
	
	public Handler(StateFactory sFactory, CollectorFactory cFactory,
		Connection connection, CommandsDescription description, int timeout)
	{
		work=true;
		sFactory.setHandler(this);
		cFactory.setHandler(this);
		setState(StateID.QUIT);
		for (StateID id : StateID.values()) {
			states.put(id, sFactory.getState(id));
			collectors.put(id, cFactory.getCollector(id));
		}

		this.connection = connection;
		this.description = description;
		this.timeout = timeout;
		
		executor = Executors.newFixedThreadPool(1);
	}
	public void setState(StateID id) {
		this.state = states.get(id);
		connection.setHandler(collectors.get(id));
		if (state.startConnection()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					state.beforeStart(connection);
					task = executor.submit(connection);
				}
			});
		}
	}		
		/*TODO ����� ����� crash'��. 
		 * 1. ������ �� ������ crash.
		 * 2. ������� ������������ �� ���� � ��������� ��� � ���������.
		 * 3. ������ � �� ������ ������� � ���������, ���� ��� ��������.
		 * 4. ����� ������� �������� ����� ������������.
		 */
	public void sendCommand(String command) throws IllegalStateException {
		String[] terms = command.split("[ ]+");
		CommandID cid = description.getID(terms[0]);
		Collection<String> params = 
				Arrays.asList(terms).subList(1, terms.length-1);
		switch(cid) {
		case CONNECT:
			state.connect(connection,params);
			break;
		case QUIT:
			state.quit(connection);
			break;
		default:
			state.command(connection,cid,params);
		}
	}
	public boolean isWork() {
		return work;
	}
	public void exit() {
		work = false;
	}
	public void close() throws Exception, InterruptedException {
		exit();
		try {
			try {
				// ������� ��������� ������.
				setState(StateID.CLOSING);
				//����, ���� ����� ���������� ���������� ������� ������.
				task.get(timeout, TimeUnit.MILLISECONDS);
			}
			finally {
				if (!task.isDone())
					task.cancel(true);
			}
		}
		catch(TimeoutException exc) {
			throw new Exception("exception.client.connection.break.timelimit"); //TODO exception
		}
		catch (InterruptedException exc) {
			throw new InterruptedException("exception.client.interrupt"); //TODO exception

		}
		catch (ExecutionException exc) {
			Throwable cause = exc.getCause();
			if (cause!=null)
				throw new Exception(cause.getMessage());
			else
				throw new Exception(exc.getMessage());
		}
	}
}
