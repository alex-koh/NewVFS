package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.CommandID;

public interface CommandsFactory {
	/**
	 * ������� ����� �������, ��������������� ��������� �������.
	 * @param id ������ �������;
	 * @param login ��� ������������;
	 * @return ��������� �������.
	 */
	CommandBuilder getCommandBuilder(CommandID id);
	/**
	 * ��������� ����� ������� ������.
	 * @param login ��� ������� ������.
	 */
	void setLogin(String login);
	/**
	 * �����������, ����������� � ������ ���� � �����.
	 * @return ������ ���� ��� ��� ������� ("/").
	 */
	String getPathSeporator();
}