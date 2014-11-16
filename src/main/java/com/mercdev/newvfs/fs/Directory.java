package com.mercdev.newvfs.fs;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/5/14
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Directory extends FileImpl {
	List<File> children;

	public Directory(String name) {
		super(name);
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public Iterable<File> getChildren() {
		return children;
	}

	@Override
	public boolean addChild(File f) {
		children.add(f);
		return true;
	}

	@Override
	public String toString() {
		return "Directory : "+name;
	}
}
