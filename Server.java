package my.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class Server {
	public static void main(String[] args) {
		new Server();
	}
	private int port = 154;
	private ServerSocket server = null;
	private int lastID = 0;
	private ArrayList<ClientWorker> clients = new ArrayList<>();
	private ArrayList<PlayRoom> rooms = new ArrayList<>();
	private JFrame frame = new JFrame("Server");
	private ClientWorker waiter = null;
	private boolean conectionListenerStopper = true;
//	private Thread conectionListener;
	
	public Server() {
		createJs();
		try {
			server = new ServerSocket(port);
		} catch (IOException ex) {
			ex.fillInStackTrace();
		}
		addConectionListener();
	}
	private void addConectionListener() {
		System.out.println("Server strarts");
		final Server thisServer = this;
		Thread conectionListener = new Thread(new Runnable() {
			@Override
			public void run() {
				while(conectionListenerStopper) {
					try {
						Socket socket = server.accept();
						int id = lastID ++;
						clients.add(new ClientWorker(id, socket, thisServer));
						System.out.println("Client connection success (id = " + id + ")");
					} catch(IOException ex) {
						ex.fillInStackTrace();
					}
				}
			}
		});
		conectionListener.start();
	}
	private void createJs() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 0);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	public void getRoom(ClientWorker w) {
		if(waiter == null) {
			waiter = w;
		} else {
			PlayRoom room = new PlayRoom(waiter, w, this);
			waiter.room = room;
			w.room = room;
			rooms.add(room);
			System.out.println("New room created (id_1 = " + waiter.id + " id_2 = " + w.id + ")");
			waiter = null;
		}
	}
	public void kill(ClientWorker w) {
		for(int i = 0; i < clients.size(); i++) {
			if(w.id == clients.get(i).id) {
				clients.remove(i);
				if(w.room != null) {
					w.room.stop(w);
				}
				System.out.println("Client disconnect success (id = " + w.id + ")");
				break;
			}
		}
	}
	public void kill(PlayRoom r) {
		rooms.remove(r);
		System.out.println("Room disabled (id_1 = " + r.playerA.id + ", id_2 = " + r.playerB.id + ")");
	}
	@Override
	protected void finalize() throws Throwable {
		conectionListenerStopper = false;
		super.finalize();
	}
}
