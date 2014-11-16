package com.mercdev.newvfs.client;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.Path;

class QuitCollector implements Collector {
	protected Logger logger;
	protected CommandsDescription description;
	public QuitCollector(CommandsDescription description, Logger logger) {
		this.description = description;
		this.logger = logger;
	}
	@Override
	public void sendMessage(CommandID command, Message m)
			throws IllegalStateException {
		throw new IllegalStateException("exception.client.quit.collector"); //TODO exception 
	}
	@Override
	public void sendEmpty(Message message) throws IllegalStateException {
		throw new IllegalStateException("exception.client.quit.collector"); //TODO exception		
	}
	@Override
	public void sendSkipCommand(Command c) {
		List<String> params = new ArrayList<String>(c.getParams().size()+1);
		params.add(description.getName(c.getID()));
		for (Path p : c.getParams())
			params.add(description.getPathFormat().pathToString(p));
		logger.log(Level.INFO,"exception.client.skip.command",
				params.toArray());		
	}
	@Override
	public boolean skipCommand() {
		return true;
	}
	@Override
	public boolean isOneStep() {
		return false;
	}
	@Override
	public boolean isWork() {
		return false;
	}
}
