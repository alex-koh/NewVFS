package com.mercdev.newvfs.fs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XMLFileMaker implements FileMaker{
	Document doc;
	File root;
	static final Logger log = Logger.getLogger("com.mercdev.newvfs.server");
	public XMLFileMaker(Properties configs) {
		try {
			FileInputStream fileIn 
				= new FileInputStream(configs.getProperty("fs.home"));
			try {
				DocumentBuilderFactory factory = 
						DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse(fileIn);
				root = new XMLDirectory(doc.getDocumentElement());
				root.setName(configs.getProperty("fs.root"));
			}
			finally {
				fileIn.close();
			}
		}
		catch(FileNotFoundException exc) {
			log.log(Level.FINER,"In FS-init: file not found",exc);
		}
		catch(ParserConfigurationException exc) {
			log.log(Level.FINER,"In FS-init: something with xml-builder",exc);
		}
		catch (SAXException exc) {
			log.log(Level.FINER,"In FS-init: something with xml-parser",exc);
		}
		catch (IOException exc) {
			log.log(Level.FINER,
					"In FS-init: exception when read fs from disc",exc);
		}

	}
	@Override
	public File newFile(String name) {
		Element fileElement = doc.createElement("file");
		fileElement.setAttribute("name", name);
		return new XMLFile(fileElement);
	}
	@Override
	public File newDirectory(String name) {
		Element fileElement = doc.createElement("dir");
		fileElement.setAttribute("name", name);
		return new XMLDirectory(fileElement);
	}
	@Override
	public File getRoot() {
		return root;
	}
}
