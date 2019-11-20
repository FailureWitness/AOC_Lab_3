// CheckersPlaceCell
package my.checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class CheckersPlaceCell extends JComponent{
	private final int width, height;
	private final Color color;
	private Checker ch = null;
	public final String code;
	public CheckersPlaceCell(int width, int height, Color color, String code) {
		this.width = width;
		this.height = height;
		this.color = color;
		this.code = code;
		setBounds(0, 0, width, height);
	}
	public String getCode() {
		return code;
	}
	public void setPosition(int x, int y) {
		setBounds(x, y, width, height);
	}
	public boolean isEmpty() {
		return ch == null;
	}
	public void addChecker(Checker ch) {
		if(this.ch == null) {
			add(ch);
			this.ch = ch;
		}
	}
	public void removeChecker() {
		if(ch != null) {
			remove(ch);
			repaint();
			ch = null;
		} else {
//			System.out.println("Warning!!!");
		}
	}
	public Checker getChecker() {
		return ch;
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Rectangle2D.Double r = new Rectangle2D.Double(0, 0, width, height);
		g2D.setColor(color);
		g2D.fill(r);
		super.paint(g);
	}
}
