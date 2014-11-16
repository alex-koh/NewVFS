package com.mercdev.newvfs.fs;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/5/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileImpl implements File {
	String name;
	public FileImpl() {
		this.name="noname"+System.currentTimeMillis();
	}
	public FileImpl(String name) {
		this.name = name;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}

	@Override
	public boolean addChild(File f) {
		return false;
	}

	@Override
	public Iterable<File> getChildren() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof File) {
			File f = (File) obj;
			if(getName().equals(f.getName()))
				return true;
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "File : "+getName();
	}
}
