package com.mercdev.newvfs.fs;

import com.mercdev.newvfs.server.Account;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/7/14
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Класс храни информации о блокировке файлов пользователями. Класс реализован
 * как одиночка.
 */
//TODO Надо всё делать синхронным
public class LockHandler {
	Map<File,List<Account>> fileMap;
	Map<Account,List<File>> connectionMap;
	public LockHandler() {
		fileMap = new HashMap<File,List<Account>>();
		connectionMap = new HashMap<Account, List<File>>();
	}

	/**
	 * Разблокировать все файлы, заблокированные данным пользователем.
	 * Применяется при удалении пользователя.
	 * @param c владелец файлов
	 * @throws IllegalStateException Если во время выполнения операции
	 * обнаружится несимметричность информации о блокировке, то будет
	 * сгенерированно данное исключение
	 */
	public void unlockAll(Account c) throws IllegalStateException {
		List<File> files = connectionMap.get(c);
		if (files!=null) {
			for(File f : files) {
				List<Account> conns = fileMap.get(f);
				if (conns!=null) {
					int index = conns.indexOf(c);
					if(index!=-1)
						conns.remove(index);
					else
						throw new IllegalStateException(
							"LockHandler error : asymmetric file lock occur");
				}

			}
			connectionMap.remove(c);
		}
	}

	/**
	 * Метод блокирует данный файл от имени данного пользователя
	 * @param f блокируемый файл
	 * @param c блокирующий пользователь
	 * @return Если файл удалось заблокировать, то возвращается true
	 * @throws IllegalStateException
	 * (см {@link #unlockAll(com.mercdev.myvfs.server.Account)})
	 * @throws NullPointerException Генерируется, если хотя бы одни из
	 * переданных параметров равен null.
	 */
	public boolean lock(File f, Account c)
			throws IllegalStateException,NullPointerException
	{
		if((f==null)||(c==null))
			throw new NullPointerException(
					"LockHandler error : null element detected");
		int result = 0;

		List<File> files = connectionMap.get(c);
		if (files==null) {
			// Пользователь ещё не заблокировал, ни одного файла
			files = new LinkedList<File>();
			connectionMap.put(c,files);
		}
		else
			if(files.contains(f))
				result=1;

		List<Account> conns = fileMap.get(f);
		if(conns==null) {
			// Ни один пользователь еще, не блокировал данный файл
			conns = new LinkedList<Account>();
			fileMap.put(f,conns);
		}
		else
			if(conns.contains(c))
				result+=2;

		switch (result) {
		case 0:
			// Блокировка прошла успешно
			// Данный файл заблокирован данным пользователем
			files.add(f);
			conns.add(c);
			return true;
		case 3:
			// этот файл уже блокирован этим пользователем
			return false;
		default:
			throw new IllegalStateException(
				"LockHandler error : asymmetric file lock occur");
		}
	}

	/**
	 * Метод разблокиреет файл от имени данного пользователя
	 * @param f блокированный файл
	 * @param c снимающий блокировку пользователь
	 * @return если файл разблокировался, то возвращает true
	 * @throws IllegalStateException
	 * (см {@link #unlockAll(com.mercdev.myvfs.server.Account)})
	 */

	public boolean unLock(File f, Account c)	throws IllegalStateException {
		int result = 0;
	    int fileIndex=-1;
		int connIndex=-1;

		List<File> files = connectionMap.get(c);
		if (files!=null) {
			fileIndex = files.indexOf(f);
			if(fileIndex!=-1)
				result=1;
		}

		List<Account> conns = fileMap.get(f);
		if(conns!=null) {
			connIndex = conns.indexOf(c);
			if(connIndex!=-1)
				result += 2;
		}

		switch (result) {
			case 0:
				// Этот пользователь не блокировал этот файл
				return false;
			case 3:
				conns.remove(connIndex);
				files.remove(fileIndex);
				// Блокировка успешно снята
				return true;
			default:
				throw new IllegalStateException("asymmetric file lock occur");
		}
	}

	/**
	 * Проверяет блокировку данного файла
	 * @param f проверяемый файл
	 * @return Если файл блокирован, то возвращает true
	 */
	public boolean isLock(File f) {
		return fileMap.containsKey(f);
	}

	/**
	 * Возвращает список пользователей, блокирующих даннный файл
	 * @param f проверяемый файл
	 * @return список пользователей (копия хранимого списка)
	 */
	public Iterable<String> getOwners(File f) {
		List<Account> conns = fileMap.get(f);
		List<String> result =
				new ArrayList<String>(conns!=null?conns.size():0);
		for(Account var : conns)
			result.add(var.getName());
		return result;
	}
}
