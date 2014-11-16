package com.mercdev.newvfs.test.client;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.testng.annotations.Test;

import com.mercdev.newvfs.client.Collector;
import com.mercdev.newvfs.client.CommandBuilder;
import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;

@Test(suiteName="states")
public class TestQuitState extends TestState {
	
	@Override
	protected StateID getStateID() {
		return StateID.QUIT;
	}
	
	@Test
	public void testConnect() {
		String host = "host";
		String login = "login";
		int port = 45;
		Collection<String> params = new ArrayList<String>();
		params.add(host+":"+port);
		params.add(login);
		
		Command empty = mock(Command.class);
		CommandBuilder cb = mock(CommandBuilder.class);
		when(cb.getResult()).thenReturn(empty);
		
		when(commandsFactory.getCommandBuilder(any(CommandID.class)))
		.thenReturn(cb);
		
		state.connect(connection, params);
		
		verify(senders).setSocketAddress(host, port);
		verify(commandsFactory).setLogin(login);
		
		verify(connection).setEmpty(empty);
		verify(connection).setSender(senders);
		
		verify(handler).setState(StateID.CONNECTING);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testConnectException() {
		state.connect(connection, Collections.singleton("host"));
	}	

	@SuppressWarnings("unchecked")
	@Test(expectedExceptions=IllegalStateException.class)
	public void testCommand() {
		CommandID anyID = CommandID.CD;
		Collection<String> someParams = new ArrayList<String>();
		state.command(connection, anyID, someParams);
	}

	@Test
	public void testQuit() {
		state.quit(connection);
		verify(handler).exit();
	}
}
