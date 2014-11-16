package com.mercdev.newvfs.test.client;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.logging.Level;

import org.testng.annotations.Test;

import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.CommandID;

public class TestEstablishedCollector extends TestCollector {

	@Override
	protected StateID getStateID() {
		return StateID.ESTABLISHED;
	}
	
	@Test
	public void testSendMessageCrash() {
		CommandID notCD = CommandID.CONNECT;
		when(message.getId()).thenReturn(CommandID.CD);
		
		collector.sendMessage(notCD, message);
		
		verify(handler).setState(StateID.CRASH);
		verify(logHandler).publish(new TestLogRecord(
				Level.WARNING, 
				"exception.client.result.message.lost", 
				new Object[2]));
	}

	@Test
	public void testSendMessageQuit() {
		CommandID id = CommandID.QUIT;
		when(message.getId()).thenReturn(id);
		
		collector.sendMessage(id, message);
		
		verify(handler).setState(StateID.QUIT);
		verify(logHandler).publish(record);
	}

	@Test
	public void testSendEmpty() {
		collector.sendEmpty(message);
		
		verify(logHandler).publish(record);
	}
}
