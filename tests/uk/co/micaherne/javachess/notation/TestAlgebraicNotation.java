package uk.co.micaherne.javachess.notation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.javachess.Chess;
import uk.co.micaherne.javachess.NotationException;

public class TestAlgebraicNotation {
	
	private AlgebraicNotation notation;
	
	@Before
	public void setUp() throws Exception {
		notation = new AlgebraicNotation();
	}
	
	@Test(expected=NotationException.class)
	public void testToSquareWrongLength() throws NotationException {
		notation.toSquare("too long");
	}
	
	@Test
	public void testToSquare() throws NotationException {
		int square = notation.toSquare("a1");
		assertEquals(Chess.Square.A1, square);
		int square2 = notation.toSquare("h8");
		assertEquals(Chess.Square.H8, square2);
	}
	
	@Test
	public void testFromPiece() throws NotationException {
		String piece = notation.fromPiece(Chess.Piece.KING);
		assertEquals("K", piece);
	}

}
