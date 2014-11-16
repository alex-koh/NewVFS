package com.mercdev.newvfs.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

class EstablishedCollector implements Collector {
	private Logger logger;
	private CommandsDescription description;
	protected Handler handler;
	public EstablishedCollector(Handler handler, 
			CommandsDescription description, Logger logger) 
	{
		this.handler = handler;
		this.logger = logger;
		this.description = description;
	}
	@Override
	public void sendMessage(CommandID id, Message m) 
			throws IllegalStateException
	{
		if (id==m.getId()) {
			sendEmpty(m);
			if (m.getId()==CommandID.QUIT)
				handler.setState(StateID.QUIT);
		}
		else {
			logger.log(Level.WARNING, "exception.client.result.message.lost", //TODO exeption
					new String[] {description.getName(id),
						description.getName(m.getId())});
			handler.setState(StateID.CRASH);
		}
	}
	@Override
	public void sendEmpty(Message m) throws IllegalStateException {
		logger.log(m.getLevel(), m.getMessage(), m.getParams());
	}
	@Override
	public void sendSkipCommand(Command c) {
		throw new UnsupportedOperationException("exception.client.collector"); //TODO exception
	}
	@Override
	public boolean skipCommand() {
		return false;
	}
	@Override
	public boolean isOneStep() {
		return false;
	}
	@Override
	public boolean isWork() {
		return true;
	}
}
