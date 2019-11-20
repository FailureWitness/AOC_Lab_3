// GameListener
package my.checkers;

public interface GameListener {
	public void killChecker(int row, int column);
	public void moveChecker(int rowS, int columnS, int rowG, int columnG);
	public void setNewQueue();
	public void gameOver();
}
