package com.mercdev.newvfs.fs;

/**
 * Класс описывает фабрику, создающую файлы и каталоги файловой системы 
 * @author alex
 *
 */
public interface FileMaker {
	/**
	 * Метод создает новый файл
	 * @param name имя файла
	 * @return новый файл
	 */
	File newFile(String name);
	/**
	 * Метод создает новый каталог
	 * @param name имя каталога
	 * @return новый каталог
	 */
	File newDirectory(String name);
	/**
	 * Метод возвращает корневой каталог файловой системы
	 * @return корневой каталог
	 */
	File getRoot();
}