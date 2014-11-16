package com.mercdev.newvfs.test.client;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mercdev.newvfs.client.StateID;

@Test(suiteName="states")
public class TestClosingState extends TestConnectingState {

	@Override
	protected StateID getStateID() {
		return StateID.CLOSING;
	}
	
	@Override
	public void testNeedNext() {
		Assert.assertFalse(state.startConnection());
	}
}
