// Piece.java
package oop.asg04;
import java.util.*;

/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.

 Typical client code looks like...
 <pre>
 Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 int width = pyra.getWidth();			// 3
 Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way

 Piece[] pieces = Piece.getPieces();	// the array of root pieces
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// get its width
 Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 </pre>
 */
public class Piece {
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation

	static private Piece[] pieces;	// singleton static array of first rotations

	/**
	 Defines a new piece given a TPoint[] array of its body.
	 Makes its own copy of the array and the TPoints inside it.
	 */
	public Piece(TPoint[] points) {
		body = new  TPoint [points.length];
		for (int i = 0 ; i < points.length ; i++)
		{
			body[i] = new TPoint(points[i]);
		}

		computeHeight();
		computeWidth();
		skirt = new int[width];
		Arrays.fill(skirt, height-1);
		for (TPoint pt : body)
		{
			if(skirt[pt.x]>pt.y)
				skirt[pt.x]=pt.y;
		}
		/*for ( int i = 0 ; i < body.length;i++){
			if ( skirt[body[i].x] > body[i].y){
				skirt[body[i].x] = body[i].y;
			}
		}*/
		skirt = Arrays.copyOfRange(skirt, 0, width);
	}

	/**
	 * Private helper method that computes the skirt of the piece.
	 */
	

	/**
	 * Private helper method that computes the height and width of the piece.
	 */
	private void computeWidth() {
		int widthMax;
		widthMax  =0;

		
		for ( int i = 0 ; i < body.length;i++){
			if ( widthMax < body[i].x)
				widthMax = body[i].x;
				
			
		}
		width =  widthMax +1;
		
	}
	private void computeHeight() {
		int heightMax;
		heightMax  =0;

		
		for ( int i = 0 ; i < body.length;i++){
			if ( heightMax < body[i].y)
				heightMax = body[i].y;
			
		}
		height = heightMax + 1;
		
	}

	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 Returns the width of the piece measured in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the piece measured in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 Returns a pointer to the piece's body. The caller
	 should not modify this array.
	 */
	public TPoint[] getBody() {
		return body;
	}

	/**
	 Returns a pointer to the piece's skirt. For each x value
	 across the piece, the skirt gives the lowest y value in the body.
	 This is useful for computing where the piece will land.
	 The caller should not modify this array.
	 */
	public int[] getSkirt() {
		return skirt;
	}


	/**
	 Returns a new piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		int Y = getHeight() - 1;

		TPoint[] fastResult = new TPoint[body.length];
		for (int i = 0; i < body.length; i++) {
			if (body[i].y == Y){
				fastResult[i] = new TPoint(0, body[i].x);
			}else if (body[i].y == 0){
				fastResult[i] = new TPoint(Y, body[i].x);
			}
			else
				fastResult[i] = new TPoint(body[i].y, body[i].x);
		}
		return new Piece(fastResult);
	}

	/**
	 Returns a pre-computed piece that is 90 degrees counter-clockwise
	 rotated from the receiver.	 Fast because the piece is pre-computed.
	 This only works on pieces set up by makeFastRotations(), and otherwise
	 just returns null.
	 */
	public Piece fastRotation() {
		return next;
	}



	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj == this) 
			return true;

		
		if (!(obj instanceof Piece)) 
			return false;
		Piece other = (Piece)obj;

		if(other.body.length != this.body.length) 
			return false;

		
		List<TPoint> point = Arrays.asList(this.body);
		List<TPoint> _point = Arrays.asList(other.body);

		return point.containsAll(_point);
		
		

	}


	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";

	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;

	
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
					makeFastRotations(new Piece(STICK_STR)),
					makeFastRotations(new Piece(L1_STR)),
					makeFastRotations(new Piece(L2_STR)),
					makeFastRotations(new Piece(S1_STR)),
					makeFastRotations(new Piece(S2_STR)),
					makeFastRotations(new Piece(SQUARE_STR)),
					makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}


		return Piece.pieces;
	}



	/**
	 Given the "first" root rotation of a piece, computes all
	 the other rotations and links them all together
	 in a circular list. The list loops back to the root as soon
	 as possible. Returns the root piece. fastRotation() relies on the
	 pointer structure setup here.
	 */
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	 */
	private static Piece makeFastRotations(Piece root) {
		if (root.equals(new Piece(SQUARE_STR)))
        {
            root.next = new Piece(SQUARE_STR);
        }
		root.RotationSequence(root);
		return root; // YOUR CODE HERE
	}
	private void RotationSequence(Piece head) {
		 
         
		Piece current_Piece = this;
		Piece next_Piece;
		while (true) {
			next_Piece = current_Piece.computeNextRotation();
			if (next_Piece.equals(head)) {
				current_Piece.next = head;
				break;
			} else {
				current_Piece.next = next_Piece;
			}
			current_Piece = next_Piece;
		}
	}



	/**
	 Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 the points into a TPoint[] array.
	 (Provided code)
	 */
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());

				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}

		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}




}
