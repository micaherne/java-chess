package uk.co.micaherne.javachess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.javachess.notation.LongAlgebraicNotation;

public class MoveGeneratorTests {

	private Position position;
	private MoveGenerator moveGenerator;

	@Before
	public void setUp() throws Exception {
		position = Position.fromFEN(Chess.START_POS_FEN);
		moveGenerator = new MoveGenerator(position);
	}

	@Test
	public void testMoveGenerator() throws NotationException {
		
		// System.out.println(BitboardUtils.toString(moveGenerator.bbKnightAttacks[Chess.Square.E4]));
		// System.out.println(BitboardUtils.toString(moveGenerator.bbKnightAttacks[Chess.Square.H5]));
	}

	@Test
	public void testGenerateMoves() throws NotationException {
		LongAlgebraicNotation notation = new LongAlgebraicNotation();
		int[] moves = moveGenerator.generateMoves();
		assertEquals(20, moves[0]);
		
		position.whiteToMove = false;
		int[] bmoves = moveGenerator.generateMoves();
		/*for (int i = 1; i <= bmoves[0]; i++) {
			System.out.println(notation.toString(bmoves[i]));
		}*/
		assertEquals(20, bmoves[0]);
	}
	
	@Test
	public void testTemp() throws NotationException {
		/*//System.out.println(BitboardUtils.toString(moveGenerator.bbKingAttacks[Chess.Square.A8]));
		Position position2 = Position.fromFEN("3kQ3/8/8/8/8/8/8/8 b - -");
		MoveGenerator moveGenerator2 = new MoveGenerator(position2);
		long start = System.nanoTime();
		for (int i = 0; i < 1000000; i++) {
			int[] moves = moveGenerator.generateMoves();
		}
		System.out.println("Time taken: " + (System.nanoTime() - start) + " nanoseconds");
		//assertEquals(5, moves[0]);
*/	}

}
