package com.mercdev.newvfs.test.client;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.logging.Level;

import org.testng.annotations.Test;

import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.CommandID;

public class TestCrashCollector extends TestCollector{

	@Override
	protected StateID getStateID() {
		return StateID.CRASH;
	}
	
	@Test
	public void testSendMessage() {
		CommandID id = CommandID.CRASH;
		when(message.getId()).thenReturn(id);
		
		collector.sendMessage(id, message);
		
		verify(handler).setState(StateID.QUIT);
		verify(logHandler).publish(new TestLogRecord(
				Level.WARNING, "exception.client.crash.finish"));
	}

}
