package uk.co.micaherne.javachess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestPosition {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFromFEN() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
	}
	
	@Test
	public void testMove() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		int move = MoveUtils.create(Chess.Square.E2, Chess.Square.E4);
		position.move(move);
		// System.out.println(position);
		int move2 = MoveUtils.create(Chess.Square.E7, Chess.Square.E5);
		position.move(move2);
		// System.out.println(position);
	}
	
	@Test
	public void testEP() throws NotationException {
		Position position = Position.fromFEN("r1bqkbnr/ppp2ppp/2np4/4pP2/4P3/8/PPPP2PP/RNBQKBNR w - -");
		int move = MoveUtils.create(Chess.Square.F5, Chess.Square.E6, false, true);
		position.move(move);
		// System.out.println(position);
	}
	
	/**
	 * @throws NotationException
	 */
	@Test
	public void testInitialisePieceBitboards() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		position.initialisePieceBitboards();
		System.out.println(BitboardUtils.toString(position.pieceBitboards[Chess.Bitboard.OCCUPIED]));
		assertEquals(2, Long.numberOfLeadingZeros(position.pieceBitboards[Chess.Piece.BISHOP]));
	}

}
