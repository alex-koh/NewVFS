package com.mercdev.newvfs.interaction;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Фабрика передатчиков информации, работающая на стороне сервера. Создает
 * новый передатчик по запросу клиента.
 * @author alex
 *
 */
public class ServerSenderFactory implements SenderFactory<Message,Command> {
	// ограничение времени для опираций доступа
	int streamTimeout;
	ServerSocket server;
	/**
	 * 
	 * @param port порт, прослушиваемый сервером;
	 * @param streamTimeout время ожидания ответа от клиента при передаче
	 * данных;
	 * @throws IOException ошибка соединения с портом
	 */
	public ServerSenderFactory(int port, int streamTimeout) 
			throws IOException
	{
		this.streamTimeout = streamTimeout;
		server = new ServerSocket(port);
	}
	/**
	 * Ничего не делает
	 */
	@Override
	public void setSocketAddress(String host, int port)
			throws IllegalArgumentException {	}
	/**
	 * Создание нового передатчика
	 * @return возвращает новый передатчик по требованию клиента.
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
	 * Закрытие соединения.
	 * @throws IOException 
	 */
	@Override
	public void close() throws IOException {
		server.close();
	}
}
