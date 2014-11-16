package com.mercdev.newvfs.interaction;

import java.io.IOException;

/**
 * Класс описывает работу по передачи данных ввиде объектов
 * 
 * @author alex
 *
 */
public interface Sender <S,R> {
	/**
	 * Передает объект на сервер.
	 * @param obj передаваемый объект.
	 * @throws IOException ошибка передачи данных
	 */
	void writeObjects(Iterable<S> objects) throws IOException;
	/**
	 * Получает объект с сервера.
	 * @return получаемый объект.
	 * @throws IOException ошибка передачи данных.
	 */
	Iterable<R> readObjects() throws IOException;
	/**
	 * Закрывает соединение
	 * @throws IOException
	 */
	void close() throws IOException;
}
