package com.mercdev.newvfs.client;

import java.util.Iterator;
import java.util.Collection;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.SenderFactory;

class QuitState extends AbstractState {
	private CommandsFactory cFactory;
	private Handler handler;
	private SenderFactory<Command, Message> sFactory;
	
	public QuitState(CommandsFactory cFactory, Handler handler,
			SenderFactory<Command, Message> sFactory)
	{
		super(false);
		this.cFactory = cFactory;
		this.handler = handler;
		this.sFactory = sFactory;
	}
	@Override
	public void connect(Connection connection, Collection<String> params) 
			throws IllegalStateException, IllegalArgumentException {
		if (params.size()==2) {
			Iterator<String> p = params.iterator();
			// Расшифровка адреса и порта сервера
			String[] hostAddr = p.next().split(":");
			String login = p.next();
			int port = -1;
			try {
				if (hostAddr.length>1)
					port = Integer.valueOf(hostAddr[1]);
			}
			catch(NumberFormatException exc) {}
			// Адрес сервера
			sFactory.setSocketAddress(hostAddr[0], port);
			// Настройка соединения с сервером
			connection.setSender(sFactory);
			// Настройка фабрики на конкретного пользователя
			cFactory.setLogin(login);
			// Настройка пустой команды под текущего пользователя
			CommandBuilder empty = cFactory.getCommandBuilder(CommandID.EMPTY);
			connection.setEmpty(empty.getResult());
			// Отправка команды на регистрацию
			CommandBuilder cb = cFactory.getCommandBuilder(CommandID.CONNECT);
			connection.sendCommand(cb.getResult());
			// Попытка соединения
			handler.setState(StateID.CONNECTING);
		}
		else {
			// Если пропущен один нз параметров
			throw new IllegalArgumentException(
					"exception.client.quit.operand.missed"); //TODO exception
		}
	}
	@Override
	public void quit(Connection connection) throws IllegalStateException {
		handler.exit();
	}
}
