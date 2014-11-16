package com.mercdev.newvfs.fs;

import java.util.Properties;
//TODO включить конструирующие методы

import com.mercdev.newvfs.server.Account;

/**
 * Класс предоставляет базовые методы по работе с файловой системой, такие как:
 * перемещение по каталогам и последовательный запуск задач на выполнение. 
 * 
 * @author alex
 *
 */
public class FileSystem {
	private File root;
	private PathNode rootNode;
	private Account rootUser;
	private FileMaker maker;
	private String separator;

	public FileSystem(LockHandler locks, Properties configs) {
		//TODO Обработка ошибок
		separator = configs.getProperty("fs.separator");
		String rootName = configs.getProperty("fs.root.name");
		maker = new XMLFileMaker(configs);
		root = maker.getRoot();
		rootNode = new ExistNode(null,root,locks);
		rootUser = new Account(rootName);
		rootNode.lock(rootUser);
	}
	/**
	 * Разделитель, используемый в файловой системе.
	 * @return строка с символом разделителя.
	 */
	public String getSeparator() {
		return separator;
	}
	/**
	 * Метод создаёт объект описывающий путь к целевому файлу.  
	 * @param root начальная директория поиска пути
	 * @param path строковая запись пути
	 * @return возвращает объект пути или null, если не удалось найти более 
	 * одного узла пути
	 * @throws NullPointerException если начальный каталог не задан и 
	 * требуется найти  относительный путь
	 */
	public PathNode getPath(PathNode root, String path) 
			throws NullPointerException
	{
		String[] terms = path.split(getSeparator());
		if(terms[0].equals(rootNode.getName()))
			root=rootNode;
		if (root==null)
			throw new NullPointerException("exception.fs.path.root.is.null"); // TODO exception
		for (String term : terms)
			if(root.isExist())
				root = root.find(term);
			else
				return null;
		return root;
	}
}