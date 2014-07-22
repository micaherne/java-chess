package uk.co.micaherne.javachess;

public class MoveGenerator {
	
	public Position position;
	public long[][] bbRayAttacks = new long[8][64];
	public long[] bbKnightAttacks = new long[64];
	public long[] bbKingAttacks = new long[64];

	public MoveGenerator(Position position) {
		this.position = position;
		initialiseRayAttacks();
		initialiseKnightAttacks();
		initialiseKingAttacks();
	}

	private void initialiseRayAttacks() {
		for (int origin = 0; origin < 64; origin++) {
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
	
	private void initialiseKnightAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			// Generate masks to prevent wrapping
			long fileMask = 0L;
			long rankMask = 0L;
			int originFile = origin % 8;
			int originRank = origin / 8;
			for (int j = 0; j < 8; j++) {
				if (Math.abs(j - originFile) < 3) {
					fileMask |= Chess.Bitboard.files[j];
				}
				if (Math.abs(j - originRank) < 3) {
					rankMask |= Chess.Bitboard.ranks[j];
				}
			}
			
			bbKnightAttacks[origin] = 0;
			for (int i = 0; i < Chess.Bitboard.knightOffsets.length; i++) {
				
				int target = origin + Chess.Bitboard.knightOffsets[i];
				if (target >= 0 && target < 64) {
					bbKnightAttacks[origin] |= 1l << target;
				}
				
			}
			bbKnightAttacks[origin] &= fileMask;
			bbKnightAttacks[origin] &= rankMask;
		}
	}
	
	private void initialiseKingAttacks() {
		for (int origin = 0; origin < 64; origin++) {
			// Generate masks to prevent wrapping
			long fileMask = 0L;
			long rankMask = 0L;
			int originFile = origin % 8;
			int originRank = origin / 8;
			for (int j = 0; j < 8; j++) {
				if (Math.abs(j - originFile) < 2) {
					fileMask |= Chess.Bitboard.files[j];
				}
				if (Math.abs(j - originRank) < 2) {
					rankMask |= Chess.Bitboard.ranks[j];
				}
			}
			
			bbKingAttacks[origin] = 0;
			for (int i = 0; i < Chess.Bitboard.kingOffsets.length; i++) {
				
				int target = origin + Chess.Bitboard.kingOffsets[i];
				if (target >= 0 && target < 64) {
					bbKingAttacks[origin] |= 1l << target;
				}
				
			}
			bbKingAttacks[origin] &= fileMask;
			bbKingAttacks[origin] &= rankMask;
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
		
		int colourToMove;
		int oppositeColour;
		// Pawn moves
		if (position.whiteToMove) {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.WHITE];
			long oneSquareMoves = (pawns << 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (destinationSquares != 0L) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				if (lowestBit >= 56) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1L << lowestBit);
			}
			long twoSquareMoves = ((oneSquareMoves & (Chess.Bitboard.RANK_3)) << 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (destinationSquares != 0L) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit - 16, lowestBit);
				destinationSquares ^= (1L << lowestBit);
			}
			
