import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Line {
	private Line2D.Double shape;
	private boolean excluded = false;
	private boolean focused = false;
	
	public enum Selected {
		none, A, B;
	}
	
	public ArrayList<Integer> boxes = new ArrayList<Integer>();
	
	public Selected selected = Selected.none;
	
	Line(double x1, double y1, double x2, double y2) {
		shape = new Line2D.Double(x1, y1, x2, y2);
	}
	
	Line2D.Double shape() {
		return shape;
	}

	boolean isUsable() {
		return !isSelected() && !excluded;
	}
	
	boolean isSelected() {
		if(selected == Selected.none)
			return false;
		else
			return true;
	}
	
	boolean isExcluded() {
		return excluded;
	}
	
	void selected(DotsAndBoxes.Player player ) {
		if(player == DotsAndBoxes.Player.playerA)
			selected = Selected.A;
		else
			selected = Selected.B;
	}

	void toggleExclusionState() {
		excluded = !excluded;
	}
	
	void removeFocus() {
		focused = false;
	}
	
	void setFocused() {
		focused = true;
	}
	
	boolean haveFocused() {
		return focused;
	}
}
