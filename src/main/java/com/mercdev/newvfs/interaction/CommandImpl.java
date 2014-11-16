package com.mercdev.newvfs.interaction;

import java.util.ArrayList;
import java.util.Collection;

public class CommandImpl implements Command { //TODO удалить
	private CommandID id;
	private String login;
	private Collection<String> params;
	public CommandImpl() {
		
	}
	public void setID(CommandID id) {
		this.id =id;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public void setParam(Collection<String> params) {
		this.params = new ArrayList<String>(params);
	}
	@Override
	public CommandID getID() {
		return id;
	}

	@Override
	public Collection<String> getParams() {
		return params;
	}

	@Override
	public String getLogin() {
		return login;
	}
	@Override
	public String toString() {
		return ""+id+" "+login+" "+params;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Command) {
			Command c = (Command) obj;
			return id.equals(c.getID())
					&&login.equals(c.getLogin())
					&&params.equals(c.getParams());
		}
		return false;
	}
}
