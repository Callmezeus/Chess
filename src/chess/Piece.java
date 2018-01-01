package chess;
/**
 *Piece class: the bread and butter of this entire project.
 *A piece is the general term for something like a pawn, knight ... etc
 *
 */
public class Piece {
	String color="";
	
	/**
	 *Piece constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece, i.e wp = white pawn
	 */
	public Piece(String color){
		this.color=color;
	}
	/**
	 *General valid move detection, if a piece is trying to move into an empty spot or not attacking its own team, its a valid move
	 *
	 * @param  p The piece in question
	 * @return boolean - whether or not the move was valid
	 */
	public boolean validMove(Piece p){
		//Checks to see if piece space is empty or occupied by the opposing team
		return p == null || !this.sameTeam(p);
	}
	/**
	 *
	 * @return Return the color of the team of the piece using this method [w,b]
	 */
	public char getTeam() {
		return this.color.charAt(0);
	}
	/**
	 *
	 * @return Return the piece type of the piece calling this method[p,r,b,n,q,k]
	 */
	public char getType() {
		return this.color.charAt(1);
	}
	/**
	 *
	 * @return Return the color of the opposite team of the piece using this method
	 */
	public char getOppositeTeam() {
		return this.getTeam() == 'w' ? 'b' : 'w';
	}
	/**
	 * @param p The piece being compared to the piece using this method
	 * @return true/false - whether or not the piece calling this method on another piece is of the opposite team
	 */
	public boolean isOppositeTeam(Piece p) {
		return p != null && this.getOppositeTeam() == p.getTeam();
	}
	/**
	 * @param p The piece being compared to the piece using this method
	 * @return true/false - whether or not the piece calling this method on another piece is of the same team
	 */
	public boolean sameTeam(Piece p) {
		return p != null && this.getTeam() == p.getTeam();
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return return 1 if valid, however, this should never be reached
	 */
	public int validMove(int row1, int column1, int row2, int column2, Piece[][] board, int turn) {
		// For backwards compatibility
		//System.out.println("Shouldn't be reaching here");
		return 1;
	}
	/**
	 * @return String - piece to string
	 */
	public String toString() {
		return this.color;
	}
	
	public int[] underAttackPos(int row, int column, char teamUnderAttack, Piece[][] Board, int turn) {
		int r[] = new int[2];
		r[0] = -1;
		r[1] = -1;
		
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]==null){
					continue;
				} else if(Board[i][j].getOppositeTeam() == teamUnderAttack){
					if(i == row && j == column) {
						continue;
					}
					if(Board[i][j].validMove(i, j, row, column, Board, turn) > 0) {
						r[0] = i;
						r[1] = j;
						return r;
					}
				}
			}
		}

		return r;
	}
	
	/**
	 * @param row the row of the piece that is to be checked
	 * @param column the column of the piece that is to be checked
	 * @param Board the current state of the game board
	 * @param turn the current turn
	 * @return used for testing / checkmate - will return pieces that are under threat of another piece
	 */
	public Piece underAttack(int row, int column, Piece[][] Board, int turn) {
		int[] r = this.underAttackPos(row, column, this.getTeam(), Board, turn);
		if(r[0] == -1) {
			return null;
		}
		return Board[r[0]][r[1]];
	}
	/**
	 * @param row the row of the piece that is to be checked
	 * @param column the column of the piece that is to be checked
	 * @param teamUnderAttack The char representing which team the piece is
	 * @param Board the current state of the game board
	 * @param turn the current turn
	 * @return used for testing / checkmate - will return pieces that are under threat of another piece
	 */
	public static Piece underAttack(int row, int column, char teamUnderAttack, Piece[][] Board, int turn){
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]==null){
					continue;
				} else if(Board[i][j].getOppositeTeam() == teamUnderAttack){
					if(Board[i][j].validMove(i, j, row, column, Board, turn) > 0) {
						return Board[i][j];
					}
				}
			}
		}
		return null;
	}
	//Returns null if nothing is blocking the path, a Piece object if something is
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param Board the current state of the game board
	 * @return Returns null if nothing is blocking the path, a Piece object if something is
	 */
	public Piece clearPath(int row1, int column1, int row2, int column2, Piece[][] Board) {
		// Moving left/right
		if(row1 == row2) {
			int colDiff = column1 - column2;
			int colChange = colDiff > 0 ? -1 : 1;
			for(int i = 1; i < Math.abs(colDiff); i++) {
				int col = column1 + (colChange * i);
				if(Board[row1][col] != null) {
					return Board[row1][col];
				}
			}
		}
		// Moving up/down
		else if(column1 == column2) {
			int rowDiff = row1 - row2;
			int rowChange = rowDiff > 0 ? -1 : 1;
			for(int i = 1; i < Math.abs(rowDiff); i++) {
				int row = row1 + (rowChange * i);
				if(Board[row][column1] != null) {
					return Board[row][column1];
				}
			}
		}
		// Moving diagonal
		else {
			int colDiff = column1 - column2;
			int colChange = colDiff > 0 ? -1 : 1;
			
			int rowDiff = row1 - row2;
			int rowChange = rowDiff > 0 ? -1 : 1;
			
			for(int i = 1; i < Math.abs(rowDiff); i++) {
				int row = row1 + (rowChange * i);
				int col = column1 + (colChange * i);
				if(Board[row][col] != null) {
					return Board[row][col];
				}
			}
		}
		return null;
	}
	//Variant of clearPath used to check safe Castle
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param Board the current state of the game board
	 * @param turn the current turn
	 * @return Returns null if nothing is blocking the path, a Piece object if something is - used for castling
	 */
	public Piece safePath(int row1, int column1, int row2, int column2, Piece[][] Board, int turn) {
		//Target path is clear
		Piece clear = this.clearPath(row1, column1, row2, column2, Board);
		if(clear != null) {
			return clear;
		}
		//Target location is clear
		if(Board[row2][column2] != null) {
			return Board[row2][column2];
		}
		// Moving left/right
		if(row1 == row2) {
			int colDiff = column1 - column2;
			int colChange = colDiff > 0 ? -1 : 1;
			for(int i = 1; i <= Math.abs(colDiff); i++) {
				int col = column1 + (colChange * i);
				Piece attacking = this.underAttack(row1, col, Board, -1);
				if(attacking != null) {
					return attacking;
				}
			}
		}
		// Moving up/down
		else if(column1 == column2) {
			int rowDiff = row1 - row2;
			int rowChange = rowDiff > 0 ? -1 : 1;
			for(int i = 1; i <= Math.abs(rowDiff); i++) {
				int row = row1 + (rowChange * i);
				Piece attacking = this.underAttack(row, column1, Board, -1);
				if(attacking != null) {
					return attacking;
				}
			}
		}
		// Moving diagonal
		else {
			int colDiff = column1 - column2;
			int colChange = colDiff > 0 ? -1 : 1;
			
			int rowDiff = row1 - row2;
			int rowChange = rowDiff > 0 ? -1 : 1;
			
			for(int i = 1; i <= Math.abs(rowDiff); i++) {
				int row = row1 + (rowChange * i);
				int col = column1 + (colChange * i);
				Piece attacking = this.underAttack(row, col, Board, -1);
				if(attacking != null) {
					return attacking;
				}
			}
		}
		return null;
	}
}
class Pawn extends Piece{
	int firstMove;
	/**
	 * Pawn constructor
	 * @param  color used in constructor to determine the entirety of piece, i.e wp = white pawn
	 */
	public Pawn(String color){
		super(color);
		this.firstMove = -1;
		//Pawn specials
		if(color.toLowerCase().equals("white")){
			this.color="wp";
		}else{
			this.color="bp";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move, 2 if the move was an enpassant, 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		if(column1 == column2) {
			// First move bonus
			if((row1 + 2 == row2 && this.getTeam() == 'b' || row1 - 2 == row2 && this.getTeam() == 'w') &&
					this.firstMove == -1 &&
					((Board[row1 + 1][column1] == null && this.getTeam() == 'b') ||
							(Board[row1 - 1][column1] == null && this.getTeam() == 'w')) &&
					Board[row2][column2] == null) {
				return 2;
			}
			// Moving forward one
			else if((row1 + 1 == row2 && this.getTeam() == 'b' || row1 - 1 == row2 && this.getTeam() == 'w')
					&& Board[row2][column2] == null) {
				if(this.firstMove == -1) {
					return 2;
				}
				return 1;
			}
		}
		// Attacking
		else if((column1 + 1 == column2 || column1 - 1 == column2) &&
				(row1 + 1 == row2 && this.getTeam() == 'b' || row1 - 1 == row2 && this.getTeam() == 'w')) {
			// Normal Attack
			if(Board[row2][column2] != null && !this.sameTeam(Board[row2][column2])) {
				if(this.firstMove == -1) {
					return 2;
				}
				return 1;
			}
			//Enpassant
			else if(this.isOppositeTeam(Board[row1][column2]) &&
					Board[row1][column2].getType() == 'p' &&
					((Pawn)Board[row1][column2]).firstMove + 1 == turn) {
				return 3;
			}
		}
		
		return 0;
	}
}
/**
 * Rook class, contains all the special characteristics of Rook
 */
class Rook extends Piece{
	int firstMove;
	/**
	 *Rook constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece -wR/bR
	 */
	public Rook(String color){
		super(color);
		//Rook specials
		
		this.firstMove = -1;
		if(color.toLowerCase().equals("white")){
			this.color="wR";
		}else{
			this.color="bR";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move, 2 if the move was an enpassant, 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		if(this.color.toLowerCase().equals("wr")){
			//System.out.println("I am a White Rook");
			if(whiteIllegal(row1,column1,row2,column2,Board)){
				return 0;
			}
		}else{
			//System.out.println("I am a Black Rook");
			if(blackIllegal(row1,column1,row2,column2,Board)){
				return 0;
			}
		}
		if(this.firstMove == -1) {
			return 2;
		}
		return 1;
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns true if the piece is attempting an illegal move, false if it is not
	 */
	public boolean whiteIllegal(int row1, int column1, int row2, int column2, Piece[][] Board){
		if(row1!=row2 && column1!=column2){
			//No diagonals allowed
			return true;
		}
		int i;
		if(row1!=row2){
			//White piece going up/down
			if(row1>row2){
				//White piece going up
				for(i=row1-1;i>row2;i--){
					if(Board[i][column1]!=null){
						return true;
					}
				}
			}else{
				for(i=row1+1;i<row2;i++){
					if(Board[i][column1]!=null){
						return true;
					}
				}
			}
		}
		if(column1!=column2){
			if(column1>column2){
				//White rook moving left
				for(i=column1-1;i>column2;i--){
					if(Board[row1][i]!=null){
						return true;
					}
				}
			}else{
				//White rook moving right
				for(i=column1+1;i<column2;i++){
					if(Board[row1][i]!=null){
						return true;
					}
				}
			}
		}
		if(Board[row2][column2]==null){
			return false;
		}
		if(Board[row2][column2].color.toLowerCase().equals("wp") || Board[row2][column2].color.toLowerCase().equals("wr") || Board[row2][column2].color.toLowerCase().equals("wn") || Board[row2][column2].color.toLowerCase().equals("wb") || Board[row2][column2].color.toLowerCase().equals("wq") ||Board[row2][column2].color.toLowerCase().equals("wk")){
			//attack same team
			return true;
		}
		return false;
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns true if the piece is attempting an illegal move, false if it is not
	 */
	public boolean blackIllegal(int row1, int column1, int row2, int column2, Piece[][] Board){
		if(row1!=row2 && column1!=column2){
			//No diagonals allowed
			return true;
		}
		int i;
		if(row1!=row2){
			//White piece going up/down
			if(row1>row2){
				//White piece going up
				for(i=row1-1;i>row2;i--){
					if(Board[i][column1]!=null){
						return true;
					}
				}
			}else{
				for(i=row1+1;i<row2;i++){
					if(Board[i][column1]!=null){
						return true;
					}
				}
			}
		}
		if(column1!=column2){
			if(column1>column2){
				//White rook moving left
				for(i=column1-1;i>column2;i--){
					if(Board[row1][i]!=null){
						return true;
					}
				}
			}else{
				//White rook moving right
				for(i=column1+1;i<column2;i++){
					if(Board[row1][i]!=null){
						return true;
					}
				}
			}
		}
		if(Board[row2][column2]==null){
			return false;
		}
		if(Board[row2][column2].color.toLowerCase().equals("bp") || Board[row2][column2].color.toLowerCase().equals("br") ||Board[row2][column2].color.toLowerCase().equals("bn") || Board[row2][column2].color.toLowerCase().equals("bb") || Board[row2][column2].color.toLowerCase().equals("bq") ||Board[row2][column2].color.toLowerCase().equals("bk")){
			//attack same team
			return true;
		}
		return false;
	}
}
/**
 * Knight class, contains all the special characteristics of Knight
 */
class Knight extends Piece{
	/**
	 *Knight constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece -wN/bN
	 */
	public Knight(String color){
		super(color);
		//Knight specials
		if(color.toLowerCase().equals("white")){
			
			this.color="wN";
		}else{
			this.color="bN";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move; 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		if(row1 + 2 == row2 || row1 - 2 == row2) {
			if(column1 + 1 == column2 || column1 - 1 == column2) {
				if(super.validMove(Board[row2][column2])) {
					return 1;
				}
			}
		}
		else if(column1 + 2 == column2 || column1 - 2 == column2) {
			if(row1 + 1 == row2 || row1 - 1 == row2) {
				if(super.validMove(Board[row2][column2])) {
					return 1;
				}
			}
		}
		return 0;
	}
}
/**
 * Bishop class, contains all the special characteristics of Bishop
 */
class Bishop extends Piece{
	/**
	 *Bishop constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece -wB/bB
	 */
	public Bishop(String color){
		super(color);
		//Bishop specials
		if(color.toLowerCase().equals("white")){
			this.color="wB";
		}else{
			this.color="bB";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move; 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		if(this.color.toLowerCase().equals("wb")){
			//System.out.println("I am a White Bishop");
			if(whiteIllegal(row1,column1,row2,column2,Board)){
				return 0;
			}
		}else{
			//System.out.println("I am a Black Bishop");
			if(blackIllegal(row1,column1,row2,column2,Board)){
				return 0;
			}
		}
		return 1;
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns true if the piece is attempting an illegal move, false if it is not
	 */
	public boolean whiteIllegal(int row1, int column1, int row2, int column2,Piece[][] Board){
		if(row1!=row2 && column1==column2){
			//must be diagonal
			return true;
		}
		if(row1==row2 && column1!=column2){
			//must be diagonal
			return true;
		}
		int testDiag1 = Math.max(row1,row2);
		int testDiag2 = Math.min(row1, row2);
		int testDiag3 = Math.max(column1, column2);
		int testDiag4 = Math.min(column1, column2);
		if((testDiag1 - testDiag3) != (testDiag2 - testDiag4)){
			//System.out.println("Not a perfect diagonal");
			return true;
		}
		//four possible directions
		if(row2<row1 && column2>column1){
			// up and right
			int horizontal;
			int vertical=column1+1;
			for(horizontal=row1-1;horizontal>row2;horizontal--){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical++;
			}
		}else if(row2<row1 && column2<column1){
			// up and left
			int horizontal;
			int vertical=column1-1;
			for(horizontal=row1-1;horizontal>row2;horizontal--){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical--;
			}
		}else if(row2>row1 && column2>column1){
			// down and right
			int horizontal;
			int vertical=column1+1;
			for(horizontal=row1+1;horizontal<row2;horizontal++){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical++;
			}
		}else if(row2>row1 && column2<column1){
			// down and left
			int horizontal;
			int vertical=column1-1;
			for(horizontal=row1+1;horizontal<row2;horizontal++){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical--;
			}
		}
		if(Board[row2][column2]==null){
			//Going to empty spot
			return false;
		}
		if(Board[row2][column2].color.toLowerCase().equals("wp") || Board[row2][column2].color.toLowerCase().equals("wr") || Board[row2][column2].color.toLowerCase().equals("wn") || Board[row2][column2].color.toLowerCase().equals("wb") || Board[row2][column2].color.toLowerCase().equals("wq") || Board[row2][column2].color.toLowerCase().equals("wk")){
			//attacking same team
			return true;
		}
		
		
		return false;
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns true if the piece is attempting an illegal move, false if it is not
	 */
	public boolean blackIllegal(int row1, int column1, int row2, int column2,Piece[][] Board){
		if(row1!=row2 && column1==column2){
			//must be diagonal
			return true;
		}
		if(row1==row2 && column1!=column2){
			//must be diagonal
			return true;
		}

		int testDiag1 = Math.max(row1,row2);
		int testDiag2 = Math.min(row1, row2);
		int testDiag3 = Math.max(column1, column2);
		int testDiag4 = Math.min(column1, column2);
		if((testDiag1 - testDiag3) != (testDiag2 - testDiag4)){
			//System.out.println("Not a perfect diagonal");
			return true;
		}
		//four possible directions
		if(row2<row1 && column2>column1){
			// up and right
			int horizontal;
			int vertical=column1+1;
			for(horizontal=row1-1;horizontal>row2;horizontal--){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical++;
			}
		}else if(row2<row1 && column2<column1){
			// up and left
			int horizontal;
			int vertical=column1-1;
			for(horizontal=row1-1;horizontal>row2;horizontal--){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical--;
			}
		}else if(row2>row1 && column2>column1){
			// down and right
			int horizontal;
			int vertical=column1+1;
			for(horizontal=row1+1;horizontal<row2;horizontal++){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical++;
			}
		}else if(row2>row1 && column2<column1){
			// down and left
			int horizontal;
			int vertical=column1-1;
			for(horizontal=row1+1;horizontal<row2;horizontal++){
				if(Board[horizontal][vertical]!=null){
					return true;
				}
				vertical--;
			}
		}
		if(Board[row2][column2]==null){
			//Going to empty spot
			return false;
		}
		if(Board[row2][column2].color.toLowerCase().equals("bp") || Board[row2][column2].color.toLowerCase().equals("br") || Board[row2][column2].color.toLowerCase().equals("bn") || Board[row2][column2].color.toLowerCase().equals("bb") || Board[row2][column2].color.toLowerCase().equals("bq") || Board[row2][column2].color.toLowerCase().equals("bk")){
			//attacking same team
			return true;
		}
		
		
		return false;
	}
}
/**
 * Queen class, contains all the special characteristics of Queen
 */
class Queen extends Piece{
	/**
	 *Queen constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece -wQ/bQ
	 */
	public Queen(String color){
		super(color);
		//Queen specials
		if(color.toLowerCase().equals("white")){
			this.color="wQ";
		}else{
			this.color="bQ";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move; 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		//Rook implementation
		if(row1!=row2 && column1==column2 || row1==row2 && column1!=column2){
			int i;
			if(row1!=row2){
				if(row1>row2){
					for(i=row1-1;i>row2;i--){
						if(Board[i][column1]!=null){
							return 0;
						}
					}
				}else{
					for(i=row1+1;i<row2;i++){
						if(Board[i][column1]!=null){
							return 0;
						}
					}
				}
			}
			if(column1!=column2){
				if(column1>column2){
					//White rook moving left
					for(i=column1-1;i>column2;i--){
						if(Board[row1][i]!=null){
							return 0;
						}
					}
				}else{
					//White rook moving right
					for(i=column1+1;i<column2;i++){
						if(Board[row1][i]!=null){
							return 0;
						}
					}
				}
			}
			if(super.validMove(Board[row2][column2])) {
				return 1;
			}
		}
		//Bishop implementation
		if(row1!=row2 && column1!=column2){
			int testDiag1 = Math.max(row1,row2);
			int testDiag2 = Math.min(row1, row2);
			int testDiag3 = Math.max(column1, column2);
			int testDiag4 = Math.min(column1, column2);
			if((testDiag1 - testDiag3) != (testDiag2 - testDiag4)){
				//System.out.println("Not a perfect diagonal");
				return 0;
			}
			//four possible directions
			if(row2<row1 && column2>column1){
				// up and right
				int horizontal;
				int vertical=column1+1;
				for(horizontal=row1-1;horizontal>row2;horizontal--){
					if(Board[horizontal][vertical]!=null){
						return 1;
					}
					vertical++;
				}
			}else if(row2<row1 && column2<column1){
				// up and left
				int horizontal;
				int vertical=column1-1;
				for(horizontal=row1-1;horizontal>row2;horizontal--){
					if(Board[horizontal][vertical]!=null){
						return 1;
					}
					vertical--;
				}
			}else if(row2>row1 && column2>column1){
				// down and right
				int horizontal;
				int vertical=column1+1;
				for(horizontal=row1+1;horizontal<row2;horizontal++){
					if(Board[horizontal][vertical]!=null){
						return 1;
					}
					vertical++;
				}
			}else if(row2>row1 && column2<column1){
				// down and left
				int horizontal;
				int vertical=column1-1;
				for(horizontal=row1+1;horizontal<row2;horizontal++){
					if(Board[horizontal][vertical]!=null){
						return 1;
					}
					vertical--;
				}
			}
			if(super.validMove(Board[row2][column2])) {
				return 1;
			}
		}
		return 0;
	}
}
/**
 * King class, contains all the special characteristics of King
 */
class King extends Piece{
	int firstMove;
	/**
	 *King constructor
	 *
	 * @param  color used in constructor to determine the entirety of piece -wK/bK
	 */
	public King(String color){
		super(color);
		//King specials
		this.firstMove = -1;
		if(color.toLowerCase().equals("white")){
			
			this.color="wK";
		}else{
			this.color="bK";
		}
	}
	/**
	 * @param row1 the row of the piece that is to be moved
	 * @param column1 the column of the piece that is to be moved
	 * @param row2 the row of which the piece moving wishes to move to
	 * @param column2 the column of which the piece moving wishes to move to
	 * @param board the current state of the game board
	 * @param turn the current turn
	 * @return Returns 1 if it is a valid move, 2 if the move was a castling move, 0 otherwise
	 */
	public int validMove(int row1, int column1, int row2, int column2,Piece[][] Board, int turn){
		
		//Taking 1 step, any direction
		if((Math.abs(row1 - row2) <= 1 && Math.abs(column1 - column2) <= 1) &&
				this.underAttack(row2, column2, Board, turn) == null &&
				super.validMove(Board[row2][column2])) {
			if(this.firstMove == -1) {
				return 2;
			}
			return 1;
		}
		// Castle
		else if(row1 == row2 && Math.abs(column1 - column2) == 2 &&
				this.firstMove == -1 &&
				this.safePath(row1, column1, row2, column2, Board, turn) == null) {
			
			int col = column1 - column2 > 0 ? 0 : 7;
			
			if(Board[row1][col] != null &&
					Board[row1][col].color.equals("" + this.getTeam() + "R") &&
					((Rook)Board[row1][col]).firstMove == -1 &&
					Board[row1][col].clearPath(row1, col, row1, col == 0 ? 3 : 5, Board) == null) {
				return 3;
			}
		}
		return 0;
	}
	
}


