package uk.co.micaherne.javachess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MoveGeneratorTests {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMoveGenerator() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		MoveGenerator moveGenerator = new MoveGenerator(position);
		// System.out.println(BitboardUtils.toString(moveGenerator.bbRayAttacks[Chess.Bitboard.DirectionIndex.E][Chess.Square.D1]));
	}

	@Test
	public void testGenerateMoves() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		MoveGenerator moveGenerator = new MoveGenerator(position);
		assertEquals(20, moveGenerator.generateMoves()[0]);
	}

}
