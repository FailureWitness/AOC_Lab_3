package my.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import my.checkers.CheckersPlace;
import my.checkers.GameListener;
import my.server.Command;

public class Client{

	public static void main(String[] args) {
		new Client("127.0.0.1");
	}
	private GameListener listener = new GameListener() {
		@Override
		public void moveChecker(int rowS, int columnS, int rowG, int columnG) {
			try {
				out.writeUTF(new Command(rowS, columnS, rowG, columnG).getStr());
			} catch (IOException ex) {
				
			}
		}
		@Override
		public void killChecker(int row, int column) {
			try {
				out.writeUTF(new Command(row, column).getStr());
			} catch (IOException ex) {
				
			}
		}
		@Override
		public void setNewQueue() {
			try {
				out.writeUTF(Command.nextMove.getStr());
			} catch (IOException ex) {
				
			}
		}
		@Override
		public void gameOver() {
			endGame(true, "You lose.");
		}
	};
	private final int port = 154;
	private Socket socket = null;
	private DataInputStream input;
	private DataOutputStream out;
	private JFrame frame = new JFrame("Client");
	private JLabel mainLabel = new JLabel();
	private CheckersPlace place;
	private JButton starter = new JButton("Start Game"), ender = new JButton("End Game");
	private boolean gameOver = true;
	
	public Client(String ip) {
		createJs();
		try {
			socket= new Socket(ip, port);
			out = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch(IOException ex) {
			ex.getStackTrace();
		}
		addServerListener();
	}
	private void startGame() {
		try {
			out.writeUTF(Command.startGame.getStr());
		} catch (IOException ex) {
			ex.fillInStackTrace();
		}
	}
	private void endGame(boolean notify, String message) {
		if(notify) {
			try {
				out.writeUTF(Command.endGame.getStr());
			} catch (IOException ex) {
				ex.fillInStackTrace();
			}
		}
		place.stopGame();
		JFrame frame = new JFrame("Message");
		JLabel l = new JLabel();
		l.setText(message);
		l.setHorizontalAlignment(SwingConstants.CENTER);
		frame.add(l);
		frame.setBounds(this.frame.getBounds().x, this.frame.getBounds().y, 300, 100);
		frame.setVisible(true);
		gameOver = true;
	}
	private void setCloseOperation() {
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			@Override
			public void windowIconified(WindowEvent e) {
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					out.writeUTF(Command.stop.getStr());
					out.close();
					input.close();
					socket.close();
				} catch (Throwable ex) {
					ex.fillInStackTrace();
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {
			}
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}
	private void createJs() {
		place = new CheckersPlace(new Color(215, 215, 215), new Color(40, 40, 40), 80, 
				Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, new Color(0, 150, 200), true);
		place.addGameListener(listener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(mainLabel);
		mainLabel.add(place);
		place.setPosition(5, 5);
		mainLabel.add(starter);
		starter.setBounds(10, 650, 100, 50);
		starter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(gameOver) {
					startGame();
					gameOver = false;
				}
			}
		});
		mainLabel.add(ender);
		ender.setBounds(120, 650, 100, 50);
		ender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!gameOver) {
					endGame(true, "You lose.");
					gameOver = true;
				}
			}
		});
		frame.setSize(670, 800);
		setCloseOperation();
		frame.setVisible(true);
	}
	private boolean setQueue(String str) {
		if(Command.nextMove.getStr().equals(str)) {
			place.setNewMoveQueue(false);
			return true;
		}
		return false;
	}
	private boolean setKill(String str) {
		if(Command.isKill(str)) {
			int[] arr = Command.getMessage(str);
			place.killChecker(arr[0], arr[1], false);
			place.repaint();
			return true;
		}
		return false;
	}
	private boolean setMove(String str) {
		if(Command.isMove(str)) {
			int[] arr = Command.getMessage(str);
			place.move(arr[0], arr[1], arr[2], arr[3], false);
			place.repaint();
			return true;
		}
		return false;
	}
	private boolean setOrientation(String str) {
		if(Command.setWhiteTeam.getStr().equals(str)) {
			place.setOrientation(CheckersPlace.white);
			return true;
		}
		if(Command.setBlackTeam.getStr().equals(str)) {
			place.setOrientation(CheckersPlace.black);
			return true;
		}
		return false;
	}
	private boolean isEnd(String str) {
		if(Command.endGame.getStr().equals(str)) {
			endGame(false, "You win");
		}
		return false;
	}
	private void addServerListener() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String str = "";
				while(true) {
					try {
						str = input.readUTF();
						if(setOrientation(str)) {
							continue;
						}
						if(setMove(str)) {
							continue;
						}
						if(setKill(str)) {
							continue;
						}
						if(setQueue(str)) {
							continue;
						}
						if(isEnd(str)) {
							continue;
						}
					} catch(IOException ex) {
						
					}
				}
			}
		});
		thread.start();
	}
	@Override
	protected void finalize() throws Throwable {
		out.writeUTF(Command.stop.getStr());
		super.finalize();
	}
}
