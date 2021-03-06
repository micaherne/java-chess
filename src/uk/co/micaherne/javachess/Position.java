package uk.co.micaherne.javachess;

import java.util.Stack;

import uk.co.micaherne.javachess.notation.AlgebraicNotation;
import uk.co.micaherne.javachess.notation.LongAlgebraicNotation;

public class Position {
	
	// TODO: This is bad - need to refactor to remove inCheck() dependency on
	// MoveGenerator for attacks()
	public MoveGenerator moveGenerator;
	
	public long[] pieceBitboards;
	public long[] colourBitboards;
	
	public int[] board;
	
	// colour, (q, k)
	public boolean castling[][];
	
	public int moves = 0;
	public int halfMoves = 0;
	
	public long epSquare = 0; // en passent square as a bitboard
	
	public boolean whiteToMove = false;
	
	/**
	 * The stack of undo data for the moves that have been made so far.
	 * 
	 * This would use less memory if we were to replace it with a set list of 
	 * MoveUndo objects and a currentMove pointer. When making a move we would 
	 * get the object for the currentMove+1 position, reset and re-use it.
	 */
	public Stack<MoveUndo> undoData = new Stack<MoveUndo>();
	
	public Position() {
		board = new int[64];
		pieceBitboards = new long[7];
		colourBitboards = new long[2];
		castling = new boolean[][]{{false, false}, {false, false}};
		moveGenerator = new MoveGenerator(this);
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
		AlgebraicNotation notation = new LongAlgebraicNotation();
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
			result.epSquare = 1L << notation.toSquare(fenParts[3]);
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
		LongAlgebraicNotation notation = new LongAlgebraicNotation();
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

	/**
	 * Make the given pseudolegal move.
	 * 
	 * @param move
	 * @return true if move legal, false if not
	 */
	public boolean move(int move) {
		MoveUndo undo = new MoveUndo(move);
		int fromSquare = MoveUtils.fromSquare(move);
		int toSquare = MoveUtils.toSquare(move);

		int sideMoving = whiteToMove ? Chess.Colour.WHITE : Chess.Colour.BLACK;
		int opposingSide = whiteToMove ? Chess.Colour.BLACK : Chess.Colour.WHITE;

		undo.movedPiece = board[fromSquare];
		undo.capturedPiece = board[toSquare]; // Always want this even if empty
		if (board[toSquare] != Chess.Piece.EMPTY) {
			undo.isCapture = true;
		}
		
		// Castling
		if ((castling[sideMoving][0] || castling[sideMoving][1]) && ((board[fromSquare] & 7) == Chess.Piece.KING)) {
			undo.affectsCastling[sideMoving] = true;
			undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
			castling[sideMoving][0] = false;
			castling[sideMoving][1] = false;
			
			// Move rook
			if (Math.abs(toSquare - fromSquare) == 2) {
				if (toSquare == MoveGenerator.oooTo[sideMoving]) {
					board[toSquare + 1] = board[MoveGenerator.oooRook[sideMoving]];
					board[MoveGenerator.oooRook[sideMoving]] = Chess.Piece.EMPTY;
				} else if (toSquare == MoveGenerator.ooTo[sideMoving]) {
					board[toSquare - 1] = board[MoveGenerator.ooRook[sideMoving]];
					board[MoveGenerator.ooRook[sideMoving]] = Chess.Piece.EMPTY;
				}
			}
		}
		if (castling[sideMoving][0] && ((board[fromSquare] & 7) == Chess.Piece.ROOK) && (fromSquare == MoveGenerator.oooRook[sideMoving])) {
			undo.affectsCastling[sideMoving] = true;
			undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
			castling[sideMoving][0] = false;
		}
		if (castling[sideMoving][1] && ((board[fromSquare] & 7) == Chess.Piece.ROOK) && (fromSquare == MoveGenerator.ooRook[sideMoving])) {
			undo.affectsCastling[sideMoving] = true;
			undo.castling[sideMoving] = new boolean[] {castling[sideMoving][0], castling[sideMoving][1]};
			castling[sideMoving][1] = false;
		}
		
		// Unset castling rights for rook captures
		if (castling[opposingSide][0] && ((board[toSquare] & 7) == Chess.Piece.ROOK) && (toSquare == MoveGenerator.oooRook[opposingSide])) {
			undo.affectsCastling[opposingSide] = true;
			undo.castling[opposingSide] = new boolean[] {castling[opposingSide][0], castling[opposingSide][1]};
			castling[opposingSide][0] = false;
		}
		if (castling[opposingSide][1] && ((board[toSquare] & 7) == Chess.Piece.ROOK) && (toSquare == MoveGenerator.ooRook[opposingSide])) {
			undo.affectsCastling[opposingSide] = true;
			undo.castling[opposingSide] = new boolean[] {castling[opposingSide][0], castling[opposingSide][1]};
			castling[opposingSide][1] = false;
		}
		
		if (MoveUtils.isQueening(move)) {
			board[toSquare] = MoveUtils.promotedPiece(move);
		} else {
			board[toSquare] = board[fromSquare];
		}
		
		// Do en passent captures
		if (MoveUtils.isEnPassentCapture(move)) {
			undo.isEnPassent = true;
			// undo.epSquare = epSquare;
			if (toSquare > fromSquare) {
				board[toSquare -  8] = Chess.Piece.EMPTY;
				undo.capturedPiece = Chess.Piece.Black.PAWN;
			} else {
				board[toSquare + 8] = Chess.Piece.EMPTY;
				undo.capturedPiece = Chess.Piece.White.PAWN;
			}
		}
		
		// Save the en passent square if necessary
		if (epSquare != 0) {
			undo.epSquare = epSquare;
		}

		if ((board[fromSquare] & 7) == Chess.Piece.PAWN && (Math.abs(toSquare - fromSquare) == 16)) {
			epSquare = 1L << (fromSquare + ((toSquare - fromSquare) / 2));
		} else {
			epSquare = 0L;
		}
		
		board[fromSquare] = Chess.Piece.EMPTY;
		

		undoData.push(undo);
		whiteToMove = !whiteToMove;
		
		initialisePieceBitboards();
		
		if (inCheck(sideMoving)) {
			unmakeMove();
			return false;
		}
				
		// TODO: Half-moves and moves
		
		return true;
	}
	
	// TODO: Fails if king not found (index out of bounds 64)
	public boolean inCheck(int side) {
		int kingPosition = Long.numberOfTrailingZeros(pieceBitboards[Chess.Piece.KING] & colourBitboards[side]);
		return moveGenerator.attacks(kingPosition, MoveGenerator.oppositeColour(side));
	}

	public void unmakeMove() {
		MoveUndo undo = undoData.pop();
		int fromSquare = MoveUtils.fromSquare(undo.move);
		int toSquare = MoveUtils.toSquare(undo.move);
		
		int sideThatMoved = whiteToMove ? Chess.Colour.BLACK : Chess.Colour.WHITE;
		int opposingSide = whiteToMove ? Chess.Colour.WHITE : Chess.Colour.WHITE;
		
		board[fromSquare] = undo.movedPiece;
		board[toSquare] = undo.capturedPiece;
		epSquare = undo.epSquare;
		
		// Undo castling permissions. TODO: Could be for loop but is it worth it?
		if (undo.affectsCastling[0]) {
			castling[0] = undo.castling[0];
		}
		if (undo.affectsCastling[1]) {
			castling[1] = undo.castling[1];
		}
		
		// Reset castled rook
		if (((undo.movedPiece & 7) == Chess.Piece.KING) && (Math.abs(fromSquare - toSquare) == 2)) {
			int rookSquare = fromSquare + ((toSquare - fromSquare) / 2);
			int rook = board[rookSquare];
			if (toSquare > fromSquare) {
				board[rookSquare + 2] = rook;
			} else {
				board[rookSquare - 3] = rook;
			}
			board[rookSquare] = Chess.Piece.EMPTY;
		}
		
		if (undo.isEnPassent) {
			board[toSquare] = Chess.Piece.EMPTY;
			if (toSquare > fromSquare) {
				board[toSquare -  8] = undo.capturedPiece;
			} else {
				board[toSquare + 8] = undo.capturedPiece;
			}
		}
		
		whiteToMove = !whiteToMove;
		
		initialisePieceBitboards();
		
		// TODO: Half-moves and moves
	}

}
