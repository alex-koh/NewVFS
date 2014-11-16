package com.mercdev.newvfs.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

class ConnectHandler implements CommandHandler {
	private AccountPool accounts;
	public ConnectHandler(AccountPool accounts) {
		this.accounts = accounts;
	}
	@Override
	public Collection<Message> invokeAndGetResult(Command command) {
		try {
			Account account = accounts.newAccount(command.getLogin());
			Collection<Message> result = new LinkedList<Message>();
			account.getMessages(result);
			return result;
		}
		catch (IllegalArgumentException exc) {
			MessageBuilder m = 
					MessageBuilder.getMessageBuilder();
			m.setId(CommandID.QUIT);
			m.setLevel(Level.WARNING);
			m.setMessage(exc.getMessage());
			m.addParam(command.getLogin());
			return Collections.singleton(m.getResult());
		}
	}
}
