package com.mercdev.newvfs.interaction;

import java.io.IOException;

/**
 * ����� ��������� ������ �� �������� ������ ����� ��������
 * 
 * @author alex
 *
 */
public interface Sender <S,R> {
	/**
	 * �������� ������ �� ������.
	 * @param obj ������������ ������.
	 * @throws IOException ������ �������� ������
	 */
	void writeObjects(Iterable<S> objects) throws IOException;
	/**
	 * �������� ������ � �������.
	 * @return ���������� ������.
	 * @throws IOException ������ �������� ������.
	 */
	Iterable<R> readObjects() throws IOException;
	/**
	 * ��������� ����������
	 * @throws IOException
	 */
	void close() throws IOException;
}
