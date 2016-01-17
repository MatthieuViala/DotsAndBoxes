import java.util.ArrayList;
import java.util.Random;

public class ArtificialIntelligence {
	private Random r;
	
	ArtificialIntelligence() {
		r = new Random(System.currentTimeMillis());
	}
	
	public Line chooseBestLine(ArrayList<Line> lines) {
		ArrayList<Line> usables = new ArrayList<Line>();
		ArrayList<Line> betters = new ArrayList<Line>();
		ArrayList<Line> LastChoice = new ArrayList<Line>();
		for(Line l1: lines) {
			if(!l1.isSelected() || l1.isExcluded()) {
				usables.add(l1);
				for(final int no: l1.boxes) {
					int lCount = 0;
					for(Line l2: lines) {
						if(l2.boxes.contains(no) && l2.isSelected()) {
							++lCount;
						}
					}
					
					if(lCount == 3)
						betters.add(l1);
					if(lCount == 2){
						LastChoice.add(l1);
						usables.remove(l1);
					}
				}
			}
		}
		if(usables.isEmpty() && betters.isEmpty() && LastChoice.isEmpty())
			return null;
		if(usables.isEmpty() && betters.isEmpty())
			return LastChoice.get(r.nextInt(LastChoice.size()));
		else if(betters.isEmpty())
			return usables.get(r.nextInt(usables.size()));
		else
			return betters.get(r.nextInt(betters.size()));
	}
}