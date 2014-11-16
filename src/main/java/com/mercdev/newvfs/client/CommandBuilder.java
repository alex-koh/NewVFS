package com.mercdev.newvfs.client;


import java.util.Collection;

import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;

public interface CommandBuilder {
	/**
	 * ���������� ������ �������, ������� ��������� � ������ �������� ������.
	 * @return ������ �������.
	 */
	CommandID getCommandID();
	/**
	 * ������������� ����� ��� ������ �������.
	 * @param login ����� �������� �����������.
	 */
	void setLogin(String login);
	/**
	 * ��������� ��������� � ������� ���������� �������
	 * @param param ��������� ������.
	 */
	void setParams(Collection<String> param);
	/**
	 * ���������� ������� �������.
	 * @return ������� �������, ������� ���������� �� ������; 
	 * @throws IllegalArgumentException ����������, ���� ��������� ������ 
	 * �� ����� ��� �� � ������ ������.
	 */
	Command getResult() throws IllegalArgumentException;
}