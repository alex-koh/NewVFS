package com.mercdev.newvfs.test.sender;

import static  org.testng.Assert.*; 
import static  org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mercdev.newvfs.interaction.ClientSenderFactory;
import com.mercdev.newvfs.interaction.Command;
import com.mercdev.newvfs.interaction.CommandID;
import com.mercdev.newvfs.interaction.CommandImpl;
import com.mercdev.newvfs.interaction.Message;
import com.mercdev.newvfs.interaction.MessageImpl;
import com.mercdev.newvfs.interaction.Sender;
import com.mercdev.newvfs.interaction.SenderFactory;
import com.mercdev.newvfs.interaction.ServerSenderFactory;

public class TestSender {
	private int port=8034;
	final private int timeout = 100;
	final private int streamTimeout = 100;
	private SenderFactory<Message,Command> sFactory;
	private SenderFactory<Command,Message> cFactory;
	private ExecutorService executor;
	private Command[] commands = new Command[2];
	private Message[][] messages = {new Message[2],new Message[3]};

	private String login="login";
	private String host = "localhost";
	
	@BeforeMethod
	public void init() throws IOException {
		cFactory = new ClientSenderFactory(timeout, streamTimeout, port);
		cFactory.setSocketAddress(host, port);
		
		sFactory = new ServerSenderFactory(port, streamTimeout);
		executor = Executors.newFixedThreadPool(2);

		List<String> params = Collections.emptyList();
		CommandID[] cids = {CommandID.CONNECT, CommandID.QUIT};
		
		for(int i=0;i<2;i++) {
			CommandImpl c = new CommandImpl();
			c.setID(cids[i]);
			c.setLogin(login);
			c.setParam(params);
			commands[i] = c;
			Message[] mm = messages[i];
			for(int j=0;j<mm.length;j++) {
				MessageImpl m = new MessageImpl();
				m.setMessage("message"+(i+1)+(j+1));
				m.setLevel(Level.INFO);
				m.setID(CommandID.EMPTY);
				m.setParams(new Object[0]);
			}
		}
		//System.out.println(Arrays.toString(commands));
		//System.out.println(Arrays.deepToString(messages));
	}
	
	@Test(enabled=true, timeOut=timeout+streamTimeout)
	public void testFromClientToServer() 
		throws TimeoutException, ExecutionException, IOException, InterruptedException 
	{
		Future<Boolean> server = executor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int i=0;
				for(Message[] mm : messages) {
					Sender<Message,Command> sender = sFactory.getSender();
					try {					
						for(Command c :sender.readObjects()) {
							System.out.println("server receive [" + c+"]");
							assertEquals(c, commands[i]);
						}
						System.out.println("server send " + Arrays.toString(mm));
						sender.writeObjects(Arrays.asList(mm));
					}
					finally {
						System.out.println("server close");
						sender.close();
					}
					i++;
				}
				return true;
			}
		});
		Future<Boolean> client = executor.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int i=0;
				for (Command c : commands) {
					Sender<Command, Message> sender = cFactory.getSender();
					try {
						System.out.println("client send [" + c+"]");
						sender.writeObjects(Collections.singleton(c));
						int j=0;
						for (Message m : sender.readObjects()) {
							System.out.println("client receive [" + m+"]");
							assertEquals(m, messages[i][j]);
							j++;
						}
					}
					finally {
						System.out.println("client close");
						sender.close();
					}
					i++;
				}
				return true;
			}
		});
		try {
			assertTrue(client.get());
			assertTrue(server.get());
		}
		finally {
			client.cancel(true);
			server.cancel(true);
		}
	}

	@AfterMethod
	public void end() throws IOException {
		sFactory.close();
		cFactory.close();
	}
	
}
