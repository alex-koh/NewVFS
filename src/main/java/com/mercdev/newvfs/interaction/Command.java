package com.mercdev.newvfs.interaction;

import java.io.Serializable;
import java.util.Collection;

public interface Command extends Serializable {
	/**
	 * ����� ���������� ������ �������
	 * @return ������ �������
	 */
	CommandID getID();
	/**
	 * ����� ���������� ������ ���������� �������
	 * @return ������ ���������� �������
	 */
	Collection<Path> getParams();
	/**
	 * ����� ���������� ��� ������������, ������������ �������
	 * @return ��� ������������
	 */
	String getLogin();
}