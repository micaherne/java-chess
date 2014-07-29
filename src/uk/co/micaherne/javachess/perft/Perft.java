package uk.co.micaherne.javachess.perft;

import uk.co.micaherne.javachess.MoveGenerator;
import uk.co.micaherne.javachess.Position;

public class Perft {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static PerftResult perft(Position position, int depth) {
		MoveGenerator moveGenerator = new MoveGenerator(position);
		PerftResult result = new PerftResult();
		if (depth == 0) {
			result.moveCount++;
			return result;
		}
		int[] moves = moveGenerator.generateMoves();
		for (int i = 1; i <= moves[0]; i++) {
			position.move(moves[i]);
			PerftResult subPerft = perft(position, depth - 1);
			result.moveCount += subPerft.moveCount;
		}
		return result;
	}

}
