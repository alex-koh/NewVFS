package com.mercdev.newvfs.client;

import java.util.Arrays;

import com.mercdev.newvfs.interaction.Path;

/**
 * ����� ��������� ��������� ������������� ���� ��� ������� �������
 * @author alex
 *
 */
public class PathFormat {
	private String seporator;
	private String root;
	/**
	 * ������� ����� �������� ����.
	 * @param seporator �����������, ������������ � ��������� �������������
	 * ����. 
	 * @param root ��� ��������� ��������.
	 */
	public PathFormat(String seporator, String root) {
		this.seporator = seporator;
		this.root = root;
	}
	/**
	 * �������� ���� �� ���������� ������������� ��� ������� �������.
	 * @param path ��������� �������������
	 * @return ����.
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
	 * �������� ��������� ������������� ���� ��� ������� �������.
	 * @param path ����.
	 * @return ��������� ������������� ����.
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
