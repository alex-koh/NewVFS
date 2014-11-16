package com.mercdev.newvfs.client;

import java.util.Collection;

import com.mercdev.newvfs.interaction.CommandID;

class EstablishedState extends AbstractState {
	private CommandsFactory factory;
	private Handler handler;
	public EstablishedState(Handler handler, CommandsFactory factory) {
		super(true);
		this.handler = handler;
		this.factory = factory;
	}
	@Override
	public void command(Connection connection, CommandID id, 
			Collection<String> params)
			throws IllegalStateException {
		CommandBuilder cb = factory.getCommandBuilder(id);
		cb.setParams(params);
		connection.sendCommand(cb.getResult());
	}
	@Override
	public void quit(Connection connection) throws IllegalStateException {
		CommandBuilder cb = factory.getCommandBuilder(CommandID.QUIT);
		connection.sendCommand(cb.getResult());
		handler.setState(StateID.QUIT);
	}
	@Override
	public boolean startConnection() {
		return true;
	}
}
