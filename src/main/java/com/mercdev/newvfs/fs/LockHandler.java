package com.mercdev.newvfs.fs;

import com.mercdev.newvfs.server.Account;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/7/14
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * ����� ����� ���������� � ���������� ������ ��������������. ����� ����������
 * ��� ��������.
 */
//TODO ���� �� ������ ����������
public class LockHandler {
	Map<File,List<Account>> fileMap;
	Map<Account,List<File>> connectionMap;
	public LockHandler() {
		fileMap = new HashMap<File,List<Account>>();
		connectionMap = new HashMap<Account, List<File>>();
	}

	/**
	 * �������������� ��� �����, ��������������� ������ �������������.
	 * ����������� ��� �������� ������������.
	 * @param c �������� ������
	 * @throws IllegalStateException ���� �� ����� ���������� ��������
	 * ����������� ���������������� ���������� � ����������, �� �����
	 * �������������� ������ ����������
	 */
	public void unlockAll(Account c) throws IllegalStateException {
		List<File> files = connectionMap.get(c);
		if (files!=null) {
			for(File f : files) {
				List<Account> conns = fileMap.get(f);
				if (conns!=null) {
					int index = conns.indexOf(c);
					if(index!=-1)
						conns.remove(index);
					else
						throw new IllegalStateException(
							"LockHandler error : asymmetric file lock occur");
				}

			}
			connectionMap.remove(c);
		}
	}

	/**
	 * ����� ��������� ������ ���� �� ����� ������� ������������
	 * @param f ����������� ����
	 * @param c ����������� ������������
	 * @return ���� ���� ������� �������������, �� ������������ true
	 * @throws IllegalStateException
	 * (�� {@link #unlockAll(com.mercdev.myvfs.server.Account)})
	 * @throws NullPointerException ������������, ���� ���� �� ���� ��
	 * ���������� ���������� ����� null.
	 */
	public boolean lock(File f, Account c)
			throws IllegalStateException,NullPointerException
	{
		if((f==null)||(c==null))
			throw new NullPointerException(
					"LockHandler error : null element detected");
		int result = 0;

		List<File> files = connectionMap.get(c);
		if (files==null) {
			// ������������ ��� �� ������������, �� ������ �����
			files = new LinkedList<File>();
			connectionMap.put(c,files);
		}
		else
			if(files.contains(f))
				result=1;

		List<Account> conns = fileMap.get(f);
		if(conns==null) {
			// �� ���� ������������ ���, �� ���������� ������ ����
			conns = new LinkedList<Account>();
			fileMap.put(f,conns);
		}
		else
			if(conns.contains(c))
				result+=2;

		switch (result) {
		case 0:
			// ���������� ������ �������
			// ������ ���� ������������ ������ �������������
			files.add(f);
			conns.add(c);
			return true;
		case 3:
			// ���� ���� ��� ���������� ���� �������������
			return false;
		default:
			throw new IllegalStateException(
				"LockHandler error : asymmetric file lock occur");
		}
	}

	/**
	 * ����� ������������ ���� �� ����� ������� ������������
	 * @param f ������������� ����
	 * @param c ��������� ���������� ������������
	 * @return ���� ���� ���������������, �� ���������� true
	 * @throws IllegalStateException
	 * (�� {@link #unlockAll(com.mercdev.myvfs.server.Account)})
	 */

	public boolean unLock(File f, Account c)	throws IllegalStateException {
		int result = 0;
	    int fileIndex=-1;
		int connIndex=-1;

		List<File> files = connectionMap.get(c);
		if (files!=null) {
			fileIndex = files.indexOf(f);
			if(fileIndex!=-1)
				result=1;
		}

		List<Account> conns = fileMap.get(f);
		if(conns!=null) {
			connIndex = conns.indexOf(c);
			if(connIndex!=-1)
				result += 2;
		}

		switch (result) {
			case 0:
				// ���� ������������ �� ���������� ���� ����
				return false;
			case 3:
				conns.remove(connIndex);
				files.remove(fileIndex);
				// ���������� ������� �����
				return true;
			default:
				throw new IllegalStateException("asymmetric file lock occur");
		}
	}

	/**
	 * ��������� ���������� ������� �����
	 * @param f ����������� ����
	 * @return ���� ���� ����������, �� ���������� true
	 */
	public boolean isLock(File f) {
		return fileMap.containsKey(f);
	}

	/**
	 * ���������� ������ �������������, ����������� ������� ����
	 * @param f ����������� ����
	 * @return ������ ������������� (����� ��������� ������)
	 */
	public Iterable<String> getOwners(File f) {
		List<Account> conns = fileMap.get(f);
		List<String> result =
				new ArrayList<String>(conns!=null?conns.size():0);
		for(Account var : conns)
			result.add(var.getName());
		return result;
	}
}
