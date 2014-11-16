package com.mercdev.newvfs.client;

import java.util.logging.Logger;

import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

class CrashCollector extends QuitCollector {
	private Handler handler;
	public CrashCollector(Handler handler,
			CommandsDescription description, Logger logger) {
		super(description, logger);
		this.handler = handler;
	}

	@Override
	public void sendMessage(CommandID command, Message m) {
		if(m.getId()==CommandID.CRASH) {
			logger.warning("exception.client.crash.finish"); //TODO exception
			handler.setState(StateID.QUIT);
		}
		else
			logger.log(m.getLevel(),m.getMessage(),m.getParams());
	}
}
