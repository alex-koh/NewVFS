package com.mercdev.newvfs.fs;

import java.util.Properties;
//TODO �������� �������������� ������

import com.mercdev.newvfs.server.Account;

/**
 * ����� ������������� ������� ������ �� ������ � �������� ��������, ����� ���:
 * ����������� �� ��������� � ���������������� ������ ����� �� ����������. 
 * 
 * @author alex
 *
 */
public class FileSystem {
	private File root;
	private PathNode rootNode;
	private Account rootUser;
	private FileMaker maker;
	private String separator;

	public FileSystem(LockHandler locks, Properties configs) {
		//TODO ��������� ������
		separator = configs.getProperty("fs.separator");
		String rootName = configs.getProperty("fs.root.name");
		maker = new XMLFileMaker(configs);
		root = maker.getRoot();
		rootNode = new ExistNode(null,root,locks);
		rootUser = new Account(rootName);
		rootNode.lock(rootUser);
	}
	/**
	 * �����������, ������������ � �������� �������.
	 * @return ������ � �������� �����������.
	 */
	public String getSeparator() {
		return separator;
	}
	/**
	 * ����� ������ ������ ����������� ���� � �������� �����.  
	 * @param root ��������� ���������� ������ ����
	 * @param path ��������� ������ ����
	 * @return ���������� ������ ���� ��� null, ���� �� ������� ����� ����� 
	 * ������ ���� ����
	 * @throws NullPointerException ���� ��������� ������� �� ����� � 
	 * ��������� �����  ������������� ����
	 */
	public PathNode getPath(PathNode root, String path) 
			throws NullPointerException
	{
		String[] terms = path.split(getSeparator());
		if(terms[0].equals(rootNode.getName()))
			root=rootNode;
		if (root==null)
			throw new NullPointerException("exception.fs.path.root.is.null"); // TODO exception
		for (String term : terms)
			if(root.isExist())
				root = root.find(term);
			else
				return null;
		return root;
	}
}