package com.mercdev.newvfs.server;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

public class DefaultHandler implements CommandHandler {
	private AccountPool accounts;
	private CommandExecutor executor;
	public DefaultHandler(AccountPool accounts, CommandExecutor executor) {
		this.executor = executor;
		this.accounts = accounts;
	}
	@Override
	public Collection<Message> invokeAndGetResult(Command command) {
		Account account = accounts.getAccount(command.getLogin());
		if (account!=null) {
			if (command.getID()!=CommandID.EMPTY)
				executor.invoke(command, account);
			Collection<Message> result = new LinkedList<Message>();
			account.getMessages(result);
			return result;
		}
		else {
			MessageBuilder m = MessageBuilder.getMessageBuilder();
			m.setId(command.getID());
			m.setLevel(Level.WARNING);
			m.setMessage("exception.receiver.account.not.found"); //TODO exception
			m.addParam(command.getLogin());
			return Collections.singleton(m.getResult());
		}

	}

}
