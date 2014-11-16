package com.mercdev.newvfs.fs;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 2/26/14
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * ����� ��������� ������������� ����� ��������� �� �������� �������.
 */
interface File {
	/**
	 * ���������� ��� �����
	 * @return ��� �����
	 */
	String getName();

	/**
	 * ����, ��������������, ��� ��� ����
	 * @return ���� ��� ����, �� ���������� true. ���� ����������, false.
	 */
	boolean isFile();
	/**
	 * ����� ��� �����
	 * @param name ����� ���
	 */
	void setName(String name);

	/**
	 * ������ ���� �������� ������� ����. �������� ������������ ��������
	 * ��������.
	 * @return ������������ ��� ���������� ��������. ���� ���� �������� ������
	 * �� ������������ null.
	 */
	Iterable<File> getChildren();

	/**
	 * �������� ���� � ������ ��������. (������������ ����� ������ �����������,
	 * ��� ���� ���������� � ��� ��� �� ����������� ����� ��������)
	 * @param f ����������� ����
	 */
	boolean addChild(File f);

	@Override
	String toString();

	@Override
	boolean equals(Object obj);
}
