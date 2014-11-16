package com.mercdev.newvfs.client;

import java.util.Collection;

import com.mercdev.newvfs.interaction.CommandID;

public interface State {
	void connect(Connection connection, Collection<String> params) 
			throws IllegalStateException ;
	void command(Connection connection, CommandID id, Collection<String> params) 
			throws IllegalStateException ;
	void quit(Connection connection) throws IllegalStateException ;
	void beforeStart(Connection connection);
	boolean startConnection();
}
