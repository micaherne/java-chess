package uk.co.micaherne.javachess.perft;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.co.micaherne.javachess.Chess;
import uk.co.micaherne.javachess.NotationException;
import uk.co.micaherne.javachess.Position;

public class PerftTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInitialisation() throws NotationException {
		Position position = Position.fromFEN(Chess.START_POS_FEN);
		PerftResult p = Perft.perft(position, 1);
		assertEquals(20, p.moveCount);
		PerftResult p2 = Perft.perft(position, 2);
		assertEquals(400, p2.moveCount);
		PerftResult p3 = Perft.perft(position, 3);
		assertEquals(8902, p3.moveCount);
		PerftResult p4 = Perft.perft(position, 4);
		assertEquals(197281, p4.moveCount);
		// PerftResult p5 = Perft.perft(position, 5);
		// assertEquals(4865609, p5.moveCount);
	}

}
