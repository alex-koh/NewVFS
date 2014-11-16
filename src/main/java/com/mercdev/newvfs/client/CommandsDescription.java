package com.mercdev.newvfs.client;

import com.mercdev.newvfs.interaction.CommandID;

public interface CommandsDescription {
	/**
	 * ���������� ��� ������� �� � �������.
	 * @param id ������.
	 * @return ��� �������
	 */
	String getName(CommandID id);
	/**
	 * ���������� ������ ������� �� � �����.
	 * @param name ������ ����� �������;
	 * @return ������ ������� ��� null, ���� ������� � ����� ������ �� 
	 * ����������.
	 */
	CommandID getID(String name);
	/**
	 * ���������� �������� ����.
	 * @return �������� ����.
	 */
	PathFormat getPathFormat();
}
