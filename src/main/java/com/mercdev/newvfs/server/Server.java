package com.mercdev.newvfs.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mercdev.newvfs.client.CommandsFactory;
import com.mercdev.newvfs.client.CommandLoader;
import com.mercdev.newvfs.client.ServerInterface;
import com.mercdev.newvfs.fs.FileSystem;
import com.mercdev.newvfs.fs.LockHandler;
import com.mercdev.newvfs.interaction.Sender;

public class Server implements Runnable {
	static public void main(String[] args) {
		//�������� �� ���������
		Properties defaultConfig = new Properties();
		defaultConfig.setProperty("server.port", "8033");
		defaultConfig.setProperty("server.threads", "4");
		defaultConfig.setProperty("server.timeout", "100");
		defaultConfig.setProperty("server.stream.timeout", "100");
		defaultConfig.setProperty("accounts.size", "20");
		defaultConfig.setProperty("accounts.threads", "4");
		defaultConfig.setProperty("dispatcher.timeout", "100");
		defaultConfig.setProperty("fs.home.name", "fs.xml");
		defaultConfig.setProperty("fs.timeout", "100");
		defaultConfig.setProperty("fs.separator", "\\");
		defaultConfig.setProperty("fs.root", "C:");
		defaultConfig.setProperty("fs.root.name", "root");
		defaultConfig.setProperty("command.list", "command.list.xml");
		// �������� ������������ ������������
		InputStream inProperties = 
				Server.class.getResourceAsStream("config.properties");
		Properties configs = new Properties(defaultConfig);
		try {
			// �������� ������������ 
			configs.load(inProperties);
			// ������ ������������� ���������� ������	
			LockHandler locks = new LockHandler();
			// �������� �������
			FileSystem fs = new FileSystem(locks, configs);
			// ���������, ������������� ���������� �����
			Dispatcher dispatcher = new Dispatcher(configs);
			// ������ ������������������ ���������
			AccountPool accounts = new AccountPool(locks, configs);
			// ��������� �������� ������
			CommandLoader loader = new CommandLoader(configs, log);
			// ��������� ������ � ���������
			CommandsFactory CI = new ServerInterface(loader, log);
			// �������� ������� �� �������� �����
			CommandExecutor factory = new CommandExecutor();
			// ������� �������� ��������� ������������
			Receiver receiver = new Receiver(fs, accounts, CI, factory);
			// �������� ���������� ������
			int port = Integer.valueOf(configs.getProperty("server.port"));
			int timeout = Integer.valueOf(configs.getProperty("server.timeout"));
			int soTimeout = Integer.valueOf(
					configs.getProperty("server.stream.timeout"));
			ServerSocketWrap sSocket 
				= new ServerSocketWrapImpl(port,timeout,soTimeout);
			// ������� ������
			Server server = new Server(receiver,dispatcher,configs,sSocket);
		}
		catch(IOException exc) {
			System.out.println(exc.getMessage());
		}
	}
	ServerSocketWrap serverSocket;
	Receiver receiver;
	int timeout;
	static final Logger log = Logger.getLogger("com.mercdev.newvfs.server");
	ExecutorService executor;
	Dispatcher dispatcher;
	int port;
	
	public Server(Receiver receiver, Dispatcher dispatcher, 
			Properties configs, ServerSocketWrap server) throws IOException {
		int nThreads = Integer.valueOf(configs.getProperty("server.threads"));
		this.receiver = receiver;
		this.dispatcher = dispatcher;
		executor = Executors.newFixedThreadPool(nThreads);
		serverSocket = server;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				try {
					serverSocket.connect();
					Sender socket = serverSocket.accept();
					Runnable action = receiver.getAction(socket);
					Future<?> task = executor.submit(action);
					dispatcher.sendTask(task);
				}
				finally {
					serverSocket.close();
				}
			}
			catch (IOException exc) {
				log.log(Level.FINER,"exception in main socket",exc);
			}
		}
	}
	//TODO
	private void closeOperation (){
		
	}
}