package com.mercdev.newvfs.test.client;

import java.io.IOException;
import java.io.CharArrayReader;
import java.io.Reader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mockito.Mockito;

import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mercdev.newvfs.client.Client;
import com.mercdev.newvfs.client.ConnectionHandler;

public class TestClient extends Mockito{
	private Handler logHandler;
	private Client client;
	private ConnectionHandler handler;
	private Reader inr;
	private String c1 = "command1";
	private String c2 = "command2";
	private String commands=c1+'\n'+c2+'\n';
	
	@BeforeMethod
	public void init() {
		TestLogRecord.initLoggergetHandler();
		Logger logger = Logger.getLogger(TestLogRecord.logName);
		logHandler = logger.getHandlers()[0];

		handler = mock(ConnectionHandler.class);
		when(handler.isNotExit()).thenReturn(true,false);
		when(handler.isConnect()).thenReturn(true);
		when(handler.isRun()).thenReturn(true,false);
		
		inr = mock(Reader.class);
		
		client = new Client(handler, inr, logger);
	}
	
	@Test(timeOut=50,enabled=false)
	public void testRun() {
		client.run();
		verify(logHandler).publish(new TestLogRecord(
				Level.INFO, "message.client.hello"));
		verify(logHandler).publish(new TestLogRecord(
				Level.INFO, "message.client.goodbye"));
		verify(handler).sendCommand(c1);
		verify(handler).sendCommand(c2);
	}
	
	@Test(timeOut=50,enabled=true)
	public void testIllegalArgument() {
		String message = "message";
		doThrow(new IllegalArgumentException(message))
			.when(handler).sendCommand(eq(c1));
		client.run();
		verify(logHandler).publish(new TestLogRecord(
				Level.WARNING, message, new Object[] {c1}));
	}

	@Test(timeOut=50,enabled=true)
	public void testIOException() throws IOException {
		char[] buffer = c1.toCharArray();
		doThrow(new NullPointerException()).when(inr)
			.read(buffer, 0, buffer.length);
		client.run();
//		verify(logHandler).publish(new TestLogRecord(Level.WARNING,
//			"exception.client.work.io"));
	}
}
