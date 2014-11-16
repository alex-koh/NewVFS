package com.mercdev.newvfs.test.client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

import static org.mockito.Mockito.*;

import com.mercdev.newvfs.client.CommandBuilder;
import com.mercdev.newvfs.client.CommandsFactory;
import com.mercdev.newvfs.client.Connection;
import com.mercdev.newvfs.client.StateFactory;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.Sender;

public class TestHandlerComplex {
	private Sender<Command, Message> sender;
	private Message message = mock(Message.class);
	private Command command;
	private Handler handler;
	private final int connectionTimeout = 100;
	private final int handlerTimeout = 100;
	
	@BeforeClass
	public void initClass() {
		String messageString = "message";
		Object[] params = new Object[0];
		when(message.getLevel()).thenReturn(Level.INFO);
		when(message.getMessage()).thenReturn(messageString);
		when(message.getParams()).thenReturn(params);
		when(message.getId()).thenReturn(command.getID());

		sender = new Sender<Command, Message>() {
			@Override
			public void writeObjects(Iterable<Command> objects) {
				command = objects.iterator().next();
			}
			@Override
			public Iterable<Message> readObjects() throws IOException {
				return Collections.singleton(message);
			}
			@Override
			public void close() throws IOException {}
		};
		sender = spy(sender);
		
		CommandsFactory cFactory = mock(CommandsFactory.class);
		when(cFactory.getCommandBuilder(any(CommandID.class)))
		.thenAnswer(new Answer<CommandBuilder>() {
			@Override
			public CommandBuilder answer(InvocationOnMock invocation)
					throws Throwable {
				invocation.getArguments()[0]
				return null;
			}
		})
	}
	
	@BeforeMethod
	public void init() {
		Connection connection = new Connection(
				connectionTimeout, Logger.getLogger(TestLogRecord.logName));
		StateFactory sFactory = new StateFactory();
		sFactory.set
		handler = new com.mercdev.newvfs.client.Handler(sFactory, cFactory);
	}
	
	@Test
	public void testQuit() {
		
	}
	
	@AfterMethod
	public void afterMethod() {
		
	}
}
