package com.mercdev.newvfs.test.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.mercdev.newvfs.client.Collector;
import com.mercdev.newvfs.client.CollectorFactory;
import com.mercdev.newvfs.client.CommandsDescription;
import com.mercdev.newvfs.client.Connection;
import com.mercdev.newvfs.client.Handler;
import com.mercdev.newvfs.client.StateID;
import com.mercdev.newvfs.interaction.Message;


public abstract class TestCollector {
	protected Connection connection;
	protected Handler handler;
	protected Collector collector;
	protected java.util.logging.Handler logHandler;
	
	@BeforeMethod
	public void init() {
		logHandler = TestLogRecord.initLoggergetHandler();
		
		handler = mock(Handler.class);
		
		CommandsDescription description = mock(CommandsDescription.class);
		
		CollectorFactory cFactory = new CollectorFactory();
		cFactory.setDescription(description);
		cFactory.setHandler(handler);
		cFactory.setLogger(TestLogRecord.logName);
		
		collector = cFactory.getCollector(getStateID());
	}
	
	protected Message message;
	protected LogRecord record;
	
	@BeforeMethod
	public void messageInit() {
		message = mock(Message.class);
		
		String messageString = "message";
		Object[] params = new Object[0];
		
		when(message.getLevel()).thenReturn(Level.INFO);
		when(message.getMessage()).thenReturn(messageString);
		when(message.getParams()).thenReturn(params);
		
		record = new TestLogRecord(message.getLevel(), 
				message.getMessage(), message.getParams());
	}
	
	protected abstract StateID getStateID();
	
	@AfterMethod
	public void end() {
		Logger.getLogger(TestLogRecord.logName).removeHandler(logHandler);
	}
}
