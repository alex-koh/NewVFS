package com.mercdev.newvfs.interaction;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ������� ������������ ����������, ���������� �� ������� �������. �������
 * ����� ���������� �� ������� �������.
 * @author alex
 *
 */
public class ServerSenderFactory implements SenderFactory<Message,Command> {
	// ����������� ������� ��� �������� �������
	int streamTimeout;
	ServerSocket server;
	/**
	 * 
	 * @param port ����, �������������� ��������;
	 * @param streamTimeout ����� �������� ������ �� ������� ��� ��������
	 * ������;
	 * @throws IOException ������ ���������� � ������
	 */
	public ServerSenderFactory(int port, int streamTimeout) 
			throws IOException
	{
		this.streamTimeout = streamTimeout;
		server = new ServerSocket(port);
	}
	/**
	 * ������ �� ������
	 */
	@Override
	public void setSocketAddress(String host, int port)
			throws IllegalArgumentException {	}
	/**
	 * �������� ������ �����������
	 * @return ���������� ����� ���������� �� ���������� �������.
	 */
	@Override
	public Sender<Message,Command> getSender() throws IOException {
		try {
			Socket socket = server.accept();
			socket.setSoTimeout(streamTimeout);
			return new SenderImpl<Message, Command>(socket);
		}
		catch(IOException exc) {
			throw new IOException("exception.sender.connect");
		}
	}
	/**
	 * �������� ����������.
	 * @throws IOException 
	 */
	@Override
	public void close() throws IOException {
		server.close();
	}
}
