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
	 * ћетод возвращает размер пул соединений.
	 * @return число соединений на момент вызова
	 */
	public int getSize() {
		return accounts.size();
	}
	
	/**
	 * ¬озвращает учЄтную запись пользовател€ по его имени 
	 * @param login  им€ пользовател€
	 * @return учЄтна€ запись пользовате€ или null
	 */
	public Account getAccount(String login) {
		return accounts.get(login);
	}
	
	/**
	 * —оздаЄт новую учЄтную запись дл€ пользовател€ с заданным именем.
	 * Ѕлокирует работу пула учетных записей на врем€ регистрации.
	 * ¬озвращает сообщение с ответом дл€ пользовател€.
	 * ≈сли запись с таким именем уже существут или превышено предельное
	 * число записей, то нова€ запись не создаетс€ и возвращаетс€ сообщение
	 * с ошибкой
	 * @param login им€ пользовател€
	 * @param address текущий адрес пользовател€
	 * @return сообщение с ответом дл€ пользовател€
	 */
	public Account newAccount(String login) throws IllegalArgumentException {
		LinkedList<String> logins = new LinkedList<String>();
		Account account;
		lock.lock();
		try {
			// ѕроверка регистрации пользовател€
			if (accounts.containsKey(login))
				throw new IllegalArgumentException(
						"exception.accounts.new.exist");
			if (accounts.size()<=maxSize)
				throw new IllegalArgumentException(
						"exception.accounts.new.too.many");
			// —писок подключенных пользователей
			logins.addAll(accounts.keySet());
			// —оздает новый аккаунт
			account = new Account(login);
			// ƒобавл€ет аккаунт в список поключЄнных пользователей
			accounts.put(login, account);
		}
		finally {
			lock.unlock();
		}
		// ¬ случае удачного завершени€ регистрации
		// формирование ответного сообщени€
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
	 * ”даление учЄтной записи пользовател€ с заданным именем.
	 * @param login им€ пользовател€.
	 * @return если попытка удалени€ удалась, то возвращает true.
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
	 * Ўироковещательна€ рассылка сообщени€ от имени пользовател€
	 * @param owner вызывающий пользователь.
	 * @param message сообщение.
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
