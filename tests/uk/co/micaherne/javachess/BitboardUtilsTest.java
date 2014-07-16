package uk.co.micaherne.javachess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BitboardUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testToStringLong() {
		System.out.println(BitboardUtils.toString(-1));
	}

}
