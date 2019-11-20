// CheckersFocusListener
package my.checkers;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

//Listener for Checkers
public class ChekersFocusListener implements FocusListener{
	private final Checker owner;
	private final CheckersPlace place;
	private final double coefficient = 0.3;
	private final int enemy;
	public ChekersFocusListener(Checker owner, CheckersPlace place) {
		this.owner = owner;
		this.place = place;
		enemy = owner.teamCode == CheckersPlace.white ? CheckersPlace.black : CheckersPlace.white;
	}
	private void addCheckerHints(int row, int column) {
		if(!addKillers(row, column)) {
			if(column != 0) {
				CheckersPlaceCell goal = place.getCell(row + owner.way, column - 1);
				Hint.addHint((int)(goal.getBounds().width * coefficient), goal, new ClickMouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						place.move(row, column, row + owner.way, column - 1, true);
						place.setNewMoveQueue(true);
					}
				});
			}
			if(column != place.getPlaceSize() - 1) {
				CheckersPlaceCell goal = place.getCell(row + owner.way, column + 1);
				Hint.addHint((int)(goal.getBounds().width * coefficient), goal, new ClickMouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						place.move(row, column, row + owner.way, column + 1, true);
						place.setNewMoveQueue(true);
					}
				});
			}
		}
	}
	private boolean addOneWayKiller(int row, int column, int cr, int cc) {
		CheckersPlaceCell cell = place.getCell(row + cr, column + cc);
		if(!cell.isEmpty() && cell.getChecker().teamCode == enemy && cell.getChecker().isAlive() && place.getCell(row + 2 * cr, column + 2 * cc).isEmpty()) {
			CheckersPlaceCell goal = place.getCell(row + 2*cr, column + 2*cc);
			Hint.addHint((int)(goal.getBounds().width * coefficient), goal, new ClickMouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Hint.removeHints();
					place.move(row, column, row + 2*cr, column + 2*cc, true);
					place.killChecker(row + cr, column + cc, true);
					owner.requestFocus();
					if((!owner.isQueen() && !addKillers(row + 2*cr, column + 2*cc)) || 
							(owner.isQueen() && !addQueenKillers(row + 2*cr, column + 2*cc)))
						place.setNewMoveQueue(true);
				}
			});
			return true;
		}
		return false;
	}
	private boolean addKillers(int row, int column) {
		boolean res = false;
		if(column > 1 && row > 1) {
			if(addOneWayKiller(row, column, -1, -1))
				res = true;
		}
		if(column > 1 && row < place.getPlaceSize() - 2) {
			if(addOneWayKiller(row, column, 1, -1))
				res = true;
		}
		if(column < place.getPlaceSize() - 2 && row < place.getPlaceSize() - 2) {
			if(addOneWayKiller(row, column, 1, 1))
				res = true;
		}
		if(column < place.getPlaceSize() - 2 && row > 1) {
			if(addOneWayKiller(row, column, -1, 1))
				res = true;
		}
		return res;
	}
	private void addOnWayQeenHints(int row, int column, int cr, int cc) {
		for(int i = 1; place.getPlaceSize() > row + cr * i && row + cr * i >= 0
				&& place.getPlaceSize() > column + cc * i && column + cc * i >= 0; i++) {
			final int goalRow = row + cr * i, goalColumn = column + cc * i;
			CheckersPlaceCell goal = place.getCell(goalRow, goalColumn);
			if(!goal.isEmpty())
				break;
			Hint.addHint((int)(goal.getBounds().width * coefficient), goal, new ClickMouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					place.move(row, column, goalRow, goalColumn, true);
					place.setNewMoveQueue(true);
				}
			});
		}
	}
	private void addQueenHints(int row, int column) {
		if(!addQueenKillers(row, column)) {
			addOnWayQeenHints(row, column, 1, -1);
			addOnWayQeenHints(row, column, 1, 1);
			addOnWayQeenHints(row, column, -1, 1);
			addOnWayQeenHints(row, column, -1, -1);
		}
	}
	private ArrayList<int[]> findPosibleGoals(int deadRow, int deadColumn, int cr, int cc) {
		boolean streightKill = false;
		ArrayList<int[]> specialGoals = new ArrayList<>();
		ArrayList<int[]> allGoals = new ArrayList<>();
		if(place.oneWayQueenKillEnebledTest(deadRow + cr, deadColumn + cc, cr, cc, enemy)) {
			specialGoals.add(new int[] {deadRow + cr, deadColumn + cc});
			streightKill = true;
		}
		for(int i = 1; place.getPlaceSize() > deadRow + cr * i && deadRow + cr * i >= 0
				&& place.getPlaceSize() > deadColumn + cc * i && deadColumn + cc * i >= 0; i++) {
			final int row = deadRow + cr * i, column = deadColumn + cc * i;
			if(!place.getCell(row, column).isEmpty())
				break;
			allGoals.add(new int[] {row, column});
			if(place.oneWayQueenKillEnebledTest(row, column, -cr, cc, enemy)) {
				specialGoals.add(new int[] {row, column});
			}
			if(place.oneWayQueenKillEnebledTest(row, column, cr, -cc, enemy)) {
				specialGoals.add(new int[] {row, column});
			}
		}
		return specialGoals.isEmpty() || streightKill ? allGoals : specialGoals;
	}
	private boolean addOneWayQueenKillers(int row, int column, int cr, int cc) {
		for(int i = 1; place.getPlaceSize() - 1 > row + cr * i && row + cr * i > 0
				&& place.getPlaceSize() - 1 > column + cc * i && column + cc * i > 0; i++) {
			final int goalRow = row + cr * i, goalColumn = column + cc * i;
			CheckersPlaceCell cell = place.getCell(goalRow, goalColumn);
			if(!cell.isEmpty()) {
				if(cell.getChecker().teamCode == enemy && cell.getChecker().isAlive() && place.getCell(goalRow + cr, goalColumn + cc).isEmpty()) {
					ArrayList<int[]> goals = findPosibleGoals(goalRow, goalColumn, cr, cc);
					for(int j = 0; j < goals.size(); j++) {
						CheckersPlaceCell goal = place.getCell(goals.get(j)[0], goals.get(j)[1]);
						final int[] curGoal = goals.get(j);
						Hint.addHint((int)(goal.getBounds().width * coefficient), goal, new ClickMouseListener() {
							@Override
							public void mouseClicked(MouseEvent e) {
								place.move(row, column, curGoal[0], curGoal[1], true);
								place.killChecker(goalRow, goalColumn, true);
								owner.requestFocus();
								if(!addQueenKillers(curGoal[0], curGoal[1])) {
									place.setNewMoveQueue(true);
								}
							}
						});
					}
					return true;
				} else {
					return false;
				}
			} 
//			else if(!cell.isEmpty() && !cell.getChecker().isAlive()){
//				return false;
//			}
			
		}
		return false;
	}
	private boolean addQueenKillers(int row, int column) {
		boolean res = false;
		if(addOneWayQueenKillers(row, column, 1, 1))
			res = true;
		if(addOneWayQueenKillers(row, column, 1, -1))
			res = true;
		if(addOneWayQueenKillers(row, column, -1, 1))
			res = true;
		if(addOneWayQueenKillers(row, column, -1, -1))
			res = true;
		return res;
	}
	@Override
	public void focusGained(FocusEvent e) {
		int y = owner.getMyCell().getCode().charAt(0) - 'A'; // column
		int x = owner.getMyCell().getCode().charAt(1) - '1'; // row
		if(owner.isQueen()) {
			addQueenHints(x, y);
		} else {
			addCheckerHints(x, y);
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		Hint.removeHints();
	}
}
