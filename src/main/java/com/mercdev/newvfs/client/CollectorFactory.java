package com.mercdev.newvfs.client;

import java.util.logging.Logger;

public class CollectorFactory {
	private CommandsDescription description;
	private Handler handler;
	private Logger logger;
	public Collector getCollector(StateID id) {
		if ((description==null)||(handler==null)||(logger==null))
			throw new NullPointerException("exception.client.quit.null"); //TODO exception
		switch(id) {
		case QUIT:
			return new QuitCollector(description,logger);
		case CONNECTING:
			return new ConnectCollector(handler, description, logger);
		case ESTABLISHED:
			return new EstablishedCollector(handler, description, logger);
		case CLOSING:
			return new EstablishedCollector(handler, description, logger);
		case CRASH:
			return new CrashCollector(handler, description, logger);
		default:
			throw new IllegalArgumentException(
					"exception.client.collector.factory");
		}
	}
	public void setDescription(CommandsDescription description) {
		this.description = description;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public void setLogger(String name) {
		this.logger = Logger.getLogger(name);
	}
}
