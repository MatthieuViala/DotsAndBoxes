import javax.swing.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;

public class GUI implements ActionListener {
	private enum Boxes {
		box2x2(2), box3x3(3), box4x4(4), box5x5(5);
		final int count;
		Boxes(int count) {
			this.count = count;
		}
		
		public int count() {
			return count;
		}
	}

	private int lineCount() {
		return box.count*box.count*2 + box.count*2;
	}
	
	private JLabel scores = new JLabel("Scores: 0 - 0");
	private JFrame frame = new JFrame();
	private Boxes box = Boxes.box2x2;
	public DotsAndBoxes.PlayGame playGame = DotsAndBoxes.PlayGame.oneUser;
	private DBPainter painter;
	private JSpinner excludedLineSpinner = new JSpinner(new SpinnerNumberModel(0, 0, lineCount(), 1));

	public GUI() {
		// Check button two select the playGame game (IA or two users).
		JRadioButton twoUsers = new JRadioButton("Two users");
		twoUsers.setActionCommand(DotsAndBoxes.PlayGame.twoUsers.toString());
		JRadioButton oneUserVsIA = new JRadioButton("One user vs IA");
		oneUserVsIA.setActionCommand(DotsAndBoxes.PlayGame.oneUser.toString());
		oneUserVsIA.setSelected(true);
		
		twoUsers.addActionListener(this);
		oneUserVsIA.addActionListener(this);
		
		// Group the box radio buttons.
		ButtonGroup playGroup = new ButtonGroup();
		playGroup.add(twoUsers);
		playGroup.add(oneUserVsIA);
		
		// Radio button to select the boxes#.
		JRadioButton box2x2 = new JRadioButton("2×2 Boxes");
		box2x2.setActionCommand(Boxes.box2x2.toString());
		box2x2.setSelected(true);
		JRadioButton box3x3 = new JRadioButton("3×3 Boxes");
		box3x3.setActionCommand(Boxes.box3x3.toString());
		JRadioButton box4x4 = new JRadioButton("4×4 Boxes");
		box4x4.setActionCommand(Boxes.box4x4.toString());
		JRadioButton box5x5 = new JRadioButton("5×5 Boxes");
		box5x5.setActionCommand(Boxes.box5x5.toString());
		
		// Group the box radio buttons.
		ButtonGroup boxGroup = new ButtonGroup();
		boxGroup.add(box2x2);
		boxGroup.add(box3x3);
		boxGroup.add(box4x4);
		boxGroup.add(box5x5);
		
		box2x2.addActionListener(this);
		box3x3.addActionListener(this);
		box4x4.addActionListener(this);
		box5x5.addActionListener(this);
		
		// Set Apply & Restart button.
		JButton restart = new JButton("Apply & Restart Game");
		restart.setAlignmentX(Component.CENTER_ALIGNMENT);
		restart.setActionCommand("restart");
		restart.addActionListener(this);
		
		JLabel spinnerLabel = new JLabel("Excluded Line#: ");
		spinnerLabel.setLabelFor(excludedLineSpinner);

		// the panel with the button and text
		JPanel ctrlPanel = new JPanel();
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.PAGE_AXIS));
		ctrlPanel.add(twoUsers);
		ctrlPanel.add(oneUserVsIA);
		ctrlPanel.add(Box.createRigidArea(new Dimension(0,30)));
		ctrlPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		ctrlPanel.add(box2x2);
		ctrlPanel.add(box3x3);
		ctrlPanel.add(box4x4);
		ctrlPanel.add(box5x5);
		ctrlPanel.add(Box.createRigidArea(new Dimension(0,30)));
		ctrlPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.LINE_AXIS));
		spinnerPanel.add(spinnerLabel);
		spinnerPanel.add(excludedLineSpinner);
		ctrlPanel.add(spinnerPanel);
		ctrlPanel.add(Box.createRigidArea(new Dimension(0,30)));
		ctrlPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		ctrlPanel.add(restart);
		ctrlPanel.add(Box.createRigidArea(new Dimension(0,20)));
		ctrlPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		ctrlPanel.add(scores);
		ctrlPanel.add(Box.createRigidArea(new Dimension(0,20)));
		ctrlPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		
		painter = new DBPainter(this, Boxes.box2x2.count());
		
		JPanel gamePanel = new JPanel();
		gamePanel.add(painter);
		
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.LINE_AXIS));
		guiPanel.add(ctrlPanel);
		guiPanel.add(gamePanel);
		
		// set up the frame and display it
		frame.add(guiPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Dots & Boxes");
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();
		if(cmd.equals(DotsAndBoxes.PlayGame.oneUser.toString()))
			playGame = DotsAndBoxes.PlayGame.oneUser;
		else if(cmd.equals(DotsAndBoxes.PlayGame.twoUsers.toString()))
			playGame = DotsAndBoxes.PlayGame.twoUsers;
		else if(cmd.equals(Boxes.box2x2.toString()))
			box = Boxes.box2x2;
		else if(cmd.equals(Boxes.box3x3.toString()))
			box = Boxes.box3x3;
		else if(cmd.equals(Boxes.box4x4.toString()))
			box = Boxes.box4x4;
		else if(cmd.equals(Boxes.box5x5.toString()))
			box = Boxes.box5x5;
		else if(cmd.equals("restart"))
			playGame();
	}
	
	public void playGame() {
		SpinnerNumberModel mdl = (SpinnerNumberModel)excludedLineSpinner.getModel(); 
		mdl.setMaximum(lineCount());
		painter.restart(playGame, box.count(), (int)mdl.getValue());
	}
	
	public void gameIsOver(DotsAndBoxes.Winners winners) {
		String msg = "Game is over!\n";
		if(winners.count() > 1)
			msg += "Draw (retry)";
		else {
			String result = "Player A";
			if(playGame == DotsAndBoxes.PlayGame.oneUser) {
				if(winners.first() == DotsAndBoxes.Player.playerB)
					result = "IA";
			} else {
				if(winners.first() == DotsAndBoxes.Player.playerB)
					result = "Player B";
			}
			msg += String.format("The winner is: %s", result);
		}
		
		Object[] options = {"Retry", "Stop"};
		final int opt = JOptionPane.showOptionDialog(
			frame,
			msg,
			"Dots & Boxes result",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.INFORMATION_MESSAGE,
		    null,
		    options,
		    options[0]);
		if(opt == 1 || opt == -1)
			frame.dispose();
		else
			playGame();
	}

	public void updateScores(int[] scores) {
		this.scores.setText(String.format("Scores: %d - %d", scores[0], scores[1]));
	}
	
	// create one Frame
	public static void main(String[] args) {
		new GUI();
	}
}