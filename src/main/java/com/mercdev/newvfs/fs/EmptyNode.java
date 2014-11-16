package com.mercdev.newvfs.fs;

import com.mercdev.newvfs.server.Account;
/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 2/27/14
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyNode implements PathNode {
	private String name;
	private File parrent;
	public  EmptyNode (File parrent, String name) {
		// ≈динственный каталог у которого нет предка это корневой, а он
		// всегда существует
		if (parrent==null)
			throw new NullPointerException("parrent==null");
		this.parrent = parrent;
		this.name = name;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isExist() {
		return false;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isUnLock() {
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean lock(Account c) {
		return false;
	}

	@Override
	public boolean unlock(Account c) {
		return false;
	}

	@Override
	public PathNode find(String name) {
		return null;
	}

	@Override
	public boolean mkDir(FileMaker maker) {
		File newFile = maker.newDirectory(name);
		return parrent.addChild(newFile);
	}

	@Override
	public boolean mkFile(FileMaker maker) {
		File newFile = maker.newFile(name);
		return parrent.addChild(newFile);
	}

	@Override
	public boolean remove() {
		return false;
	}

	//TODO ¬озможно дописать специфический итератор
	@Override
	public Iterable<PathNode> getChildren() {
		return null;
	}
}