			// For using later
			colourToMove = Chess.Colour.WHITE;
			oppositeColour = Chess.Colour.BLACK;
		} else {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.BLACK];
			long oneSquareMoves = (pawns >>> 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			long destinationSquares = oneSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				if (lowestBit < 8) {
					result[moveCount] = MoveUtils.create(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit + Chess.Bitboard.DirectionOffset.N, lowestBit);
				}
				destinationSquares ^= (1L << lowestBit);
			}
			long twoSquareMoves = ((oneSquareMoves & (Chess.Bitboard.RANK_6)) >>> 8) & ~position.pieceBitboards[Chess.Bitboard.OCCUPIED];
			destinationSquares = twoSquareMoves;
			while (Long.bitCount(destinationSquares) != 0) {
				int lowestBit = Long.numberOfTrailingZeros(destinationSquares);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit + 16, lowestBit);
				destinationSquares ^= (1L << lowestBit);
			}
			
			colourToMove = Chess.Colour.BLACK;
			oppositeColour = Chess.Colour.WHITE;
		}
		
		// Pawn captures
		long targets = (position.epSquare | position.pieceBitboards[Chess.Bitboard.OCCUPIED]) & ~position.colourBitboards[colourToMove];
		if (position.whiteToMove) {
			
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.WHITE];
			long captureNW = targets & ((~Chess.Bitboard.FILE_A & pawns) << 7);
			while (captureNW != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureNW);
				moveCount++;
				if (lowestBit >= 56) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NW, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NW, lowestBit);
				}
				captureNW ^= (1L << lowestBit);
			}
			
			long captureNE = targets & ((~Chess.Bitboard.FILE_H & pawns) << 9);
			while (captureNE != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureNE);
				moveCount++;
				if (lowestBit >= 56) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NE, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.NE, lowestBit);
				}
				captureNE ^= (1L << lowestBit);
			}
		} else {
			long pawns = position.pieceBitboards[Chess.Piece.PAWN] & position.colourBitboards[Chess.Colour.BLACK];
			long captureSW = targets & ((~Chess.Bitboard.FILE_A & pawns) >>> 9);
			while (captureSW != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureSW);
				moveCount++;
				if (lowestBit < 8) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SW, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SW, lowestBit);
				}
				captureSW ^= (1L << lowestBit);
			}
			
			long captureSE = targets & ((~Chess.Bitboard.FILE_H & pawns) >>> 7);
			while (captureSE != 0) {
				int lowestBit = Long.numberOfTrailingZeros(captureSE);
				moveCount++;
				if (lowestBit < 8) {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SE, lowestBit, true, false);
				} else {
					result[moveCount] = MoveUtils.create(lowestBit - Chess.Bitboard.DirectionOffset.SE, lowestBit);
				}
				captureSE ^= (1L << lowestBit);
			}
		}
		// Knight moves
		long knights = position.pieceBitboards[Chess.Piece.KNIGHT] & position.colourBitboards[colourToMove];
		while (knights != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(knights);
			long moves = bbKnightAttacks[lowestBit] & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			knights ^= (1L << lowestBit);
		}
		
		// Bishop moves (+ queen diagonals)
		long bishopsAndQueens = (position.pieceBitboards[Chess.Piece.BISHOP] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[colourToMove];
		while (bishopsAndQueens != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(bishopsAndQueens);
			long moves = positiveRayAttacks(Chess.Bitboard.DirectionIndex.NW, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = positiveRayAttacks(Chess.Bitboard.DirectionIndex.NE, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = negativeRayAttacks(Chess.Bitboard.DirectionIndex.SW, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = negativeRayAttacks(Chess.Bitboard.DirectionIndex.SE, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			bishopsAndQueens ^= (1L << lowestBit);
		}
		
		// Rook moves (+ queen linear)
		long rooksAndQueens = (position.pieceBitboards[Chess.Piece.ROOK] | position.pieceBitboards[Chess.Piece.QUEEN]) & position.colourBitboards[colourToMove];
		while (rooksAndQueens != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(rooksAndQueens);
			long moves = positiveRayAttacks(Chess.Bitboard.DirectionIndex.N, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = positiveRayAttacks(Chess.Bitboard.DirectionIndex.E, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = negativeRayAttacks(Chess.Bitboard.DirectionIndex.W, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			moves = negativeRayAttacks(Chess.Bitboard.DirectionIndex.S, lowestBit) & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			rooksAndQueens ^= (1L << lowestBit);
		}
		
		// Queen moves are done.
		
		// King moves
		long kings = position.pieceBitboards[Chess.Piece.KING] & position.colourBitboards[colourToMove];
		while (kings != 0L) {
			int lowestBit = Long.numberOfTrailingZeros(kings);
			long moves = bbKingAttacks[lowestBit] & ~position.colourBitboards[colourToMove];
			while (moves != 0L) {
				int lowestMoveBit = Long.numberOfTrailingZeros(moves);
				moveCount++;
				result[moveCount] = MoveUtils.create(lowestBit, lowestMoveBit);
				moves ^= (1L << lowestMoveBit);
			}
			kings ^= (1L << lowestBit);
		}
		
		// TODO: Castling
		
		result[0] = moveCount;
		
		return result;
	}
	
	/**
	 * Get a bitboard of all the squares attacked by a piece on square origin
	 * in direction directionIndex. This contains the first blocker which may
	 * be the same colour as the moving piece.
	 * 
	 * The logic of this is from https://chessprogramming.wikispaces.com/Classical+Approach
	 * 
	 * @param directionIndex
	 * @param origin
	 * @return
	 */
	public long positiveRayAttacks(int directionIndex, int origin) {
		long attacks = bbRayAttacks[directionIndex][origin];
		long blocker = attacks & position.pieceBitboards[Chess.Bitboard.OCCUPIED];
		if (blocker != 0L) {
			int square = Long.numberOfTrailingZeros(blocker);
			attacks ^= bbRayAttacks[directionIndex][square];
		}
		return attacks;
	}
	
	public long negativeRayAttacks(int directionIndex, int origin) {
		long attacks = bbRayAttacks[directionIndex][origin];
		long blocker = attacks & position.pieceBitboards[Chess.Bitboard.OCCUPIED];
		if (blocker != 0L) {
			int square = 63 - Long.numberOfLeadingZeros(blocker);			
			attacks ^= bbRayAttacks[directionIndex][square];
		}
		return attacks;
	}

}
