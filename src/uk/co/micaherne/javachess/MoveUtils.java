package uk.co.micaherne.javachess;

/**
 * Utility class for dealing with moves, which are stored internally
 * as an int:
 * 
 * lowest 8 bits: from square
 * next 8 bits: to square
 * next 8 bits: promoted piece
 * next 1 bit: queening move?
 * next 1 bit: e.p. capture?
 * 
 * The promoted piece can be empty where the move is a generated prospective one and all 
 * promotions may be tried, whereas for specific moves (such as parsed from notation) the
 * promoted piece will be set.
 * 
 * @author Michael Aherne
 *
 */
public class MoveUtils {
	
	public static int create(int from, int to) {
		return from << 8 | to;
	}
	
	public static int create(int from, int to, boolean queening, boolean enPassentCapture) {
		return from << 8 | to | (queening ? 2 << 24 : 0) | (enPassentCapture ? 2 << 25 : 0);
	}
	
	public static int create(int from, int to, int promotedPiece) {
		// If promotedPiece is provided, it's a promotion and therefore 
		// also not an en passent capture
		return from << 8 | to | (2 << 24) | (promotedPiece << 16);
	}
	
	public static int fromSquare(int move) {
		return 255 & (move >>> 8);
	}

	public static int toSquare(int move) {
		return move & 255;
	}
	
	public static boolean isQueening(int move) {
		return ((move >>> 25) & 1) == 1;
	}
	
	public static boolean isEnPassentCapture(int move) {
		return ((move >>> 26) & 1) == 1;
	}

	public static int promotedPiece(int move) {
		return (move >>> 16) & 255;
	}
	
}
