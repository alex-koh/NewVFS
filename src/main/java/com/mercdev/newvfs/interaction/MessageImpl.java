package com.mercdev.newvfs.interaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

class MessageImpl implements Message{
	private String message;
	private CommandID id=null;
	private Level level;
	private Object[] params;

	public void setMessage(String message) {
		this.message = message;
	}
	public void setID(CommandID id) {
		this.id = id;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public void setParams(Object[] params) {
		this.params = Arrays.copyOf(params, params.length);
	}
	@Override
	public CommandID getId() {
		return id;
	}
	@Override
	public Level getLevel() {
		return level;
	}
	@Override
	public String getMessage() {
		return message;
	}
	@Override
	public Object[] getParams() {
		return params;
	}
}
