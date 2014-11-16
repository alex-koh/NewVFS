package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

public interface Collector {
	void sendMessage(CommandID command, Message message) 
			throws IllegalStateException ;
	void sendEmpty(Message message) throws IllegalStateException ;
	void sendSkipCommand(Command c);
	boolean skipCommand();
	boolean isWork();
	boolean isOneStep();
}
