package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.SenderFactory;

public class StateFactory {
	private Handler handler;
	private CommandsFactory commandsFactory;
	private SenderFactory<Command, Message> senderFactory;

	public StateFactory() {}
	public State getState(StateID state) {
		if ((commandsFactory==null)||(handler==null)||(senderFactory==null))
			throw new NullPointerException("exception.client.quit.null"); //TODO exception
		switch(state) {
		case QUIT:
			return new QuitState(commandsFactory, handler, senderFactory);
		case CONNECTING:
			return new AbstractState(true);
		case CLOSING:
			return new AbstractState(false);
		case CRASH:
			return new CrashState(commandsFactory);
		case ESTABLISHED:
			return new EstablishedState(handler,commandsFactory);
		default:
			throw new IllegalArgumentException(
					"exception.client.state.factory");
		}
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	public void setCommandsFactory(CommandsFactory commandsFactory) {
		this.commandsFactory = commandsFactory;
	}
	public void setSenderFactory(SenderFactory<Command, Message> senderFactory) {
		this.senderFactory = senderFactory;
	}
}
