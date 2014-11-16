package com.mercdev.newvfs.fs;

import java.util.Iterator;

import org.w3c.dom.Node;

import org.w3c.dom.Element;

public class XMLDirectory extends XMLFile{
	//Element base;
	public XMLDirectory(Element element) {
		super(element);
	}
	@Override
	public boolean addChild(File f) {
		if (f instanceof XMLFile) {
			XMLFile xmlF = (XMLFile) f;
			base.appendChild(xmlF.base);
			return true;
		}
		return false;
	}
	@Override
	public boolean isFile() {
		return false;
	}

	/**
	 * Возвращает список содержимого в папке
	 */
	@Override
	public Iterable<File> getChildren() {
		Node node = base.getFirstChild();
		final Element child = (Element) node;
		return new Iterable<File>() {
			@Override
			public Iterator<File> iterator() {
				return new Iterator<File>() {
					Element last=child;
					@Override
					public void remove() {
						if(!hasNext()) 
							throw new IllegalStateException(
								"iterator closed");
						base.removeChild(last);
						last=null;
					}
					@Override
					public File next() {
						if(!hasNext()) 
							throw new IllegalStateException(
								"iterator closed");
						File file = new XMLFile(last);
						Node node = last.getNextSibling();
						if (node instanceof Element)
							last = (Element) node;
						return file;
					}						
					@Override
					public boolean hasNext() {
						return last!=null;
					}
				};
			}
		};
	}

	@Override
	public String toString() {
		return "Directory : "+getName();
	}
}
