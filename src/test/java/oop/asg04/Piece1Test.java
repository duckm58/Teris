package oop.asg04;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class Piece1Test {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4, pyr5;
	private Piece s, sRotated;

	@Before
	public void setUp() throws Exception {
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		pyr5 = pyr4.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}
	
	// Here are some sample tests to get you started
	
	@Test
	public void testSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
		
		Piece l2 = l.computeNextRotation();
		assertEquals(4, l2.getWidth());
		assertEquals(1, l2.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	@Test
	public void testSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}
	
	@Test
	public void testEquals(){
		Piece piece1 = new Piece("0 0	0 1	 0 2  0 3");
		Piece piece2 = new Piece("0 3	0 2	 0 1  0 0");
		Piece piece3 = new Piece("1 3	0 2	 0 1  0 0");
		
		assertTrue(piece1.equals(piece1));
		assertTrue(piece1.equals(piece2));
		assertTrue(!piece1.equals(piece3));
		
		
	}
	
	@Test
	public void testGetPieces(){
		Piece[] pieces = Piece.getPieces();
		Piece L1 = pieces[1];
		Piece L1_2 = L1.fastRotation();
		Piece L1_2_2 = L1.computeNextRotation();
		Piece L1_3 = L1_2.fastRotation();
		Piece L1_4 = L1_3.fastRotation();
		Piece L1_5 = L1_4.fastRotation();
		
		assertEquals(3, L1.getHeight());
		assertEquals(3, L1_2.getWidth());
		assertTrue(L1_2.equals(L1_2_2));
		assertTrue(!(L1 == L1_2));
		assertTrue(L1 == L1_5);
	}
	
	@Test
    public void testEquals3() {
        pyr1 = new Piece(Piece.PYRAMID_STR);
        pyr2 = pyr1.computeNextRotation();
        pyr3 = pyr2.computeNextRotation();
        pyr4 = pyr3.computeNextRotation();
        pyr5 = pyr4.computeNextRotation();
        assertTrue(pyr1.equals(pyr5));
    }
	
	@Test
	public void testRootPiece() {
        assertEquals(0, pyr1.getBody()[0].x);
        assertEquals(1, pyr1.getBody()[1].x);
        assertEquals(1, pyr1.getBody()[2].x);
        assertEquals(2, pyr1.getBody()[3].x);
}
}

