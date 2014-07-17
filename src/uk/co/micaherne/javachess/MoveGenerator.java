package uk.co.micaherne.javachess;

public class MoveGenerator {
	
	private Position position;
	public long[][] bbRayAttacks = new long[8][64];
	public long[] bbKnightAttacks = new long[64];

	public MoveGenerator(Position position) {
		this.position = position;
		initialiseRayAttacks();
		initialiseKnightAttacks();
	}

	private void initialiseRayAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			for (int directionIndex = 0; directionIndex < 9; directionIndex++) {
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.SW, Chess.Bitboard.DirectionIndex.SW, Chess.Bitboard.FILE_H | Chess.Bitboard.RANK_8);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.S,  Chess.Bitboard.DirectionIndex.S, Chess.Bitboard.RANK_8);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.SE, Chess.Bitboard.DirectionIndex.SE, Chess.Bitboard.FILE_A | Chess.Bitboard.RANK_8);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.W,  Chess.Bitboard.DirectionIndex.W, Chess.Bitboard.FILE_H);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.E,  Chess.Bitboard.DirectionIndex.E, Chess.Bitboard.FILE_A);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.NW, Chess.Bitboard.DirectionIndex.NW, Chess.Bitboard.FILE_H | Chess.Bitboard.RANK_1);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.N,  Chess.Bitboard.DirectionIndex.N, Chess.Bitboard.RANK_1);
				setRayAttack(origin, Chess.Bitboard.DirectionOffset.NE, Chess.Bitboard.DirectionIndex.NE, Chess.Bitboard.FILE_A | Chess.Bitboard.RANK_1);
			}
		}
	}
	
	
	private void initialiseKnightAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			bbKnightAttacks[origin] = 0;
			for (int i = 0; i < Chess.Bitboard.knightOffsets.length; i++) {
				int target = origin + Chess.Bitboard.knightOffsets[i];
				// TODO: Make an up to 5x5 mask of ranks and files to stop wrapping
				if (target >= 0 && target < 64) {
					bbKnightAttacks[origin] |= 1l << target;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param origin
	 * @param directionOffset
	 * @param directionIndex
	 * @param terminalSquares if directionOffset takes us to one of these squares, stop
	 */
	private void setRayAttack(int origin, int directionOffset, int directionIndex, long terminalSquares) {
		bbRayAttacks[directionIndex][origin] = 0;
		int next = origin + directionOffset;
		while (next < 64 && next >= 0) {
			if (((1L << next) & terminalSquares) != 0) {
				break;
			}
			bbRayAttacks[directionIndex][origin] |= (1L << next);
			next += directionOffset;
		}
	}

	/**
	 * An array of moves. 0 index is number of actual moves.
	 * 
	 * @return
	 */
	public int[] generateMoves() {
		int moveCount = 0;
		int[] result = new int[128];
		
		// Pawn moves
		if (position.whiteToMove) {
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.WHITE];
			long oneSquareMoves = (pawns << 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				if (lowestBit >= 56) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1 << lowestBit);
			}
			long twoSquareMoves = (oneSquareMoves & (Chess.Bitboard.RANK_1 << 16)) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit - 16, lowestBit);
				destinationSquares ^= (1 << lowestBit);
			}
		} else {
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.BLACK];
			long oneSquareMoves = (pawns >>> 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				if (lowestBit < 8) {
					result[moveCount] = MoveUtils.create(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1 << lowestBit);
			}
			long twoSquareMoves = (oneSquareMoves & (Chess.Bitboard.RANK_1 >> 16)) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit + 16, lowestBit);
				destinationSquares ^= (1 << lowestBit);
			}
		}
		
		// TODO: Pawn captures
		
		// TODO: Knight moves
		
		// TODO: Bishop moves
		
		// TODO: Queen moves
		
		// TODO: King moves
		
		// TODO: Castling
		
		result[0] = moveCount;
		
		return result;
	}

}
