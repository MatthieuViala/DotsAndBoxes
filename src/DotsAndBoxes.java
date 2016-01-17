import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class DotsAndBoxes extends Observable {
	public final static int WIDTH = 500;
	public final static int HEIGHT = 500;

	public enum Player {
		playerA(0), playerB(1);
		final int ix;
		Player(int ix) {
			this.ix = ix;
		}
	}
	
	public enum PlayGame {
		twoUsers, oneUser;
	}
	
	public class Winners {
		private ArrayList<Player> players;
		Winners() {
			players = new ArrayList<Player>();
		}
		
		public void add(Player p) {
			players.add(p);
		}
		
		public Player first() {
			return players.get(0);
		}
		
		public int count() {
			return players.size();
		}
	}
	
	private int boxCount;
	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<ArrayList<Point2D.Double>> dots = new ArrayList<ArrayList<Point2D.Double>>();
	
	private Player player = Player.playerA;
	
	private PlayGame playGame = PlayGame.oneUser;

	private ArtificialIntelligence ai;

	private int[] scores = new int[2];
	
	DotsAndBoxes(int count, Observer obs) {
		addObserver(obs);
		setConfig(playGame, count, 0);
	}
	
	DotsAndBoxes(PlayGame playGame, int boxCount, int excludedLineCount, Observer obs) {
		addObserver(obs);
		setConfig(playGame, boxCount, excludedLineCount);
	}
	
	public void setConfig(PlayGame playGame, int boxCount, int excludedLineCount) {
		final int MARGIN = 25;

		lines.clear();
		dots.clear();

		player = Player.playerA;
		
		this.boxCount = boxCount;
		this.playGame = playGame;

		ai = new ArtificialIntelligence();

		resetScores();

		for(int j = boxCount; j >= 0; --j) {
			ArrayList<Point2D.Double> dotRow = new ArrayList<Point2D.Double>();
			for(int i = 0; i <= boxCount; ++i) {
				double x = MARGIN + (WIDTH - 2*MARGIN)*(double)i/(double)(boxCount);
				double y = MARGIN + (HEIGHT - 2*MARGIN)*(double)j/(double)(boxCount);
				dotRow.add(new Point2D.Double(x, y));
			}

			dots.add(dotRow);
		}

		for(int row = 0, boxNo = 0; row <= boxCount; ++row) {
			for(int col = 1; col <= boxCount; ++col, ++boxNo) {
				Point2D.Double p1 = dots.get(row).get(col-1);
				Point2D.Double p2 = dots.get(row).get(col);
				Line l = new Line(p1.x, p1.y, p2.x, p2.y);
				lines.add(l);
				if(row < boxCount)
					l.boxes.add(boxNo);
				if(row > 0)
					l.boxes.add(boxNo - boxCount);
			}
		}
		
		for(int row = 1, boxNo = 0; row <= boxCount; ++row) {
			for(int col = 0; col <= boxCount; ++col) {
				Point2D.Double p1 = dots.get(row-1).get(col);
				Point2D.Double p2 = dots.get(row).get(col);
				Line l = new Line(p1.x, p1.y, p2.x, p2.y);
				lines.add(l);
				if(col < boxCount) {
					l.boxes.add(boxNo);
					++boxNo;
				}
				if(col > 0 && col != boxCount)
					l.boxes.add(boxNo - 2);
				else if(col == boxCount)
					l.boxes.add(boxNo - 1);
			}
		}

		Random r = new Random();
		final int sz = lines.size(); 
		for(int i = 0; i < excludedLineCount;) {
			int no = r.nextInt(sz);
			Line l = lines.get(no);
			if(l.isExcluded())
				continue;
			else{ 
				l.toggleExclusionState();
				++i;
			}
		}
		
		notifyNewGame();
	}
	
	public int getBoxCount() {
		return boxCount;
	}

	public ArrayList<Line> getLines() {
		return lines;
	}

	public ArrayList<ArrayList<Point2D.Double>> getDots() {
		return dots;
	}

	public Player getPlayer() {
		return player;
	}

	public void playerDoSelection(Line chosenLine) {
		boolean won = false;
		if(chosenLine.isUsable()) {
			chosenLine.selected(player);
			won = updateScores(chosenLine, player);
		} else if(chosenLine.isExcluded())
			chosenLine.toggleExclusionState();
		else
			return;
		
		notifyPlayerSelection(chosenLine);
		
		int count = 0;
		for(final Line l: lines) {
			if(l.isSelected())
				++count;
		}
		if(lines.size() == count) {
			Winners winners = new Winners();
			if(scores[Player.playerA.ix] < scores[Player.playerB.ix])
				winners.add(Player.playerB);
			else if(scores[Player.playerA.ix] > scores[Player.playerB.ix])
				winners.add(Player.playerA);
			else if(scores[Player.playerA.ix] == scores[Player.playerB.ix]) {
				winners.add(Player.playerA);
				winners.add(Player.playerB);
			}
			notifyGameIsOver(winners);
		}

		togglePlayer(won);
	}	

	private void togglePlayer(boolean won) {
		if(won) {
			if(playGame == PlayGame.oneUser && player == Player.playerB)
				playerDoSelection(ai.chooseBestLine(lines));
		} else {
			if(player == Player.playerA) {
				player = Player.playerB;
				if(playGame == PlayGame.oneUser)
					playerDoSelection(ai.chooseBestLine(lines));
			} else
				player = Player.playerA;
		}		
	}
	
	private boolean updateScores(Line selectedLine, Player player) {
		boolean won = false;
		for(final int no: selectedLine.boxes) {
			int selectedLineCount = 0;
			for(Line l: lines) {
				if(l != selectedLine && l.boxes.contains(no) && l.isSelected()) {
					++selectedLineCount;
				}
			}
			
			if(selectedLineCount == 3) {
				++scores[player.ix];
				won = true;
				notifyScores();
			}
		}
		return won;
	}
	
	private void resetScores() {
		scores[0] = scores[1] = 0;
		notifyScores();
	}
	
	private void notifyScores() {
		setChanged();
		notifyObservers(scores);
	}

	private void notifyGameIsOver(final Winners winners) {
		setChanged();
		notifyObservers(winners);
	}

	private void notifyPlayerSelection(final Line chosenLine) {
		setChanged();
		notifyObservers(chosenLine);
	}
	
	private void notifyNewGame() {
		setChanged();
		notifyObservers();
	}
}
