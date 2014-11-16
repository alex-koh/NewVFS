package com.mercdev.newvfs.test.client;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mercdev.newvfs.client.Collector;
import com.mercdev.newvfs.client.Connection;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.Sender;
import com.mercdev.newvfs.interaction.SenderFactory;

@Test(suiteName="client", groups={"client.init"})
public class TestConnection {
	private Connection connection;
	private SenderFactory<Command, Message> factory;
	private Sender<Command, Message> sender;
	private Collector handler;
	private Command empty;
	private Command command;
	private Message message; 
	private Handler loggerHandler;
	private final int timeout = 100; //ms
	
	@SuppressWarnings("unchecked")
	@BeforeMethod
	public void init() throws IOException {
		TestLogRecord.initLoggergetHandler();
		Logger logger = Logger.getLogger(TestLogRecord.logName);		
		loggerHandler = logger.getHandlers()[0];
		
		message = mock(Message.class);
		
		sender = mock(Sender.class);
		when(sender.readObjects()).thenReturn(Collections.singleton(message));
		factory = mock(SenderFactory.class);
		when(factory.getSender()).thenReturn(sender);
		
		empty = mock(Command.class);
		when(empty.getID()).thenReturn(CommandID.EMPTY);
		command = mock(Command.class);
		when(command.getID()).thenReturn(CommandID.CONNECT);
		
		handler = mock(Collector.class);
		
		// Проверяемый объект соединения 
		connection = new Connection(timeout, logger);
		connection.setEmpty(empty);
		connection.setHandler(handler);
		connection.setSender(factory);
	}
	
	/**
	 * Неверно заданы параметры.
	 */
	@Test
	public void testNull() {
		connection.setEmpty(null);
		connection.run();
		verify(loggerHandler).publish(new TestLogRecord(Level.WARNING,
				"exception.client.connection.null"));
	}
	/**
	 * Работа в первом варианте. Делает два прохода. На первом проходе 
	 * отправляет заданную команду. На втором - пустую команду через 
	 * заданный промежуток времени.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Test(timeOut=timeout*3/2)
	public void testOneStep() throws IOException {
		
		when(handler.isOneStep()).thenReturn(true);
		when(handler.isWork()).thenReturn(true,true,false);
		
		when(message.getId()).thenReturn(CommandID.EMPTY,CommandID.CONNECT);
		connection.sendCommand(command);
		long begin = System.currentTimeMillis();
		connection.run();
		assertTrue(System.currentTimeMillis()-begin>timeout);

		verify(handler).isOneStep();
		verify(handler,times(3)).isWork();
		verify(handler).sendEmpty(message);
		verify(handler).sendMessage(command.getID(), message);
		
		verify(factory,times(2)).getSender();
		verify(sender,times(2)).writeObjects(any(Iterable.class));
		verify(sender,times(2)).readObjects();
		verify(sender,times(2)).close();
	}
	/**
	 * Работа во втором варианте. Сценарий работы: 1) первая команда проходит,
	 * 2) вторая команда проходит, но в ответном сообщении есть ошибка,
	 * 3) третья команда пропускается.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Test(timeOut=timeout)
	public void testMultyStepSkip() throws IOException {
		when(handler.isOneStep()).thenReturn(false);
		when(handler.isWork()).thenReturn(true,false);
		when(handler.skipCommand()).thenReturn(false,true);
		connection.sendCommand(command);
		connection.sendCommand(command);
		
		connection.run();

		verify(handler).isOneStep();
		verify(handler,times(2)).isWork();
		verify(handler,times(2)).skipCommand();
		verify(handler).sendMessage(command.getID(), message);
		verify(handler).sendSkipCommand(command);
		
		verify(factory).getSender();
		verify(sender).writeObjects(any(Iterable.class));
		verify(sender).readObjects();
		verify(sender).close();
	}

	/**
	 * Проверка работы после при нормальном закрытие соединения.
	 * 1) В первой порции одна команда. 2) Перед второй порцией появляется
	 * флаг закрытия соединения. Вторая порция команд все равно проходит.  
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Test(timeOut=timeout)
	public void testMultyStep() throws IOException {
		when(handler.isOneStep()).thenReturn(false);
		when(handler.isWork()).then(new Answer<Boolean>() {
			int i=3;
			@Override
			public Boolean answer(InvocationOnMock invocation) {
				if(i-->0)
					switch(i) {
					case 2:
						return true;
					case 1:
						connection.sendCommand(command);
					case 0:
						return false;
					}
				throw new IllegalStateException();
			}
		});
		when(handler.skipCommand()).thenReturn(false);
		connection.sendCommand(command);
		
		connection.run();

		verify(handler).isOneStep();
		verify(handler,times(3)).isWork();
		verify(handler,times(2)).sendMessage(command.getID(), message);
		
		verify(factory,times(2)).getSender();
		verify(sender,times(2)).writeObjects(any(Iterable.class));
		verify(sender,times(2)).readObjects();
		verify(sender,times(2)).close();
	}
	/**
	 * Отправка пустой команды.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Test(timeOut=timeout*3/2)
	public void testMultyStepEmpty() throws IOException {
		when(handler.isOneStep()).thenReturn(false);
		when(handler.isWork()).thenReturn(true,false);
		when(handler.skipCommand()).thenReturn(false);
		
		when(message.getId()).thenReturn(CommandID.EMPTY);
		
		long begin = System.currentTimeMillis();
		connection.run();
		assertTrue(System.currentTimeMillis()-begin>timeout);

		verify(handler).isOneStep();
		verify(handler,times(2)).isWork();
		verify(handler).skipCommand();
		verify(handler).sendEmpty(message);
		
		verify(factory).getSender();
		verify(sender).writeObjects(any(Iterable.class));
		verify(sender).readObjects();
		verify(sender).close();
	}

}


