package com.mercdev.newvfs.server;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.mercdev.newvfs.fs.PathNode;
import com.mercdev.newvfs.interaction.Message;
/**
 * ������� ������ ������������. ������ ���������� �� ����� ������������,
 * ��� ������� �������� � ���������� ������������ ���.
 * 
 * @author alex
 *
 */
public class Account {
	private String login;
	private PathNode root;
	private BlockingQueue<Message> messages;
	/**
	 * ������� ����� ������� ������ ������������.
	 * @param login ��� ������������.
	 */
	public Account(String login) {
		this.login = login;
		messages = new LinkedBlockingQueue<Message>();
	}
	/**
	 * ���������� ��� ������������.
	 * @return ��� ������������.
	 */
	public String getName() {
		return login;
	}
	/**
	 * ���������� ������� ������� ������������.
	 * @return ������� ������������.
	 */
	public PathNode getRoot() {
		return root;
	}
	/**
	 * ������������� ������� ������� ������������.
	 * @param root ������� ������������.
	 */
	public void setRoot(PathNode root) {
		this.root = root;
	}
	/**
	 * �������� ��������� � �����.
	 * @param m ���������.
	 */
	public void sendMessage(Message m) {
		messages.offer(m);
	}
	/**
	 * �������� ��� ��������� ��������� � ��������� ���������.
	 * @param result ��������� ��������.
	 */
	public void getMessages(Collection<Message> result) {
		messages.drainTo(result);
	}
}
