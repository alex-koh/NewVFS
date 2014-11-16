package com.mercdev.newvfs.fs;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/8/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileMakerImpl implements FileMaker {
	File root;
	public FileMakerImpl(String rootName) {
		root = new Directory(rootName);
	}

	@Override
	public File newFile(String name) {
		return new FileImpl(name);
	}

	@Override
	public File newDirectory(String name) {
		return new Directory(name);
	}
	@Override
	public File getRoot() {
		return root;
	}
}
