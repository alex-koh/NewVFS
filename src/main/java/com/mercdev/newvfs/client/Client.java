package com.mercdev.newvfs.client;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mercdev.newvfs.interaction.ClientSenderFactory;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.SenderFactory;

public class Client implements Runnable {
	private BufferedReader in;
	private Logger logger;
	private Handler handler;
	
	/**
	 * �������� � ��������� �������. 
	 * @param connection ������, ���������� �� ���������� � �������� � �� 
	 * �������� ������ �� ������
	 * @param inr ����� ������� �����
	 * @param settings ��������� ���������
	 * @param loggerName ��� ���������
	 */
	public Client(Handler handler, Reader inr, Logger logger) {
		if ((handler==null)||(inr==null)||(logger==null))
			throw new NullPointerException("exception.client.null");
		this.handler = handler;
		in = new BufferedReader(inr);
		this.logger = logger;
	}
	
	/**
	 * ����� ����������� � ��������� ������. ������ �� ������� ����� � ���������
	 * ����������� ������� �� �������. ������ ������� �� ����������� � �������.
	 */
	@Override
	public void run() {
		try {
			try {
				//�������������� ���������
				logger.info("message.client.hello"); //TODO message
				while (handler.isWork()) {
					//��������� ����� � �������� ������� �� �����������
					String command = in.readLine();
					try {
						handler.sendCommand(command);
					}
					catch (IllegalArgumentException exc) {
						logger.log(Level.WARNING, 
								exc.getMessage(), new Object[] {command});
					}
					catch (IllegalStateException exc) {
						logger.warning(exc.getMessage());					
					}
				}
				logger.info("message.client.goodbye"); //TODO message
			}
			finally {
				handler.close();
			}
		}
		catch(IOException exc) {
			// ������ �������� ������
			logger.warning("exception.client.work.io"); //TODO exception
		}
		catch (Exception exc) {
			logger.warning(exc.getMessage());
		}
	}

	private static Logger getLogger(Properties settings) {
		//TODO jast write it
		return Logger.getAnonymousLogger();
	}
	
	private static Properties defoultParams() {
		Properties defoultConfigs = new Properties();
		defoultConfigs.setProperty("connection.queue.timeout", "100");
		defoultConfigs.setProperty("connection.timeout", "100");
		defoultConfigs.setProperty("server.trials", "3");
		defoultConfigs.setProperty("server.port", "8033");
		defoultConfigs.setProperty("server.timeout", "100");
		defoultConfigs.setProperty("server.stream.timeout", "100");
		return defoultConfigs;
	}
	static public void main(String args[]) {
		String propertiesFileName = "client.properties";
		// ������� ����� ������
		Reader isr = new InputStreamReader(System.in);
		// ��������
		Logger logger = null; 
		// ��������� �� ���������
		Properties settings = new Properties(defoultParams());
		try {
			try {
				// ������ ���������� ���������, �������� �������������
				InputStream propertiesFile =
						Client.class.getResourceAsStream(propertiesFileName);
				settings.load(propertiesFile);
				propertiesFile.close();
			}
			finally {
				logger = getLogger(settings);
			}
		}
		catch (IOException exc) {
			// ������ ��� ������ �����, ������� �� ��������� ��-���������
			logger.log(Level.WARNING, 
				"exception.client.init.properties.file"); //TODO exception
		}
		int timeout = Integer.valueOf(
				settings.getProperty("connection.timeout"));
		
		CommandsFactory cFactory = null; //TODO
		
		// ������� ����������� ���������
		SenderFactory<Command, Message> factory = new ClientSenderFactory(
			Integer.valueOf(settings.getProperty("server.timeout")),
			Integer.valueOf(settings.getProperty("server.stream.timeout")),					
			Integer.valueOf(settings.getProperty("server.port")));
		
		// ���������� ����� �������� ������ ������� �� ������
		int clientTimeout=Integer.valueOf(
				settings.getProperty("connection.queue.timeout"));
		
		// ����������� �����
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
	}
}