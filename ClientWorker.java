package my.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientWorker {
	public final Socket socket;
	public final int id;
	public DataInputStream input;
	public DataOutputStream out;
	public final Server server;
	public PlayRoom room;
	private ArrayList<ClientListener> listeners = new ArrayList<>();
	private boolean listenerStopper = true;
	public ClientWorker(int id, Socket socket, Server server) {
		this.socket = socket;
		this.id = id;
		try {
			this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));//input;
			this.out = new DataOutputStream(socket.getOutputStream());//out;
		}
		catch (IOException ex) {
			ex.getMessage();
		}
		this.server = server;
		addThread();
	}
	private boolean disconectTest(String str) {
		if(Command.stop.getStr().equals(str)) {
			server.kill(this);
			try {
				System.out.println("Stopping");
				input.close();
				out.close();
				socket.close();
			} catch(IOException ex) {}
			listenerStopper = false;
			return true;
		}
		return false;
	}
	private boolean newGameTest(String str) {
		if(Command.startGame.getStr().equals(str)) {
			x = 1;
			server.getRoom(this);
			return true;
		}
		return false;
	}
	int x = 0;
	private void addThread(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(listenerStopper) {
					try {
						String str = input.readUTF();
						if(str == null || str.isEmpty()) {
							throw new IOException("void message");
						}
						if(disconectTest(str)) {
							continue;
						}
						if(newGameTest(str))
							continue;
						listeners.forEach(e -> e.gottenMessage(str));
					} catch (IOException e) {
						continue;
					}
				}
			}
		});
		thread.start();
	}
	@Override
	protected void finalize() throws Throwable {
		listenerStopper = false;
		super.finalize();
	}
	public void addListener(ClientListener l) {
		if(l != null)
			listeners.add(l);
	}
	public void removeListener(ClientListener l) {
		if(l != null)
			listeners.remove(l);
	}
}
