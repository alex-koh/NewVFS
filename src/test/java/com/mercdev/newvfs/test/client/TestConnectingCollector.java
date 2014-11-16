package com.mercdev.newvfs.test.client;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.testng.annotations.Test;

import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.CommandID;

public class TestConnectingCollector extends TestEstablishedCollector {

	@Override
	protected StateID getStateID() {
		return StateID.CONNECTING;
	}

	@Test
	public void testSendMessage() {
		when(message.getId()).thenReturn(CommandID.CONNECT);
		
		collector.sendMessage(CommandID.CONNECT, message);
		
		verify(handler).setState(StateID.ESTABLISHED);
	}
}
