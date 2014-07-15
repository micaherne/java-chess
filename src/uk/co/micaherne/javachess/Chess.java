package uk.co.micaherne.javachess;

/**
 * Class for defining widely-used values.
 * 
 * @author Michael Aherne
 *
 */
/**
 * @author michael
 *
 */
/**
 * @author michael
 *
 */
public class Chess {
	
	public static class Piece {
		public static final int EMPTY = 0;
		public static final int PAWN = 1;
		public static final int ROOK = 2;
		public static final int KNIGHT = 3;
		public static final int BISHOP = 4;
		public static final int QUEEN = 5;
		public static final int KING = 6;
		
		public static class White {
			public static final int PAWN = 1;
			public static final int ROOK = 2;
			public static final int KNIGHT = 3;
			public static final int BISHOP = 4;
			public static final int QUEEN = 5;
			public static final int KING = 6;
		}
		
		// Black has bit 4 set
		public static class Black {
			public static final int PAWN = 9;
			public static final int ROOK = 10;
			public static final int KNIGHT = 11;
			public static final int BISHOP = 12;
			public static final int QUEEN = 13;
			public static final int KING = 14;
		}
	}
	
	public static class Colour {
		public static final int BLACK = 0;
		public static final int WHITE = 1;
	}
	
	
	/**
	 * Piece constants. These are used for both array indices and bit position in
	 * bitboards.
	 * 
	 * @author Michael Aherne
	 *
	 */
	public static class Square {
		
		public static final int A1 = 0;
		public static final int B1 = 1;
		public static final int C1 = 2;
		public static final int D1 = 3;
		public static final int E1 = 4;
		public static final int F1 = 5;
		public static final int G1 = 6;
		public static final int H1 = 7;
		public static final int A2 = 8;
		public static final int B2 = 9;
		public static final int C2 = 10;
		public static final int D2 = 11;
		public static final int E2 = 12;
		public static final int F2 = 13;
		public static final int G2 = 14;
		public static final int H2 = 15;
		public static final int A3 = 16;
		public static final int B3 = 17;
		public static final int C3 = 18;
		public static final int D3 = 19;
		public static final int E3 = 20;
		public static final int F3 = 21;
		public static final int G3 = 22;
		public static final int H3 = 23;
		public static final int A4 = 24;
		public static final int B4 = 25;
		public static final int C4 = 26;
		public static final int D4 = 27;
		public static final int E4 = 28;
		public static final int F4 = 29;
		public static final int G4 = 30;
		public static final int H4 = 31;
		public static final int A5 = 32;
		public static final int B5 = 33;
		public static final int C5 = 34;
		public static final int D5 = 35;
		public static final int E5 = 36;
		public static final int F5 = 37;
		public static final int G5 = 38;
		public static final int H5 = 39;
		public static final int A6 = 40;
		public static final int B6 = 41;
		public static final int C6 = 42;
		public static final int D6 = 43;
		public static final int E6 = 44;
		public static final int F6 = 45;
		public static final int G6 = 46;
		public static final int H6 = 47;
		public static final int A7 = 48;
		public static final int B7 = 49;
		public static final int C7 = 50;
		public static final int D7 = 51;
		public static final int E7 = 52;
		public static final int F7 = 53;
		public static final int G7 = 54;
		public static final int H7 = 55;
		public static final int A8 = 56;
		public static final int B8 = 57;
		public static final int C8 = 58;
		public static final int D8 = 59;
		public static final int E8 = 60;
		public static final int F8 = 61;
		public static final int G8 = 62;
		public static final int H8 = 63;
		
	}
	
	public enum BitboardType {
		WHITE,
		BLACK,
		PAWNS,
		ROOKS,
		KNIGHTS,
		BISHOPS,
		QUEENS,
		KINGS,
		EP
	}
	
	public static enum NotationType {
		COORDINATE, LONG_ALGEBRAIC
	}
	
	public static final String START_POS_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";


}
