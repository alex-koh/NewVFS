package com.mercdev.newvfs.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ������� ������ ��������� � ������������� ������ � ������ � ��������� ������.
 * ����� ������������� ������ ������� �������� ����������
 */
public class CommandLoader {
	private String seporator;
	private String root;
	
	List<CommandBuilder> commands;
	
	public void generate() {
		try {
			Map<String,String> properties = new HashMap<String, String>();
			properties.put("fs.root", root);
			properties.put("fs.seporator", seporator);
			InputStream input = getClass().getResourceAsStream(
					configs.getProperty("commands.list"));
			if (input==null)
				throw new 
					FileNotFoundException("In CommandLoader-init: file not found");
			try {
				// �������, ��������� ������ ���������
				DocumentBuilderFactory factory = 
						DocumentBuilderFactory.newInstance();
				// ������� ���������� ������������ ����
				factory.setNamespaceAware(true);

				SchemaFactory schemaFactory = SchemaFactory.newInstance(
						XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(getClass().getResource(
								configs.getProperty("commands.list.schema")));
				factory.setSchema(schema);
				// ������� ������ ������������ �������� � ���������
				factory.setIgnoringElementContentWhitespace(true);
				// ������ ���������
				DocumentBuilder builder = factory.newDocumentBuilder();
				// ������ ���������
				Document doc = builder.parse(input);
				// �������� ���� ���������
				Element root = doc.getDocumentElement();
				// ������� size ���������� ���������� �������
				int size = Integer.valueOf(root.getAttribute("size"));
				// �������� ������ ������
				commands = new ArrayList<CommandBuilder>(size);
				// ������ ���� ������� � ���������
				NodeList nodes =  root.getChildNodes();
				for(int i=0;i<nodes.getLength();i++) {
					// ������ ����������
					Map<String,String> params = new HashMap<String, String>();
					// ������� �������� �������
					List<String> description = new LinkedList<String>();
					// ��������� �������� �������
					NodeList terms = nodes.item(i).getChildNodes();
					// �������� � ������ ����������
					for(int j=0; j<terms.getLength()-1; j++) {
						params.put(terms.item(j).getNodeName(), 
								terms.item(j).getTextContent());
					}
					// ��������� ������� ��������
					NodeList lines = nodes.item(i).getLastChild().getChildNodes();
					// ������ ����� ��������
					for(int j=0; j<lines.getLength(); j++) {
						// ����� ������ ��������
						terms = lines.item(j).getChildNodes();
						// ���� ������ ������� �� ���������� ������
						if (terms.getLength()>1) {
							StringBuilder line = new StringBuilder();
							for (int k=0;k<terms.getLength();k++) {
								// �������� ������� ������ � ������ ������ ��
								// �������� ��������
								String t = properties.get(
										terms.item(k).getNodeName());
								if (t!=null)
									line.append(t);
								// ���� �������� �� �������
								else
									line.append(terms.item(k).getTextContent());
							}
							// ��������� ������ � ��������
							description.add(line.toString());
							
						}
						// ���� ������ �������
						else
							description.add(lines.item(j).getTextContent());
					}
					// �������� ������ �������� �������
					CommandDescriptionImpl descript 
						= new CommandDescriptionImpl(params, description);
					commands.add(descript);
				}
				commands = Collections.unmodifiableList(commands);
			}
			finally {
				input.close();
			}
		}
		catch(ParserConfigurationException exc) {
			log.log(Level.INFO,
				"In CommandLoader-init: something with xml-builder",exc);
		}
		catch (SAXException exc) {
			log.log(Level.INFO,
				"In CommandLoader-init: something with xml-parser",exc);
		}
		catch (IOException exc) {
			log.log(Level.INFO,
				"In CommandLoader-init: exception when read fs from disc",exc);
		}
	}
	
	public List<CommandBuilder> getCommandBuildersList() {
		return commands;
	}
	public PathFormat getPathFormat() {
		
	}
	public CommandsDescription getCommandDescription() {
		
	}
}
