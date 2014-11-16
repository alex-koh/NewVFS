package com.mercdev.newvfs.client;

import java.util.Collection;

import com.mercdev.newvfs.interaction.CommandID;

class AbstractState implements State {
	private boolean startNext;
	public AbstractState(boolean startNext) {
		this.startNext = startNext;
	}
	@Override
	public void connect(Connection connection, Collection<String> params) 
			throws IllegalStateException 
	{
		throw new IllegalStateException("exception.client.state.connect"); //TODO exception
	}

	@Override
	public void command(Connection connection, CommandID id, 
			Collection<String> params)
			throws IllegalStateException 
	{
		throw new IllegalStateException("exception.client.state.command"); //TODO exception
	}

	@Override
	public void quit(Connection connection) throws IllegalStateException {
		throw new IllegalStateException("exception.client.state.quit"); //TODO exception
	}
	@Override
	public void beforeStart(Connection connection) { }
	@Override
	public boolean startConnection() {
		return startNext;
	}
}

