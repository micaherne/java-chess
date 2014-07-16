package uk.co.micaherne.javachess.notation;

import uk.co.micaherne.javachess.MoveUtils;
import uk.co.micaherne.javachess.NotationException;

public class LongAlgebraicNotation extends AlgebraicNotation {
	
	public int parseMove(String representation) throws NotationException {
		if (representation.length() < 4 || representation.length() > 5) {
			throw new NotationException("Move must be 4 or 5 characters");
		}
		int move;
		if (representation.length() == 5) {
			move = MoveUtils.create(toSquare(representation.substring(0, 1)), 
					toSquare(representation.substring(2,  3)), toPiece("" + representation.charAt(4)));
		} else {
			move = MoveUtils.create(toSquare(representation.substring(0, 1)), 
					toSquare(representation.substring(2,  3)));
		}
		return move;
	}

}
