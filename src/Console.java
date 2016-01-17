import java.util.ArrayList;
import java.util.Scanner;

public class Console {
	public static void main(String [] args) {
		Scanner input = new Scanner(System.in);
		ConsoleObserver obs = new ConsoleObserver();
		
		while(true) {
			System.out.print("Select One Player vs IA (1), Two Players (2) or Exit (-1): ");
			final int playGame = input.nextInt();
			if(playGame < 0)
				break;
			if(playGame > 2)
				continue;
			
			System.out.print("Select number of Boxes: ");
			final int boxCount = input.nextInt();
			if(boxCount < 2 || boxCount > 5)
				continue;
			
			System.out.print("Select number of excluded lines: ");
			final int lineCount = input.nextInt();
			if(lineCount < 0)
				continue;
			
			DotsAndBoxes dab =
				new DotsAndBoxes(
					playGame == 1 ? DotsAndBoxes.PlayGame.oneUser : DotsAndBoxes.PlayGame.twoUsers,
					boxCount,
					lineCount,
					obs);
			
			while(!obs.gameOver()) {
				System.out.format("%s Choose your line no: ", dab.getPlayer());
				final int lineNo = input.nextInt();
				
				ArrayList<Line> lines = dab.getLines();
				if(lineNo < 0 || lineNo > lines.size())
					continue;
				dab.playerDoSelection(lines.get(lineNo));
			}
		}
		input.close();
		System.out.print("Closing app");
		System.exit(0);
	}
}