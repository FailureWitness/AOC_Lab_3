package my.server;

public class Command {
	public static final Command 
		stop = new Command("STOP"),				//send to server
		startGame = new Command("START"),		//send to server
		nextMove = new Command("YOUR_MOVE"),	//send to client
		setBlackTeam = new Command("BLACK"),	//send to client
		setWhiteTeam = new Command("WHITE"),	//send to client
		endGame = new Command("END");			//send to client
	private final String str;
	private Command(String str){
		this.str = str;
	}
	public Command(int rowA, int columnA, int rowB, int columnB) {
		str = "MOVE|" + rowA + " " + columnA + " " + rowB + " " + columnB + "|";
	}
	public Command(int deadRow, int deadColumn) {
		str = "KILL|" + deadRow + " " + deadColumn + "|";
	}
	public String getStr() {
		return str;
	}
	public static boolean isSpecial(Command c) {
		return 	c.equals(stop) || 
				c.equals(startGame) ||
				c.equals(nextMove) || 
				c.equals(setBlackTeam) ||
				c.equals(setWhiteTeam) || 
				c.equals(endGame);
	}
	public static boolean isKill(String str) {
		return str.split("\\|")[0].equals("KILL");
	}
	public static boolean isMove(String str) {
		return str.split("\\|")[0].equals("MOVE");
	}
	public boolean isKill() {
		return isKill(str);
	}
	public boolean isMove() {
		return isMove(str);
	}
	public boolean equals(Command c) {
		return this.str.equals(c.str);
	}
	public static int[] getMessage(String str){
		if(Command.isSpecial(new Command(str))) {
			return null;
		} else {
			String[] sArr = str.split("\\|")[1].split(" ");
			int[] iArr = new int[sArr.length];
			for(int i = 0; i < sArr.length; i++) {
				try {
					iArr[i] = Integer.parseInt(sArr[i]);
				} catch (Exception e) {
					return null;
				}
			}
			return iArr;
		}
	}
}
