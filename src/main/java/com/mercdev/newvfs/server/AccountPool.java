package com.mercdev.newvfs.server;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import com.mercdev.newvfs.fs.LockHandler;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.Message;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/8/14
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountPool {
	private final LockHandler lockHandler;
	private Map<String, Account> accounts;
	private int maxSize;
	private Lock lock;
	
	public AccountPool(LockHandler lockHandler, int size) {
		lock = new ReentrantLock();
		maxSize = size;
		this.lockHandler = lockHandler;
	}
	/**
	 * ����� ���������� ������ ��� ����������.
	 * @return ����� ���������� �� ������ ������
	 */
	public int getSize() {
		return accounts.size();
	}
	
	/**
	 * ���������� ������� ������ ������������ �� ��� ����� 
	 * @param login  ��� ������������
	 * @return ������� ������ ����������� ��� null
	 */
	public Account getAccount(String login) {
		return accounts.get(login);
	}
	
	/**
	 * ������ ����� ������� ������ ��� ������������ � �������� ������.
	 * ��������� ������ ���� ������� ������� �� ����� �����������.
	 * ���������� ��������� � ������� ��� ������������.
	 * ���� ������ � ����� ������ ��� ��������� ��� ��������� ����������
	 * ����� �������, �� ����� ������ �� ��������� � ������������ ���������
	 * � �������
	 * @param login ��� ������������
	 * @param address ������� ����� ������������
	 * @return ��������� � ������� ��� ������������
	 */
	public Account newAccount(String login) throws IllegalArgumentException {
		LinkedList<String> logins = new LinkedList<String>();
		Account account;
		lock.lock();
		try {
			// �������� ����������� ������������
			if (accounts.containsKey(login))
				throw new IllegalArgumentException(
						"exception.accounts.new.exist");
			if (accounts.size()<=maxSize)
				throw new IllegalArgumentException(
						"exception.accounts.new.too.many");
			// ������ ������������ �������������
			logins.addAll(accounts.keySet());
			// ������� ����� �������
			account = new Account(login);
			// ��������� ������� � ������ ����������� �������������
			accounts.put(login, account);
		}
		finally {
			lock.unlock();
		}
		// � ������ �������� ���������� �����������
		// ������������ ��������� ���������
		MessageBuilder builder = MessageBuilder.getMessageBuilder();
		builder.setMessage("message.accounts.new.hello"); //TODO message
		builder.setId(CommandID.CONNECT);
		builder.setLevel(Level.INFO);
		
		logins.addFirst(login);
		logins.addFirst(Integer.toString(logins.size()));

		builder.setParams(logins);
		account.sendMessage(builder.getResult());
		return account;
	}
	
	/**
	 * �������� ������� ������ ������������ � �������� ������.
	 * @param login ��� ������������.
	 * @return ���� ������� �������� �������, �� ���������� true.
	 */
	public Account disconnect(String login) {
		Account account = null;
		lock.lock();
		try {
			account = accounts.remove(login);
		}
		finally {
			lock.unlock();
		}
		if (account!=null) {
			lockHandler.unlockAll(account);
		}
		return account;
	}

	/**
	 * ����������������� �������� ��������� �� ����� ������������
	 * @param owner ���������� ������������.
	 * @param message ���������.
	 */
	public void notifyAllUsers(Account owner, Message message)
	{
		lock.lock();
		try {
			for(Account a : accounts.values())
				if (!owner.equals(a))
					a.sendMessage(message);
		}
		finally {
			lock.unlock();
		}
	}
}
