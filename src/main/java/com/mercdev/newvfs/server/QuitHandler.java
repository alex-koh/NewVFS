package com.mercdev.newvfs.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

public class QuitHandler implements CommandHandler {
	private AccountPool accounts;
	public QuitHandler(AccountPool accounts) {
		this.accounts = accounts;
	}

	@Override
	public Collection<Message> invokeAndGetResult(Command command) {
		Account account = accounts.disconnect(command.getLogin());
		if (account!=null) {
			Collection<Message> result = new LinkedList<Message>();
			account.getMessages(result);
			MessageBuilder m = MessageBuilder.getMessageBuilder();
			m.setId(CommandID.QUIT);
			m.setLevel(Level.INFO);
			m.setMessage("message.server.goodbye"); //TODO message
			result.add(m.getResult());
			return result;
		}
		else {
			MessageBuilder m = MessageBuilder.getMessageBuilder();
			m.setId(CommandID.QUIT);
			m.setLevel(Level.WARNING);
			m.setMessage("exception.server.account.not.found"); //TODO exception
			m.addParam(command.getLogin());
			return Collections.singleton(m.getResult());
		}

	}

}
