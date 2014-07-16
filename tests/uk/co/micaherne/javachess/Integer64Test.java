package uk.co.micaherne.javachess;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Integer64Test {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testOne() {
		long l1 = -1L;
		assertEquals(1, l1 >>> 63);
		/*System.out.println(Chess.Square.A1);
		System.out.println(Chess.Piece.BISHOP);*/
		
	}
	
	@Test
	public void testTwo() {
		long[] colourBitboards = new long[2];
		//System.out.println(colourBitboards[0]);
	}
	
	public void generateSquares() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				StringBuffer file = new StringBuffer("public static final int ");
				file.append((char)('A' + j));
				file.append(i + 1);
				file.append(" = ");
				file.append(i * 8 + j);
				file.append(";");
				System.out.println(file.toString());
			}
		}
	}
	
	@Test
	public void generateLines() {
		long fileA = 1L;
		long rank1 = 1L;
		for (int i = 0; i < 7; i++) {
			fileA |= (fileA << 8);
			rank1 |= (rank1 << 1);
		}
		long fileH = fileA << 7;
		long rank8 = rank1 << 56;
		
		System.out.println("public static final long FILE_A = " + fileA + "L;\n" );
		System.out.println("public static final long FILE_H = " + fileH + "L;\n" );
		System.out.println("public static final long RANK_1 = " + rank1 + "L;\n" );
		System.out.println("public static final long FILE_8 = " + rank8 + "L;\n" );
	}
	

}
