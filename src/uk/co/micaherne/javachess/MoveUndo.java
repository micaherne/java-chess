package uk.co.micaherne.javachess;

public class MoveUndo {
	
	public int move;
	public boolean isCapture = false;
	// Is castling affected for each side (some moves can affect both)
	public boolean[] affectsCastling = new boolean[] {false, false};
	public boolean isEnPassent = false;
	
	public int movedPiece = 0;
	public int capturedPiece = 0;
	
	public boolean[][] castling = new boolean[][]{{false, false}, {false, false}};
	public long epSquare = 0;
	
	public MoveUndo(int move) {
		this.move = move;
	}
}
