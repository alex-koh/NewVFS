package com.mercdev.newvfs.server;

import java.util.Collection;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.Message;

public interface CommandHandler {
	Collection<Message> invokeAndGetResult(Command command);
}
