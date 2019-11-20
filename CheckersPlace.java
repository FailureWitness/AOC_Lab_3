// CheckersPlace
package my.checkers;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JComponent;

public class CheckersPlace extends JComponent{
	private final int rowsCount = 8, armyRowsCount = 3, columnsCount = 8, checkerCount = 12, cellSize;
	private CheckersPlaceCell[][] cells;
	private Checker[][] checkers;
	private ArrayList<int[]> lastTaken = new ArrayList<>(); // temp array for killing checkers (one move)
	private ArrayList<GameListener> listeners = new ArrayList<>();
	public static final int white = 0, black = 1;
	private int moveQueue = white;
	private final Color lightC, darkC, lightA, lightB, darkA, darkB;
	private boolean playWithServer;
	private int orientation = -1;
	private int nonKillMovesCount = 0;
	private final int maxNonKillMovesCount = 15;
	public CheckersPlace(Color light, Color dark, int cellSize,
			Color lightA, Color lightB, Color darkA, Color darkB, Color colorH, boolean PvE) {
		this.lightC = light;
		this.darkC = dark;
		this.lightA = lightA;
		this.lightB = lightB;
		this.darkA = darkA;
		this.darkB = darkB;
		this.cellSize = cellSize;
		this.playWithServer = PvE;
		Hint.setColor(colorH);
		cells = new CheckersPlaceCell[rowsCount][columnsCount];
		checkers = new Checker[2][checkerCount];
		createCells();
		createChecker();
		setBounds(0, 0, columnsCount * cellSize, rowsCount * cellSize);
	}
	private void createChecker() {
		for(int i = 0; i < checkerCount; i++) {
			checkers[white][i] = new Checker(white, cellSize, null, lightA, lightB);
			checkers[white][i].addFocusListener(new ChekersFocusListener(checkers[white][i], this));
		}
		for(int i = 0; i < checkerCount; i++) {
			checkers[black][i] = new Checker(black, cellSize, null, darkA, darkB);
			checkers[black][i].addFocusListener(new ChekersFocusListener(checkers[black][i], this));
		}
	}
	private void createCells() {
		for(int i = 0; i < rowsCount; i++) {
			for(int j = 0; j < columnsCount; j++) {
				Color c;
				if(i % 2 == j % 2) {
					c = lightC;
				} else {
					c = darkC;
				}
				char[] code = {(char)('A' + j), (char)('1' + i)};
				cells[i][j] = new CheckersPlaceCell(cellSize, cellSize, c, new String(code));
				cells[i][j].setPosition(j * cellSize, i * cellSize);
				add(cells[i][j]);
			}
		}
	}
	private void createEnemyArmy(int enemyArmy) {
		final int checkersInRow = checkerCount / armyRowsCount;
		for(int i = 0 ; i < armyRowsCount; i++) {
			for(int j = i * checkersInRow; j < (i + 1) * checkersInRow; j++) {
				CheckersPlaceCell cell = cells[i][cells.length - 1 - i % 2 - 2 * (j % checkersInRow)];
				checkers[enemyArmy][j].setMyCell(cell);
				checkers[enemyArmy][j].setFocusable(false);
				checkers[enemyArmy][j].way = 1;
				checkers[enemyArmy][j].setAlive(true);
				cell.addChecker(checkers[enemyArmy][j]);
			}
		}
	}
	private void createMyArmy(int myArmy) {
		final int checkersInRow = checkerCount / armyRowsCount;
		for(int i = 0 ; i < armyRowsCount; i++) {
			for(int j = i * checkersInRow; j < (i + 1) * checkersInRow; j++) {
				CheckersPlaceCell cell = cells[cells.length - 1 - i][i % 2 + 2 * (j % checkersInRow)];
				checkers[myArmy][j].setMyCell(cell);
				checkers[myArmy][j].setFocusable(false);
				checkers[myArmy][j].way = -1;
				checkers[myArmy][j].setAlive(true);
				cell.addChecker(checkers[myArmy][j]);
			}
		}
	}
	private void clearCells(){
		for(int i = 0; i < rowsCount; i++) {
			for(int j = 0; j < columnsCount; j++) {
				cells[i][j].removeChecker();
			}
		}
	}
	
