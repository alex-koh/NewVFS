package com.mercdev.newvfs.client;


import java.util.logging.Logger;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

class ConnectCollector extends EstablishedCollector {
	
	public ConnectCollector(Handler handler, CommandsDescription description,
			Logger logger) {
		super(handler, description, logger);
	}
	@Override
	public void sendMessage(CommandID id, Message m) 
		throws IllegalStateException
	{
		super.sendMessage(id, m);
		if (m.getId()==CommandID.CONNECT)
			handler.setState(StateID.ESTABLISHED);
	}
	@Override
	public boolean isOneStep() {
		return true;
	}
}
