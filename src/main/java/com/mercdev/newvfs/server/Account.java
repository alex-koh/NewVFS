package com.mercdev.newvfs.server;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.mercdev.newvfs.fs.PathNode;
import com.mercdev.newvfs.interaction.Message;
/**
 * Учетная запись пользователя. Хранит информацию об имени пользователя,
 * его текущем каталоге и сообщениях отправленных ему.
 * 
 * @author alex
 *
 */
public class Account {
	private String login;
	private PathNode root;
	private BlockingQueue<Message> messages;
	/**
	 * Создает новую учетную запись пользователя.
	 * @param login имя пользователя.
	 */
	public Account(String login) {
		this.login = login;
		messages = new LinkedBlockingQueue<Message>();
	}
	/**
	 * Возвращает имя пользователя.
	 * @return имя пользователя.
	 */
	public String getName() {
		return login;
	}
	/**
	 * Возвращает текущий каталог пользователя.
	 * @return каталог пользователя.
	 */
	public PathNode getRoot() {
		return root;
	}
	/**
	 * Устанавливает текущий каталог пользователя.
	 * @param root каталог пользователя.
	 */
	public void setRoot(PathNode root) {
		this.root = root;
	}
	/**
	 * Помещает сообщение в буфер.
	 * @param m сообщение.
	 */
	public void sendMessage(Message m) {
		messages.offer(m);
	}
	/**
	 * Копирует все доступные сообщения в задданную коллекцию.
	 * @param result коллекция приемник.
	 */
	public void getMessages(Collection<Message> result) {
		messages.drainTo(result);
	}
}
