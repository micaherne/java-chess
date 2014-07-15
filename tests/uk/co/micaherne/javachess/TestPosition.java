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
		System.out.println(position);
	}

}
