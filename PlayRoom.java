package my.server;

import java.io.IOException;

public class PlayRoom {
	private final Server server;
//	private final PlayRoom thisRoom;
	public ClientWorker playerA, playerB;
	private final int gamePlaceSize = 8;
	private final ClientListener 
		listenerA = new ClientListener() {
				@Override
				public String gottenMessage(String message) {
					gameEvent(playerB, message);
					return null;
				}
			},
		listenerB = new ClientListener() {
				@Override
				public String gottenMessage(String message) {
					gameEvent(playerA, message);
					return null;
				}
			}; 
	private void gameEvent(ClientWorker w, String message) {
		System.out.println("Server in: " + message);
		if(w != null && Command.isKill(message)) {
			int[] arr = Command.getMessage(message);
			for(int i = 0; i < arr.length; i++) {
				arr[i] = gamePlaceSize - 1 - arr[i];
			}
			try {
				String str = new Command(arr[0], arr[1]).getStr();
				w.out.writeUTF(str);
				System.out.println("Server out: " + str);
			} catch (IOException ex) {
				ex.fillInStackTrace();
			}
		} else if(w != null && Command.isMove(message)) {
			int[] arr = Command.getMessage(message);
			for(int i = 0; i < arr.length; i++) {
				arr[i] = gamePlaceSize - 1 - arr[i];
			}
			try {
				String str = new Command(arr[0], arr[1], arr[2], arr[3]).getStr();
				w.out.writeUTF(str);
				System.out.println("Server out: " + str);
			} catch (IOException ex) {
				ex.fillInStackTrace();
			}
		} else if(Command.nextMove.getStr().equals(message)) {
			try {
				w.out.writeUTF(message);
				System.out.println("Server out: " + message);
			} catch (IOException ex) {
				ex.fillInStackTrace();
			}
		} else if(Command.endGame.getStr().equals(message)) {
			try {
				w.out.writeUTF(message);
				System.out.println("Server out: " + message);
			} catch(IOException ex) {
				ex.fillInStackTrace();
			}
			server.kill(this);
			playerA.removeListener(listenerA);
			playerB.removeListener(listenerB);
			playerA.room = null;
			playerB.room = null;
//			stop(w);
		}
	}
	public PlayRoom(ClientWorker playerA, ClientWorker playerB, Server server) {
		this.playerA = playerA;
		this.playerB = playerB;
		this.playerA.addListener(listenerA);
		this.playerB.addListener(listenerB);
		try {
			this.playerA.out.writeUTF(Command.setWhiteTeam.getStr());
			this.playerB.out.writeUTF(Command.setBlackTeam.getStr());
		} catch (IOException ex) {
			ex.fillInStackTrace();
		}
		this.server = server;
//		thisRoom = this;
	}
	private void finalizePlayer(ClientWorker player, ClientListener l) {
		try {
			player.removeListener(l);
			player.out.writeUTF(Command.endGame.getStr());
		} catch (IOException ex) {
			ex.fillInStackTrace();
		}
	}
	@Override
	protected void finalize() throws Throwable {
		finalizePlayer(playerA, listenerA);
		finalizePlayer(playerB, listenerB);
		super.finalize();
	}
	public void stop(ClientWorker loser) {
		try {
			if(loser == playerA) {
				playerB.out.writeUTF(Command.endGame.getStr());
				playerB.removeListener(listenerB);
			} else {
				playerA.out.writeUTF(Command.endGame.getStr());
				playerA.removeListener(listenerA);
			}
		} catch(IOException ex){
			
		}
		server.kill(this);
		playerA.room = null;
		playerB.room = null;
		playerA = null;
		playerB = null;
	}
}
