package com.mercdev.newvfs.fs;

/**
 * ����� ��������� �������, ��������� ����� � �������� �������� ������� 
 * @author alex
 *
 */
public interface FileMaker {
	/**
	 * ����� ������� ����� ����
	 * @param name ��� �����
	 * @return ����� ����
	 */
	File newFile(String name);
	/**
	 * ����� ������� ����� �������
	 * @param name ��� ��������
	 * @return ����� �������
	 */
	File newDirectory(String name);
	/**
	 * ����� ���������� �������� ������� �������� �������
	 * @return �������� �������
	 */
	File getRoot();
}