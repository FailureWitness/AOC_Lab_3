// Checker
package my.checkers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class Checker extends JComponent{
	private final int diametr;
	private final double [] c = new double[] {0.9, 0.8, 0.5, 0.3};
	private final Color colorA, colorB, queenColor = new Color(250, 200, 0), selectColor = new Color(0, 150, 250);
	private boolean isFocused = false;
	private boolean isAlive = false;
	private boolean isQueen = false;
	public int way = 0;
	public final int teamCode;
	private CheckersPlaceCell myCell;
	public Checker(int teamCode, int diametr, CheckersPlaceCell field, Color colorA, Color colorB) {
		this.diametr = diametr;
		this.colorA = colorA;
		this.colorB = colorB;
		this.myCell = field;
		this.teamCode = teamCode;
		setFocusable(true);
		setBounds(0, 0, diametr, diametr);
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				isFocused = false;
				repaint();
				if(myCell != null)
					myCell.repaint();
			}
			@Override
			public void focusGained(FocusEvent arg0) {
				isFocused = true;
				repaint();
				if(myCell != null)
					myCell.repaint();
			}
		});
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {
				requestFocus();
			}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
	}
	@Override
	public void paint(Graphics g) {
		int alpha = 70;
//		if(isAlive)
			alpha = 255;
		Graphics2D g2D = (Graphics2D) g;
		if(isFocused) {
			Ellipse2D.Double r = new Ellipse2D.Double(0, 0, diametr, diametr);
			g2D.setColor(selectColor);
			g2D.fill(r);
		}
		for(int i = 0; i < c.length; i++) {
			Ellipse2D.Double circle = new Ellipse2D.Double((1 - c[i]) * diametr / 2, (1 - c[i]) * diametr / 2, c[i] * diametr, c[i] * diametr);
			if((i & 1) == 0) {
				if(isQueen)
					g2D.setColor(new Color(queenColor.getRed(), queenColor.getGreen(), queenColor.getBlue(), alpha));
				else
					g2D.setColor(new Color(colorB.getRed(), colorB.getGreen(), colorB.getBlue(), alpha));
			} else {
				g2D.setColor(new Color(colorA.getRed(), colorA.getGreen(), colorA.getBlue(), alpha));
			}
			g2D.fill(circle);
		}
		if(!isAlive) {
			Ellipse2D.Double e = new Ellipse2D.Double(0, 0, diametr, diametr);
			g2D.setColor(new Color(70, 70, 70, 150));
			g2D.fill(e);
		}
		super.paint(g);
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setPosition(int x, int y) {
		setBounds(x, y, diametr, diametr);
	}
	public CheckersPlaceCell getMyCell() {
		return myCell;
	}
	public void setMyCell(CheckersPlaceCell myCell) {
		this.myCell = myCell;
	}
	public boolean isQueen() {
		return isQueen;
	}
	public void setQueen(boolean q) {
		isQueen = q;
	}
}
