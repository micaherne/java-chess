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
	}

}
