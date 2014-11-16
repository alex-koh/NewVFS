package com.mercdev.newvfs.interaction;

import java.io.IOException;

/**
 * ������� �� ������������ ������������
 * @author alex
 *
 */
public interface SenderFactory<S,R> {
	/**
	 * ������ ����� ������ �� �������.
	 * @param host ����� �������;
	 * @param port ���� �� �������.
	 */
	void setSocketAddress(String host, int port) 
			throws IllegalArgumentException;
	/**
	 * ������� ����� ����������.
	 * @return ���������� ������� � ������ ����������.
	 */
	Sender<S,R> getSender() throws IOException;
	/**
	 * ��������� ������ � ��������
	 * @throws IOException ������ ��� ��������.
	 */
	void close() throws IOException;
}
