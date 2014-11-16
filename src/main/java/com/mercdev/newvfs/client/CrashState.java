package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.CommandID;

class CrashState extends AbstractState {
	private CommandsFactory factory;
	public CrashState(CommandsFactory factory) {
		super(true);
		this.factory = factory;
	}
	@Override
	public void beforeStart(Connection connection) {
		CommandBuilder cb = factory.getCommandBuilder(CommandID.CRASH);
		connection.sendCommand(cb.getResult());
	}
}
