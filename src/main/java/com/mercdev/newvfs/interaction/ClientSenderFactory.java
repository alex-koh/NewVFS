package com.mercdev.newvfs.interaction;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSenderFactory implements SenderFactory<Command,Message> {
	private int timeout=0;
	private int streamTimeout=0;
	private int defaultPort;
	private SocketAddress address;
	/**
	 * Фабрика по производству передатчиков информации на клиентской стороне.
	 * @param timeout предел времи для подключения к серверу;
	 * @param streamTimeout ограничение времени на выполнение операции
	 * передачи данных;
	 * @param defaultPort порт по умалчанию.
	 */
	public ClientSenderFactory(
			int timeout, int streamTimeout,	int defaultPort) 
	{
		this.timeout = timeout;
		this.streamTimeout = streamTimeout;
		this.defaultPort = defaultPort;
	}
	/**
	 * Устанавливает адрес сервера и порт.
	 * @param host адрес сервера;
	 * @param port порт на сервере.
	 */
	@Override
	public void setSocketAddress(String host, int port)
		throws  IllegalArgumentException
	{
		if((port<0)||(port>65535))
			port = defaultPort;
		if((host!=null)&&(host!="")) {
			InetSocketAddress address = new InetSocketAddress(host, port);
			if(!address.isUnresolved()) {
				this.address = address;
				return;
			}
		}
		throw new IllegalArgumentException(
				"exception.sender.address.not.found"); //TODO exception
	}
	/**
	 * Создание объекта передатчика
	 * @return возвращает готовый к работе передатчик.
	 * @throws IllegalArgumentException если не удалось найти ресурс с
	 * заданным адресом;
	 * @throws IOException если возникли проблемы при подключение.
	 */	
	@Override
	public Sender<Command, Message> getSender() throws IOException {
		Socket socket = new Socket();
		try {
			socket.connect(address, timeout);
			socket.setSoTimeout(streamTimeout);
		}
		catch(IOException exc) {
			throw new IOException("exception.sender.connect"); //TODO exception
		}
		if (socket.isConnected())
			return new SenderImpl<Command,Message>(socket);
		else
			throw new IOException("exception.socket.disconnected"); //TODO exception
	}
	/**
	 * Ничего не делает
	 */
	@Override
	public void close() throws IOException {
	
	}
}