	public void setOrientation (int orientation){
		clearCells();
		this.orientation = orientation;
		if(orientation == white) {
			createMyArmy(white);
			createEnemyArmy(black);
		} else {
			createMyArmy(black);
			createEnemyArmy(white);
		}
		moveQueue = black;
		setNewMoveQueue(false);
		repaint();
	}
	public CheckersPlaceCell getCell(int row, int column) {
		return cells[row][column];
	}
	public int getPlaceSize() {
		return rowsCount;
	}
	public void move(int rowS, int columnS, int rowG, int columnG, boolean listen) {
		Checker ch = cells[rowS][columnS].getChecker();
		cells[rowS][columnS].removeChecker();
		cells[rowG][columnG].addChecker(ch);
		ch.setMyCell(cells[rowG][columnG]);
		if((ch.way == -1 && rowG == 0) || (ch.way == 1 && rowG == cells.length - 1)) {
			ch.setQueen(true);
		}
		if(listen) {
			listeners.forEach(e -> e.moveChecker(rowS, columnS, rowG, columnG));
		}
	}
	public void killChecker(int row, int column, boolean listen) {
		Checker ch = cells[row][column].getChecker();
		if(ch != null) {
			ch.setAlive(false);
			cells[row][column].repaint();
			lastTaken.add(new int[]{row, column});
			if(listen) {
				listeners.forEach(e -> e.killChecker(row, column));
			}
		}
	}
	private void removeDeadCheckers() {
		while(0 < lastTaken.size()) {
			int[] temp = lastTaken.get(0);
			cells[temp[0]][temp[1]].removeChecker();
			lastTaken.remove(0);
		}
	}
	public boolean killEnebledTest(int row, int column, int enemy) {
		int s = cells.length - 1;
		for(int i = 0; i < checkerCount; i++) {
			Checker ch = checkers[enemy][i];
			if(!ch.isAlive())
				continue;
			if(row > 1 && column > 1 && 
					cells[row - 1][column - 1].getChecker() == ch && 
					cells[row - 2][column - 2].isEmpty()) {
				return true;
			}
			if(row > 1 && column < s - 1 && 
					cells[row - 1][column + 1].getChecker() == ch && 
					cells[row - 2][column + 2].isEmpty()) {
				return true;
			}
			if(row < s - 1 && column < s - 1 && 
					cells[row + 1][column + 1].getChecker() == ch && 
					cells[row + 2][column + 2].isEmpty()) {
				return true;
			}
			if(row < s - 1 && column > 1 && 
					cells[row + 1][column - 1].getChecker() == ch && 
					cells[row + 2][column - 2].isEmpty()) {
				return true;
			}
		}
		return false;
	}
	public boolean oneWayQueenKillEnebledTest(int row, int column, int cr, int cc, int enemy) {
		for(int i = 1; getPlaceSize() - 1 > row + cr * i && row + cr * i > 0
				&& getPlaceSize() - 1 > column + cc * i && column + cc * i > 0; i++) {
			CheckersPlaceCell cell = cells[row + cr * i][column + cc * i];
			if(!cell.isEmpty()) {
				if(cell.getChecker().teamCode == enemy && cell.getChecker().isAlive() 
						&& cells[row + cr * i + cr][column + cc * i + cc].isEmpty()) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	public boolean queenKillEnebledTest(int row, int column, int enemy) {
		if(oneWayQueenKillEnebledTest(row, column, 1, 1, enemy))
			return true;
		if(oneWayQueenKillEnebledTest(row, column, 1, -1, enemy))
			return true;
		if(oneWayQueenKillEnebledTest(row, column, -1, 1, enemy))
			return true;
		if(oneWayQueenKillEnebledTest(row, column, -1, -1, enemy))
			return true;
		return false;
	}
	private boolean canMove(Checker ch) {
		CheckersPlaceCell cell = ch.getMyCell();
		int row = cell.getBounds().y / cell.getBounds().height;
		int column = cell.getBounds().x / cell.getBounds().width;
		
		if(!ch.isQueen()) {
			if(ch.way > 0 && row != rowsCount - 1) {
				if((column != 0 && cells[row + 1][column- 1].isEmpty()) || (column != columnsCount - 1 && cells[row + 1][column + 1].isEmpty())) {
					return true;
				}
			} else if (ch.way < 0 && row != 0) {
				if((column != 0 && cells[row - 1][column- 1].isEmpty()) || (column != columnsCount - 1 && cells[row - 1][column + 1].isEmpty())) {
					return true;
				}	
			}
		} else {// checker is queen
			if(row != rowsCount - 1) {
				if((column != 0 && cells[row + 1][column- 1].isEmpty()) || (column != columnsCount - 1 && cells[row + 1][column + 1].isEmpty())) {
					return true;
				}
			}
			if (row != 0) {
				if((column != 0 && cells[row - 1][column- 1].isEmpty()) || (column != columnsCount - 1 && cells[row - 1][column + 1].isEmpty())) {
					return true;
				}	
			}
		}
		return false;
	}
	private boolean setTeamFocuseble(int team, boolean b) {
		boolean res = false;
		for(int i = 0; i < checkerCount; i++) {
			if(checkers[team][i].isAlive()) {
				if(!b || canMove(checkers[team][i])) {
					checkers[team][i].setFocusable(b);
					res = true;
				}
			}
		}
		return res;
	}
	public void setNewMoveQueue(boolean listen) {
		removeDeadCheckers();
		setTeamFocuseble(moveQueue, false);
		requestFocus();
		int nextQueue = moveQueue == white ? black : white;
		if(!playWithServer || orientation == nextQueue) {
			boolean killNeed = false;
			for(int i = 0; i < checkerCount; i++) {
				if(!checkers[nextQueue][i].isAlive())
					continue;
				CheckersPlaceCell cell = checkers[nextQueue][i].getMyCell();
				int row = cell.getBounds().y / cell.getBounds().height;
				int column = cell.getBounds().x / cell.getBounds().width;
				if((!checkers[nextQueue][i].isQueen() && killEnebledTest(row, column, moveQueue)) ||
						(checkers[nextQueue][i].isQueen() && queenKillEnebledTest(row, column, moveQueue))) {
					checkers[nextQueue][i].setFocusable(true);
					killNeed = true;
				}
			}
			if(!killNeed) {
				if(!setTeamFocuseble(nextQueue, true)) {
					listeners.forEach(e -> e.gameOver());
				} else if(nonKillMovesCount ++ >= maxNonKillMovesCount) {
					listeners.forEach(e -> e.gameOver());
				}
			} else {
				nonKillMovesCount = 0;
			}
		}
		moveQueue = nextQueue;
		if(listen) {
			listeners.forEach(e -> e.setNewQueue());
		}
	}
	public void addGameListener(GameListener l) {
		listeners.add(l);
	}
	public void removeListener(GameListener l) {
		listeners.remove(l);
	}
	public void setPosition(int x, int y) {
		setBounds(x, y, columnsCount * cellSize, rowsCount * cellSize);
	}
	public void stopGame() {
		for(int i = 0; i < checkerCount; i++) {
			checkers[white][i].setFocusable(false);
			checkers[black][i].setFocusable(false);
		}
	}
}
