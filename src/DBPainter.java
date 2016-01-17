import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
	
@SuppressWarnings("serial")
class DBPainter extends Component implements MouseListener, MouseMotionListener, Observer {
	private static final int DOT_DIAM = 5;
	private static final Color DOT_COLOR = Color.BLACK;
	private static final Color SELECTED_LINE_COLOR_A = Color.GREEN;
	private static final Color SELECTED_LINE_COLOR_B = Color.BLUE;
	private static final Color FOCUSED_LINE_COLOR = Color.BLACK;
	private static final Color EXCLUDED_LINE_COLOR = Color.BLACK;
	private static final double PICKING_AREA = 10;
	private static final float LINE_WIDTH = 2;
	private static final BasicStroke DASHED_STYLE =
		new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	private static final BasicStroke REGULAR_STYLE = new BasicStroke(LINE_WIDTH);
	
	private GUI gui;
	private DotsAndBoxes dab;
	
	DBPainter(GUI gui, int count) {
		this.gui = gui;
		
		dab = new DotsAndBoxes(count, this);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		repaint();
	}

	public void restart(DotsAndBoxes.PlayGame playGame, int boxCount, int excludedLineCount) {
		dab.setConfig(playGame, boxCount, excludedLineCount);
	}

	public void update(Observable obs, Object obj) {
		if(obs instanceof DotsAndBoxes) {
			if(obj instanceof Line)
				repaint();
			else if(obj instanceof DotsAndBoxes.Winners)
				gui.gameIsOver((DotsAndBoxes.Winners)obj);
			else if(obj instanceof int [])
				gui.updateScores((int [])obj);
			else if(obj == null)
				repaint();
		}
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(DotsAndBoxes.WIDTH, DotsAndBoxes.HEIGHT);
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Dimension size = getSize();

		g2d.clearRect(0, 0, size.width, size.height);
		ArrayList<Line> lines = dab.getLines();
		for(Line l: lines) {
			if(l.isSelected()){
				if(l.selected == Line.Selected.A)
					draw(l, g2d, SELECTED_LINE_COLOR_A, REGULAR_STYLE);
				else 
					draw(l, g2d, SELECTED_LINE_COLOR_B, REGULAR_STYLE);
			} else if(l.isExcluded())
				draw(l, g2d, EXCLUDED_LINE_COLOR, DASHED_STYLE);
			else if(l.isUsable() && l.haveFocused())
				draw(l, g2d, FOCUSED_LINE_COLOR, REGULAR_STYLE);
			else
				draw(l, g2d, g2d.getBackground(), REGULAR_STYLE);
		}

		int boxCount = dab.getBoxCount();
		ArrayList<ArrayList<Point2D.Double>> dots = dab.getDots();
		for(int i = 0; i <= boxCount; ++i) {
			for(int j = 0; j <= boxCount; ++j) {
				final double x = dots.get(i).get(j).x - DOT_DIAM/2;
				final double y = dots.get(i).get(j).y - DOT_DIAM/2;
				Ellipse2D c = new Ellipse2D.Double(x, y, DOT_DIAM, DOT_DIAM);
				g2d.setColor(DOT_COLOR);
				g2d.fill(c);
				g2d.draw(c);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
		final double x = e.getX() - PICKING_AREA/2;
		final double y = e.getY() - PICKING_AREA/2;
		ArrayList<Line> lines = dab.getLines();
		for(Line l: lines) {
			if(l.shape().intersects(x, y, PICKING_AREA, PICKING_AREA)) {
				dab.playerDoSelection(l);
				break;
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		// Do nothing.
	}
	
	public void mouseEntered(MouseEvent e) {
		// Do nothing.
	}
	
	public void mouseExited(MouseEvent e) {
		// Do nothing.
	}
	
	public void mouseClicked(MouseEvent e) {
		// Do nothing.
	}
	
	public void mouseMoved(MouseEvent e) {
		double x = e.getX() - PICKING_AREA/2;
		double y = e.getY() - PICKING_AREA/2;
		ArrayList<Line> lines = dab.getLines();
		for(Line l: lines) {
			l.removeFocus();
			boolean underMouse = l.shape().intersects(x, y, PICKING_AREA, PICKING_AREA);
			if(l.isUsable() && underMouse)
				l.setFocused();
			repaint();
		}
	}	

	public void mouseDragged(MouseEvent e) {
		// Do nothing.
	}
	
	private static void draw(Line l, Graphics2D g2d, Color c, BasicStroke style) {
		g2d.setColor(c);
		g2d.setStroke(style);
		g2d.draw(l.shape());		
	}
}
