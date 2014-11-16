package com.mercdev.newvfs.test.client;

import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.*;

import com.mercdev.newvfs.client.CommandsFactory;
import com.mercdev.newvfs.client.Connection;
import com.mercdev.newvfs.client.Handler;
import com.mercdev.newvfs.client.State;
import com.mercdev.newvfs.client.StateFactory;
import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.SenderFactory;

public abstract class TestState {
	protected SenderFactory<Command, Message> senders;
	protected CommandsFactory commandsFactory;
	protected Connection connection;
	protected Handler handler;
	protected State state;
	
	@BeforeMethod
	public void init() {
		handler = mock(Handler.class);
		commandsFactory = mock(CommandsFactory.class);
		@SuppressWarnings("unchecked")
		SenderFactory<Command, Message> senders = mock(SenderFactory.class);
		this.senders = senders;
		
		connection = mock(Connection.class);
		
		StateFactory sFactory = new StateFactory();
		sFactory.setHandler(handler);
		sFactory.setCommandsFactory(commandsFactory);
		sFactory.setSenderFactory(senders);
		state = sFactory.getState(getStateID());
		
	}
	
	protected abstract StateID getStateID();
}
