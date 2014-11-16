package com.mercdev.newvfs.interaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class SenderImpl<S,R> implements Sender<S,R> {
	private Socket socket;
	
	public SenderImpl(Socket socket) {
		this.socket = socket;
	}
	/**
	 * ���������� �� ������ ����� ��������.
	 * @param objects ������������ �����.
	 * @throws IOException ������ �������� ������.
	 */
	@Override
	public void writeObjects(Iterable<S> objects) throws IOException {
		try {
			ObjectOutputStream out =
					new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(objects);
			out.flush();
			socket.shutdownOutput();
		}
		catch(IOException exc) {
			throw new IOException("exception.sender.write"); //TODO exception
		}
	}
	/**
	 * �������� ����� �������� �� �������
	 * @return ������ ��������
	 * @throws IOException ������ �������� ������.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterable<R> readObjects() throws IOException {
		try {
			ObjectInputStream in =
						new ObjectInputStream(socket.getInputStream());
			Iterable<R> receipt = (Iterable<R>) in.readObject();
			socket.shutdownInput();
			return receipt;
		}
		catch (ClassNotFoundException exc) {
			throw new IOException("exception.sender.class.not.found"); //TODO exception
		}
		catch (ClassCastException exc) {
			throw new IOException("exception.sender.class.cast"); //TODO exception
		}
		catch(IOException exc) {
			throw new IOException("exception.sender.read"); //TODO exception
		}				
	}
	/**
	 * ��������� ����������
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		socket.close();
	}
}