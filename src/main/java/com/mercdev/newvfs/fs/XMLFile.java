package com.mercdev.newvfs.fs;

import org.w3c.dom.Element;

public class XMLFile implements File{
	Element base; 
	public XMLFile(Element elemnt) {
		if (elemnt.getTagName().equals("file"))
			base = elemnt;
		else
			throw new IllegalArgumentException("expected tag's name == file");
	}
	@Override
	public void setName(String name) {
		base.setAttribute("name", name);
	}
	@Override
	public String getName() {
		return base.getAttribute("name");
	}
	@Override
	public boolean isFile() {
		return true;
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
