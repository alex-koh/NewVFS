package com.mercdev.newvfs.fs;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 2/26/14
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Класс описывает представление файла зависящее от файловой системы.
 */
interface File {
	/**
	 * Возвращает имя файла
	 * @return имя файла
	 */
	String getName();

	/**
	 * Флаг, подтверждающий, что это файл
	 * @return Если это файл, то возвращает true. Если директория, false.
	 */
	boolean isFile();
	/**
	 * Задаёт имя файла
	 * @param name новое имя
	 */
	void setName(String name);

	/**
	 * Список всех потомков данного узла. Итератор поддерживает операцию
	 * удаления.
	 * @return Перечислимый тип содержащий потомков. Если узел является файлом
	 * то возвращается null.
	 */
	Iterable<File> getChildren();

	/**
	 * Добавить файл в список потомков. (пользователь этого метода гарантирует,
	 * что файл существует и его имя не встречается среди потомков)
	 * @param f добавляемый файл
	 */
	boolean addChild(File f);

	@Override
	String toString();

	@Override
	boolean equals(Object obj);
}
