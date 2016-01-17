import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ConsoleObserver implements Observer {
	private static final int WIDTH = DotsAndBoxes.WIDTH;
	private static final int HEIGHT = DotsAndBoxes.HEIGHT;
	private static final double PEN_RADIUS = 1.0/100.0;
	private static final double LARGE_PEN_RADIUS = 2.0*PEN_RADIUS;
	private static final Color DOT_COLOR = StdDraw.WHITE;
	private static final Color SELECTED_LINE_COLOR_A = StdDraw.GREEN;
	private static final Color SELECTED_LINE_COLOR_B = StdDraw.BLUE;
	private static final Color EXCLUDED_LINE_COLOR = StdDraw.WHITE;
	private boolean gameOver = false;
	
	ConsoleObserver() {
		StdDraw.setXscale(0, WIDTH);
		StdDraw.setYscale(0, HEIGHT);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.setPenRadius(PEN_RADIUS);
		StdDraw.clear(StdDraw.BLACK);
	}
	
	public boolean gameOver() {
		return gameOver;
	}

	public void update(Observable obs, Object obj) {
		DotsAndBoxes dab = (DotsAndBoxes)obs;
		if(obs instanceof DotsAndBoxes) {
			if(obj instanceof Line)
				repaint(dab);
			else if(obj instanceof DotsAndBoxes.Winners) {
				DotsAndBoxes.Winners winners = (DotsAndBoxes.Winners)obj;
				System.out.format("Game is Over!\n");
				if(winners.count() > 1)
					System.out.format("Draw (retry)\n");
				else
					System.out.format("The winner is: %s\n", winners.first());
				gameOver = true;
			} else if(obj instanceof int []) {
				int[] scores = (int [])obj;
				System.out.format("Scores: %d - %d\n", scores[0], scores[1]);
			} else if(obj == null)
				repaint(dab);
		}
	}
		
	private void repaint(DotsAndBoxes dab) {
		StdDraw.clear(StdDraw.BLACK);
		ArrayList<Line> lines = dab.getLines();
		for(int i = 0; i < lines.size(); ++i) {
			Line l = lines.get(i);
			if(l.isSelected()){
				if(l.selected == Line.Selected.A)
					draw(l, SELECTED_LINE_COLOR_A, PEN_RADIUS);
				else 
					draw(l, SELECTED_LINE_COLOR_B, PEN_RADIUS);
			} else if(l.isExcluded())
				draw(l, EXCLUDED_LINE_COLOR, LARGE_PEN_RADIUS);
			
			final double x = l.shape().getX1() + Math.abs((l.shape().getX1() - l.shape().getX2())/2);
			final double y = HEIGHT - l.shape().getY1() + Math.abs((l.shape().getY1() - l.shape().getY2())/2);
			StdDraw.setPenColor(StdDraw.WHITE);
			StdDraw.setPenRadius(PEN_RADIUS);
			StdDraw.text(x, y, String.format("%d", i));
		}

		int boxCount = dab.getBoxCount();
		ArrayList<ArrayList<Point2D.Double>> dots = dab.getDots();
		for(int i = 0; i <= boxCount; ++i) {
			for(int j = 0; j <= boxCount; ++j) {
				final double x = dots.get(i).get(j).x;
				final double y = dots.get(i).get(j).y;
				StdDraw.setPenColor(DOT_COLOR);
				StdDraw.setPenRadius(LARGE_PEN_RADIUS);
				StdDraw.point(x, HEIGHT - y);
			}
		}
	}

	private static void draw(Line l, Color c, double pen_radius) {
		StdDraw.setPenColor(c);
		StdDraw.setPenRadius(pen_radius);
		StdDraw.line(
			l.shape().getX1(),
			HEIGHT - l.shape().getY1(),
			l.shape().getX2(),
			HEIGHT - l.shape().getY2());
	}
}
