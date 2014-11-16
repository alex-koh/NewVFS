package com.mercdev.newvfs.server;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

public class MessageBuilder {
	private MessageImpl message;
	private List<String> params;
	class MessageImpl implements Message {
		private String message;
		private CommandID id;
		private Object[] params;
		private Level level;

		public MessageImpl() { }
		@Override
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		@Override
		public CommandID getId() {
			return id;
		}
		public void setId(CommandID id) {
			this.id = id;
		}
		@Override
		public Object[] getParams() {
			return params;
		}
		public void setParams(Object[] params) {
			this.params = params;
		}
		@Override
		public Level getLevel() {
			return level;
		}
		public void setLevel(Level level) {
			this.level = level;
		}
		@Override
		public String toString() {
			StringBuilder out = new StringBuilder();
			out.append(id);
			out.append(" ");
			out.append(level);
			out.append(" ");
			out.append(message);
			out.append(" ");
			out.append(Arrays.toString(params));

			return out.toString();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Message) {
				Message m = (Message) obj;
				return id.equals(m.getId())
						&&message.equals(m.getMessage())
						&&level.equals(m.getLevel())
						&&Arrays.equals(params, m.getParams());
			}
			return false;
		}
	}
	protected MessageBuilder() {
		message = new MessageImpl();
		params = new LinkedList<String>();
	}
	public static MessageBuilder getMessageBuilder() {
		return new MessageBuilder();
	}
	/**
	 * ������������� ������ ���������. 
	 * @param message ������ ��������� (�� ��������� message.server.unknown). 
	 */
	public void setMessage(String message) {
		this.message.setMessage(message);
	}
	/**
	 * ������������� ������ �������, � ������� ������� ���������. 
	 * @param id ������ ������� (�� ��������� {@link CommandID#EMPTY}).
	 */
	public void setId(CommandID id) {
		this.message.setId(id);
	}
	/**
	 * ����� ��������� ������ ���������� ��������� �� ������. ���������, 
	 * �������� � ���� ������ �� ����������� � ����������, �������� � ������
	 * {@link #setParams(Collection)}. ��� ��� ������ ������������� ��� 
	 * ������������� � ������ �������.
	 * @param param ������ ���������.
	 */
	public void addParam(String param) {
		params.add(param);
	}
	/**
	 * ������ ��������� ��������� ����� �������. ���� ������ ���� �����, ��
	 * � ��������� ����� �������������� ������ ��� ���������. ���������
	 * ����� ������ �������� � �� ������.
	 * @param params ��������� ���������
	 */
	public void setParams(Collection<String> params) {
		this.message.setParams(params.toArray());
	}
	/**
	 * ������������� ������� �������� ���������
	 * @param level ������� �������� (�� ��������� {@link Level#INFO}).
	 */
	public void setLevel(Level level) {
		this.message.setLevel(level);
	}
	/**
	 * ���������� ����� ������� ���������.
	 * @return �������� ���������
	 */
	public Message getResult() {
		if(message.getId()==null)
			message.setId(CommandID.EMPTY);
		if(message.getParams()==null)
			message.setParams(params.toArray());
		if(message.getLevel()==null)
			message.setLevel(Level.INFO);
		if(message.getMessage()==null)
			message.setMessage("message.server.unknown"); //TODO message
		return message;
	}
}
