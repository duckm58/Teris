package oop.asg04;

import java.awt.*;

import javax.swing.*;

public class JBrainTetris extends JTetris {

	protected Brain.Move bestMove;
	

	protected DefaultBrain brain;
	protected boolean DEBUG = false;
	protected int currentCount;

	
	protected JCheckBox brainMode;
	protected JSlider adversary;
	
	protected JLabel adversaryStatus;

	public JBrainTetris(int pixels) {
		super(pixels);
		brain = new DefaultBrain();
		currentCount = 0;
		bestMove = new Brain.Move();
	}

	/**
	 * Creates a frame with a JBrainTetris.
	 */

	/**
	 * Override createControlPanel method to tack on the Brain label and
	 * JCheckBox. Also adds Animate Fall checkbox and Adversary slider.
	 * */
	@Override
	public JComponent createControlPanel() {

		JPanel panel = (JPanel) super.createControlPanel();

		// BRAIN CHECKBOX

		panel.add(new JLabel("Brain:"));
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);

		// Adversary - slider - ok status
		JPanel little = new JPanel();

		little.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0); // min, max, current
		adversary.setPreferredSize(new Dimension(100, 15));
		little.add(adversary); // now add little to panel of controls

		adversaryStatus = new JLabel("OK -");
		little.add(adversaryStatus);

		panel.add(little);

		return panel;
	}

	/**
	 * The strategy is to override tick(), so that every time the system calls
	 * tick(DOWN) to move the piece down one, JBrainTetris takes the opportunity
	 * to move the piece a bit first. Our rule is that the brain may do up to
	 * one rotation and one left/right move each time tick(DOWN) is called:
	 * rotate the piece one rotation and move it left or right one position.
	 * With the brain on, the piece should drift down to its correct place. We
	 * use the "Animate Falling" checkbox (default to true) to control how the
	 * brain works the piece once it is in the correct column but not yet
	 * landed. When animate is false, the brain can use the "DROP" command to
	 * drop the piece down into place. In any case, after the brain does its
	 * changes, the tick(DOWN) should have its usual effect of trying to lower
	 * the piece by one. So on each tick, the brain will move the piece a
	 * little, and the piece will drop down one row. The user should still be
	 * able to use the keyboard to move the piece around while the brain is
	 * playing, but the brain will move the piece back on course. As the board
	 * gets full, the brain may fail to get the piece over fast enough.
	 * */
	@Override
	public void tick(int verb) {
		boolean use_brain = ( brainMode.isSelected() && verb == DOWN );
		if ( use_brain) {			// if use_brain actived
			boolean result = (currentCount != count);
			computeBrainMoveIfNecessary(result);
			if (bestMove != null) {
				if (!currentPiece.equals(bestMove.piece)) {
					currentPiece = currentPiece.fastRotation();
					
				}
				if (bestMove.x > currentX){
					currentX++;
				}
				else if (bestMove.x < currentX){
					currentX--;
				}
			}
		}
		super.tick(verb);
	}
	protected void computeBrainMoveIfNecessary( boolean result){
		if( result){
			board.undo();
			bestMove = brain.bestMove(board, currentPiece, HEIGHT, bestMove);
		}
	}

	/**
	 * Override pickNextPiece to let adversary choose next piece based on slider
	 * position. If the slider is at 100, the adversary should always intervene.
	 * Create a random number between 1 and 99. If the random number is >= than
	 * the slider, then the piece should be chosen randomly as usual (just
	 * "super" on up). But if the random value is less, the mischief begins. In
	 * that case the "adversary" gets to pick the next piece. When the piece is
	 * chosen at random, setText() the status to "ok", otherwise set it to
	 * "*ok*".
	 * */
	@Override
	public Piece pickNextPiece() {
		int adversaryValue = adversary.getValue();
		int decider = random.nextInt(100);
		boolean result = decider >= adversaryValue;
		if ( result) {
			adversaryStatus.setText("ok");
			return super.pickNextPiece();
		} else {
			adversaryStatus.setText("_OK_ ");
			return computeWorstPossiblePiece();
		}
	}
	protected Piece computeWorstPossiblePiece()
	{
		Piece worstPiece = super.pickNextPiece();
		double worstScore = 0;
		for (Piece piece : pieces) {
			board.undo();
			Brain.Move nextMove = brain.bestMove(board, piece,
					board.getHeight(), null);
			if (nextMove != null && nextMove.score > worstScore) {
				worstPiece = piece;
				worstScore = nextMove.score;
			}
		}
		return worstPiece;
	}

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JBrainTetris tetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(tetris);
		frame.setVisible(true);
	}

}
