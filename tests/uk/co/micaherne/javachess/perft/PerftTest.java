package uk.co.micaherne.javachess.perft;

import static org.junit.Assert.*;

import java.util.Map;

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
	public void testInitPos() throws NotationException {
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
	
	@Test
	public void testKiwiPete() throws NotationException {
		Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		PerftResult p = Perft.perft(position, 1);
		assertEquals(48, p.moveCount);
		PerftResult p2 = Perft.perft(position, 2);
		assertEquals(2039, p2.moveCount);
	}
	
	@Test
	public void testDivide() throws NotationException {
		Position position = Position.fromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
		Map<String, Long> divide = Perft.divide(position, 2);
		for (String key : divide.keySet()) {
			System.out.println(key + " " + divide.get(key));
		}
	}

}
