// Hint
package my.checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class Hint extends JComponent{
	protected static ArrayList<Hint> allHints = null;
	private final int radius, size;
	private static Color color = Color.BLUE;
	private final CheckersPlaceCell myCell;
	private boolean isActive = true;
	private MouseListener myListener = null;
	private Hint(int size, int radius, CheckersPlaceCell myCell) {
		if(allHints == null) {
			allHints = new ArrayList<>();
		}
		allHints.add(this);
		this.radius = radius;
		this.size = size;
		this.myCell = myCell;
		setBounds(0, 0, size, size);
	}
	public static void setColor(Color c) {
		color = c;
	}
	public static void addHint(int radius, CheckersPlaceCell myCell, MouseListener ml) {
		Hint h = new Hint(myCell.getBounds().width, radius, myCell);
		h.addMouseListener(ml);
		h.myListener = ml;
		myCell.add(h);
		myCell.repaint();
	}
	public static void removeHints() {
		while(allHints != null && allHints.size() != 0) {
			allHints.get(0).delMe();
		}
	}
	private void delMe() {
		allHints.remove(this);
		isActive = false;
		removeMouseListener(myListener);
		myCell.repaint();
	}
	@Override
	protected void finalize() throws Throwable {
		delMe();
		super.finalize();
	}
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		if(isActive) {
			Graphics2D g2D = (Graphics2D)g;
			Ellipse2D.Double body = new Ellipse2D.Double((size - radius) / 2, (size - radius) / 2, radius, radius);
			g2D.setColor(color);
			g2D.fill(body);
			super.paint(g);
		}
	}
}
