package uk.co.micaherne.javachess;

import uk.co.micaherne.javachess.Chess.NotationType;
import uk.co.micaherne.javachess.notation.AlgebraicNotation;

public class Position {
	
	
	
	public long[] pieceBitboards;
	public long[] colourBitboards;
	
	public int[] board;
	
	public boolean castleWhiteKing = false;
	public boolean castleWhiteQueen = false;
	public boolean castleBlackKing = false;
	public boolean castleBlackQueen = false;
	
	// colour, (q, k)
	public boolean castling[][];
	
	public int moves = 0;
	public int halfMoves = 0;
	
	public int epSquare = 0;
	
	public boolean whiteToMove = false;
	
	public Position() {
		board = new int[64];
		pieceBitboards = new long[7];
		colourBitboards = new long[2];
		castling = new boolean[][]{{false, false}, {false, false}};
	}
	
	public void initialisePieceBitboards() {
		colourBitboards[0] = 0L;
		colourBitboards[1] = 0L;
		for (int i = 0; i < 7; i++) {
			pieceBitboards[i] = 0L;
		}
		for (int i = 0; i < 64; i++) {
			if (board[i] == Chess.Piece.EMPTY) {
				continue;
			}
			pieceBitboards[board[i] & 7] |= (1L << i);
			pieceBitboards[Chess.Bitboard.OCCUPIED] |= (1L << i);
			colourBitboards[(board[i] >> 3) & 1] |= (1L << i);
		}
	}
	
	public static Position fromFEN(String fen) throws NotationException {
		Position result = new Position();
		AlgebraicNotation notation = new AlgebraicNotation();
		String[] fenParts = fen.split(" ");
		String[] boardParts = fenParts[0].split("\\/");

		for (int i = 0; i < 8; i++) {
			String rank = null;
			try {
				rank = boardParts[7 - i];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new FENException("Not enough ranks");
			}

			int file = 0;
			for (String c : rank.split("")) {
				if ("".equals(c)) {
					continue;
				}
				if (!c.matches("\\d")) {
					result.board[i * 8 + file] = notation.toPiece(c);
					file++;
				} else {
					int cint = Integer.parseInt(c);
					while (cint > 0) {
						result.board[i * 8 + file] = Chess.Piece.EMPTY;
						cint--;
						file++;
					}
				}
			}
			if (file != 8) {
				throw new FENException("Not enough files");
			}
		}

		result.whiteToMove = "w".equals(fenParts[1]);

		if (!"-".equals(fenParts[2])) {
			for (String c : fenParts[2].trim().split("")) {
				if ("".equals(c)) {
					continue;
				}
				int piece = notation.toPiece(c);
				result.castling[piece >> 3][(piece % 8) - 5] = true;
			}
		}
		if (!"-".equals(fenParts[3])) {
			result.epSquare = notation.toSquare(fenParts[3]);
		}

		if (fenParts.length > 4) {
			try {
				result.halfMoves = Integer.parseInt(fenParts[4]);
			} catch (NumberFormatException e) {
				throw new FENException("halfmove must be integer", e);
			}
		}

		if (fenParts.length > 5) {
			try {
				result.moves = Integer.parseInt(fenParts[5]);
			} catch (NumberFormatException e) {
				throw new FENException("fullmove must be integer", e);
			}
		}
		
		result.initialisePieceBitboards();
		
		return result;
	}

	@Override
	public String toString() {
		AlgebraicNotation notation = new AlgebraicNotation();
		StringBuilder builder = new StringBuilder("+-+-+-+-+-+-+-+-+\n");
		for (int i = 7; i >= 0; i--) {
			builder.append("|");
			for (int j = 0; j < 8; j++) {
				try {
					builder.append(notation.fromPiece(board[i * 8 + j]));
					builder.append("|");
				} catch (NotationException e) {
					e.printStackTrace();
					return "ERROR: Invalid values";
				}
			}
			builder.append("\n+-+-+-+-+-+-+-+-+\n");
		}
		return builder.toString();
	}

	public void move(int move) {
		// TODO: Undo stack
		int fromSquare = MoveUtils.fromSquare(move);
		int toSquare = MoveUtils.toSquare(move);
		if (MoveUtils.isQueening(move)) {
			board[toSquare] = MoveUtils.promotedPiece(move);
		} else {
			board[toSquare] = board[fromSquare];
		}
		board[fromSquare] = Chess.Piece.EMPTY;
		if (MoveUtils.isEnPassentCapture(move)) {
			if (toSquare > fromSquare) {
				board[toSquare -  8] = Chess.Piece.EMPTY;
			} else {
				board[toSquare + 8] = Chess.Piece.EMPTY;
			}
		}
	}

}
