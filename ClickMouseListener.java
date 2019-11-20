// ClickMouseListener
package my.checkers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class ClickMouseListener implements MouseListener{
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public abstract void mouseClicked(MouseEvent e);
}
