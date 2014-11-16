package com.mercdev.newvfs.test.client;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.testng.annotations.Test;

import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.CommandID;

@Test(suiteName="states")
public class TestConnectingState extends TestState {
	
	@Override
	protected StateID getStateID() {
		return StateID.CONNECTING;
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void testConnect() {
		Collection<String> someParams = new ArrayList<String>();
		state.connect(connection, someParams);
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void testCommand() {
		CommandID anyID = CommandID.CD;
		Collection<String> someParams = new ArrayList<String>();
		state.command(connection, anyID, someParams);
	}

	@Test(expectedExceptions=IllegalStateException.class)
	public void testQuit() {
		state.quit(connection);
	}
	
	@Test
	public void testNeedNext() {
		assertTrue(state.startConnection());
	}
}
