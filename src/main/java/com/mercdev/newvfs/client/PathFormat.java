package com.mercdev.newvfs.client;

import java.util.Arrays;

import com.mercdev.newvfs.interaction.Path;

/**
 * Класс описывает строковое представление пути для данного клиента
 * @author alex
 *
 */
public class PathFormat {
	private String seporator;
	private String root;
	/**
	 * Создает новое описание пути.
	 * @param seporator разделитель, используемый в строковом представлении
	 * пути. 
	 * @param root имя корневого каталога.
	 */
	public PathFormat(String seporator, String root) {
		this.seporator = seporator;
		this.root = root;
	}
	/**
	 * Получает путь из строкового представления для данного клиента.
	 * @param path строковое представление
	 * @return путь.
	 */
	public Path stringToPath(String path) {
		String[] terms = path.split(seporator);
		if(terms[0].equals(root)) {
			return new Path(true, 
					Arrays.asList(terms).subList(1, terms.length-2));
		}
		else
			return new Path(false,Arrays.asList(terms));
	}
	/**
	 * Получает строковое представление пути для данного клиента.
	 * @param path путь.
	 * @return строковое представление пути.
	 */
	public String pathToString(Path path) {
		StringBuilder result = new StringBuilder();
		result.append(path.getBeginFromRoot()? "" : root);
		for(String t : path.getTerms()) {
			result.append(seporator);
			result.append(t);
		}
		return result.toString();
	}
}
