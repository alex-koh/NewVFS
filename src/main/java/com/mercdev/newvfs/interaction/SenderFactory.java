package com.mercdev.newvfs.interaction;

import java.io.IOException;

/**
 * Фабрика по производству передатчиков
 * @author alex
 *
 */
public interface SenderFactory<S,R> {
	/**
	 * Задать адрес сокета на сервере.
	 * @param host адрес сервера;
	 * @param port порт на сервере.
	 */
	void setSocketAddress(String host, int port) 
			throws IllegalArgumentException;
	/**
	 * Создать новый передатчик.
	 * @return возвращает котовый к работе передатчик.
	 */
	Sender<S,R> getSender() throws IOException;
	/**
	 * Окончание работы с фабрикой
	 * @throws IOException ошибка при закрытие.
	 */
	void close() throws IOException;
}
