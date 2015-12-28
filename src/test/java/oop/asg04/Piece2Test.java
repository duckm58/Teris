package oop.asg04;

import junit.framework.TestCase;

import java.util.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class Piece2Test extends TestCase {
        // You can create data to be used in the your
        // test cases like this. For each run of a test method,
        // a new PieceTest object is created and setUp() is called
        // automatically by JUnit.
        // For example, the code below sets up some
        // pyramid and s pieces in instance variables
        // that can be used in tests.
        private Piece pyr1, pyr2, pyr3, pyr4;
        private Piece s, sRotated;

        protected void setUp() throws Exception {
                super.setUp();

                pyr1 = new Piece(Piece.PYRAMID_STR);
                pyr2 = pyr1.computeNextRotation();
                pyr3 = pyr2.computeNextRotation();
                pyr4 = pyr3.computeNextRotation();
               
                s = new Piece(Piece.S1_STR);
                sRotated = s.computeNextRotation();
        }
       
        // Here are some sample tests to get you started

        public void testSampleSize() {
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
        }
       
        public int numberOfUniqueNexts(Piece piece)
        {
                int count = 1;
                for (Piece next = piece.fastRotation(); next != piece; next = next.fastRotation())
                        count++;
                return count;
        }
       
        public void testFastRotation() {
                Piece[] pieces = Piece.getPieces();
                Piece square = pieces[5];
                // Check size of pyr piece
                assertSame(square, square.fastRotation());
               
                //this is a relatively thorough test of the rotation code
                assertEquals(1, numberOfUniqueNexts(pieces[Piece.SQUARE]));
                assertEquals(4, numberOfUniqueNexts(pieces[Piece.PYRAMID]));
                assertEquals(2, numberOfUniqueNexts(pieces[Piece.STICK]));
                assertEquals(4, numberOfUniqueNexts(pieces[Piece.L1]));
                assertEquals(4, numberOfUniqueNexts(pieces[Piece.L2]));
                assertEquals(2, numberOfUniqueNexts(pieces[Piece.S1]));
                assertEquals(2, numberOfUniqueNexts(pieces[Piece.S2]));
                assertSame(pieces, Piece.getPieces());
        }
       
        public void testRotationMore() {
                assertEquals(pyr4, pyr4);
                assertEquals(pyr1,pyr1.computeNextRotation().computeNextRotation().computeNextRotation().computeNextRotation());
        }
       
       
        // Test the skirt returned by a few pieces
        public void testSampleSkirt() {
                // Note must use assertTrue(Arrays.equals(... as plain .equals does not work
                // right for arrays.
                assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
                assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
               
                assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
                assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
        }
       }
        

