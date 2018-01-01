package chess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class Chess {
	
	public static void main(String[] args) throws IOException{
		Chess chessGame = new Chess();
		
		Piece[][] Board = new Piece[8][8];
		chessGame.initializeBoard(Board);
		chessGame.printCurrentBoard(Board);
		int turn = 0;
		while(true){
			//Infinite loop just for testing, need to implement breaks when game is over
			int check = turn;
			turn = chessGame.readNextMove(Board,turn);
			while(check == turn){
				turn = chessGame.readNextMove(Board,turn);
			}
			//readNextMove returning 2 indicates end of game
			if(turn == -1){
				break;
			}
			//System.out.println("Turn "+ turn);
			chessGame.printCurrentBoard(Board);
			
			String wkCoord = chessGame.findWhiteKing(Board);
			String bkCoord = chessGame.findBlackKing(Board);
			if(chessGame.checkCheck(wkCoord,Board, turn) == 2) {
				System.out.println("Black wins");
				break;
			}
			if(chessGame.checkCheck(bkCoord,Board, turn) == 2) {
				System.out.println("White wins");
				break;
			}
		}
		
	}
	private boolean drawOffer = false;
	/**
	 *readNextMove is our method of asking the user for input. It also implements a turn system as you can see by the variable turn
	 *if turn % 2 == 0 then its whites move, otherwise it is blacks. If turn == -1, it signifies the game being over
	 *Further, this is where all the checking for input goes, whether the user is making a normal move, draw, or promotion entry
	 *
	 * @param  Board  The current game state of the board given by the Piece [][]
	 * @param  turn current value of the turn variable, used for denoting whose turn it is
	 * @return int - Returns an int corresponding to the next turn, to be used by the method again to determine who's turn it is.
	 * @throws IOException - necessary for buffered reader
	 */
	public int readNextMove(Piece[][] Board, int turn)throws IOException{
		if(turn % 2 == 0){
			System.out.println("");
			System.out.print("White's move: ");
		}else{
			System.out.println("");
			System.out.print("Black's move: ");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		s = s.trim().replaceAll(" +", " ");
		
		if(drawOffer == true && s.toLowerCase().equals("draw")) {
			//System.out.println("Draw accepted!");
			return -1;
		}
		else {
			drawOffer = false;
		}

		if(s.toLowerCase().equals("resign")){
			if(turn % 2 == 0){
				System.out.println("Black wins");
			}else{
				System.out.println("White wins");
			}
			return -1;
		}
		
		else if(s.length() == 5 || s.length() == 7 || (s.length() == 11 && s.substring(6).equals("draw?"))){
			String coord= convertCoords(s);
			if(coord.length()!=4){
				System.out.println("Illegal move, try again");
				return turn;
			}
			int row1 = Integer.parseInt(coord.substring(1,2));
			int column1 = Integer.parseInt(coord.substring(0,1));
			int row2 = Integer.parseInt(coord.substring(3,4));
			int column2 = Integer.parseInt(coord.substring(2,3));
			
			//Bug where if you put something like ae bg, it will work, but that input isn't valid
			if(Board[row1][column1]==null){
				//if there is no piece on the spot the user put as first input
				System.out.println("Illegal move, try again");
				return turn;
			}
			if(row1==row2&&column1==column2){
				//Piece must move to somewhere
				System.out.println("Illegal move, try again");
				return turn;
			}
			if(turn % 2 == 0){
				if(Board[row1][column1].color.toLowerCase().equals("bp") || Board[row1][column1].color.toLowerCase().equals("br") || Board[row1][column1].color.toLowerCase().equals("bb") || Board[row1][column1].color.toLowerCase().equals("bq") || Board[row1][column1].color.toLowerCase().equals("bk") || Board[row1][column1].color.toLowerCase().equals("bn")){
					System.out.println("Illegal move, try again");
					return turn;
				}
			}
			if(turn % 2 != 0){
				if(Board[row1][column1].color.toLowerCase().equals("wp") || Board[row1][column1].color.toLowerCase().equals("wr") || Board[row1][column1].color.toLowerCase().equals("wb") || Board[row1][column1].color.toLowerCase().equals("wq") || Board[row1][column1].color.toLowerCase().equals("wk") || Board[row1][column1].color.toLowerCase().equals("wn")){
					System.out.println("Illegal move, try again");
					return turn;
				}
			}
			int valid = Board[row1][column1].validMove(row1,column1,row2,column2,Board, turn);
			if(valid > 0){
				//Not a special case just a regular move
				Board[row2][column2] = Board[row1][column1];
				Board[row1][column1] = null;
				
				//Checks for special case
				if(valid == 2) {
					Piece p = Board[row2][column2];
					// First Move
					if('p' == p.getType()) {
						((Pawn)Board[row2][column2]).firstMove = turn;
					}
					else if('R' == p.getType()) {
						((Rook)Board[row2][column2]).firstMove = turn;
					}
					else if('K' == p.getType()) {
						((King)Board[row2][column2]).firstMove = turn;
					}
				}
				else if(valid == 3) {
					Piece p = Board[row2][column2];
					// Enpassant
					if(p.getType() == 'p') {
						Board[row1][column2] = null;
					}
					// Castle
					if(p.getType() == 'K') {
						int col = column1 - column2 > 0 ? 0 : 7;
						Board[row1][col == 0 ? 3 : 5] = Board[row1][col];
						Board[row1][col] = null;
						
						((Rook)Board[row1][col == 0 ? 3 : 5]).firstMove = turn;
						((King)Board[row2][column2]).firstMove = turn;
					}
				}
				
				turn += 1;
				Piece attacking = Board[row2][column2].underAttack(row2, column2, Board, turn);
				if(attacking != null) {
					//System.out.println("Under threat of attack by " + attacking);
				}
				
				
				// Promotion
				if(row2 == 0 && Board[row2][column2].color.equals("wp")|| row2 == 7 && Board[row2][column2].color.equals("bp")) {
					// If no valid promotion argument given, default
					if(s.length() == 5 || "RNBQ".indexOf(s.charAt(6)) < 0) {
						Board[row2][column2].color = "" + Board[row2][column2].color.charAt(0) + 'Q';
					}
					else {
						Board[row2][column2].color = "" + Board[row2][column2].color.charAt(0) + s.charAt(6);
					}
				}
				if(s.length()==11) {
					drawOffer = true;
					//System.out.println("Offer to draw extended");
				}
				//Check for stalemate
			}else{
				System.out.println("Illegal move, try again");
				return turn;
			}
			
		}
		else if(s.length() > 5 && s.substring(0, 5).equals("cheat")) {
			String coord= convertCoords(s.substring(6));
			int row1 = Integer.parseInt(coord.substring(1,2));
			int column1 = Integer.parseInt(coord.substring(0,1));
			int row2 = Integer.parseInt(coord.substring(3,4));
			int column2 = Integer.parseInt(coord.substring(2,3));
			Board[row2][column2] = Board[row1][column1];
			Board[row1][column1] = null;
			//System.out.println("Cheated");
			turn += 1;
		}
		else{
			
			//Temporary placement for testing - will be removed once else statement above is implemented
			System.out.println("Illegal move, try again");
		}
		return turn;
		
		
	}
	/**
	 *printCurrentBoard does exactly what its called, will print the current state of the board to the console.
	 *
	 * @param  Board The current game state of the board given by the Piece [][]
	 */
	public void printCurrentBoard(Piece[][] Board){
		System.out.println("");
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]!=null){
					System.out.print(Board[i][j].color+" ");
				}else{
					if((i+j)%2!=0){
						System.out.print("## ");
					}else{
						System.out.print("   ");
					}
				}
			}
			System.out.print(8-i);
			System.out.println();
		}
		System.out.println(" a  b  c  d  e  f  g  h");
	}
	/**
	 *initializeBoard does exactly what its called, at the beginning of the program, will initialize the board.
	 *This means populating a Piece[][] with the proper starting positions of every piece in a game of chess
	 *
	 * @param  Board an empty Piece [][]
	 */
	public void initializeBoard(Piece[][] Board){
		//Make all the Black Pieces
		Rook rook1b = new Rook("Black");
		Rook rook2b = new Rook("Black");
		Knight knight1b = new Knight("Black");
		Knight knight2b = new Knight("Black");
		Bishop bishop1b = new Bishop("Black");
		Bishop bishop2b = new Bishop("Black");
		Queen queenb = new Queen("Black");
		King kingb = new King("Black");
		Pawn pawn1b = new Pawn("Black");
		Pawn pawn2b = new Pawn("Black");
		Pawn pawn3b = new Pawn("Black");
		Pawn pawn4b = new Pawn("Black");
		Pawn pawn5b = new Pawn("Black");
		Pawn pawn6b = new Pawn("Black");
		Pawn pawn7b = new Pawn("Black");
		Pawn pawn8b = new Pawn("Black");
		//Make all the White Pieces
		Rook rook1w = new Rook("White");
		Rook rook2w = new Rook("White");
		Knight knight1w = new Knight("White");
		Knight knight2w = new Knight("White");
		Bishop bishop1w = new Bishop("White");
		Bishop bishop2w = new Bishop("White");
		Queen queenw = new Queen("White");
		King kingw = new King("White");
		Pawn pawn1w = new Pawn("White");
		Pawn pawn2w = new Pawn("White");
		Pawn pawn3w = new Pawn("White");
		Pawn pawn4w = new Pawn("White");
		Pawn pawn5w = new Pawn("White");
		Pawn pawn6w = new Pawn("White");
		Pawn pawn7w = new Pawn("White");
		Pawn pawn8w = new Pawn("White");
		//Populate the Board
		//Black Side
		Board[0][0] = rook1b;
		Board[0][1] = knight1b;
		Board[0][2] = bishop1b;
		Board[0][3] = queenb;
		Board[0][4] = kingb;
		Board[0][5] = bishop2b;
		Board[0][6] = knight2b;
		Board[0][7] = rook2b;
		Board[1][0] = pawn1b;
		Board[1][1] = pawn2b;
		Board[1][2] = pawn3b;
		Board[1][3] = pawn4b;
		Board[1][4] = pawn5b;
		Board[1][5] = pawn6b;
		Board[1][6] = pawn7b;
		Board[1][7] = pawn8b;
		//White Side
		Board[7][0] = rook1w;
		Board[7][1] = knight1w;
		Board[7][2] = bishop1w;
		Board[7][3] = queenw;
		Board[7][4] = kingw;
		Board[7][5] = bishop2w;
		Board[7][6] = knight2w;
		Board[7][7] = rook2w;
		Board[6][0] = pawn1w;
		Board[6][1] = pawn2w;
		Board[6][2] = pawn3w;
		Board[6][3] = pawn4w;
		Board[6][4] = pawn5w;
		Board[6][5] = pawn6w;
		Board[6][6] = pawn7w;
		Board[6][7] = pawn8w;
		
		
		
	}
	/**
	 *convertCoords will take in the user input, if it is of size 5 and will convert something like a1 e2 into its proper coordinates relating to the board array
	 *
	 * @param  s the string given to the program by the user, should be of size 5 and look like: a1 e1
	 * @return    String returns a string containing 4 integers, to be used later to translate into array inputs.
	 */
	public String convertCoords(String s){
		String firstLetter=s.substring(0,1);
		String firstInt=s.substring(1,2);
		String secondLetter=s.substring(3,4);
		String secondInt=s.substring(4,5);		
		String fullCoords = switchMethod(firstLetter) +  switchMethod(firstInt) + switchMethod(secondLetter) + switchMethod(secondInt); 
		return  fullCoords;
	}
	/**
	 *switchMethod is a tool to be used by convertCoords which translates what the user inputs and returns its appropriate value
	 *
	 * @param  s  a piece of the full coordinate string to be translated
	 * @return String a single integer in the form of a string "easier to be interpreted"
	 */
	public String switchMethod(String s){
		switch(s){
		case "a":
			return "0";
		case "b":
			return "1";
		case "c":
			return "2";
		case "d":
			return "3";
		case "e":
			return "4";
		case "f":
			return "5";
		case "g":
			return "6";
		case "h":
			return "7";
		case "1":
			return "7";
		case "2":
			return "6";
		case "3":
			return "5";
		case "4":
			return "4";
		case "5":
			return "3";
		case "6":
			return "2";
		case "7":
			return "1";
		case "8":
			return "0";
		default:
			//INVALID INPUT ERROR HERE
			return "ERROR ERROR ERROR";
		}
	}
	/**
	 *findWhiteKing finds the position of the white king, to be used with check and checkmate.
	 *
	 * @param  Board The current game state of the board given by the Piece [][]
	 * @return String The location of the White King
	 */
	public String findWhiteKing(Piece[][] Board){
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]==null){
					continue;
				}else{
					if(Board[i][j].color.toLowerCase().equals("wk")){
						return i+""+j;
					}
				}
			}
		}
		return "I'll never reach here";
	}
	/**
	 *findBlackKing finds the position of the black king, to be used with check and checkmate.
	 *
	 * @param  Board The current game state of the board given by the Piece [][]
	 * @return String The location of the Black King
	 */
	public String findBlackKing(Piece[][] Board){
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]==null){
					continue;
				}else{
					if(Board[i][j].color.toLowerCase().equals("bk")){
						return i+""+j;
					}
				}
			}
		}
		return "I'll never reach here";
	}
	/**
	 *checkCheck is to be called after every turn, this uses the findXKing methods and checks if any piece can attack the appropriate king
	 *in chess, if this is possible, it is called a check; and if it is possible, will print which king is in check.
	 *
	 * @param coords the location of the king being checked
	 * @param  Board The current game state of the board given by the Piece [][]
	 * @param turn the current turn
	 */
	public int checkCheck(String coords, Piece[][] Board, int turn){
		int row2 = Integer.parseInt(coords.substring(0,1));
		int column2= Integer.parseInt(coords.substring(1,2));
		for(int i=0; i<8;i++){
			for(int j=0;j<8;j++){
				if(Board[i][j]==null){
					continue;
				}else{
					if(Board[i][j].validMove(i, j, row2, column2, Board, turn) > 0){
						
						if(checkMate(row2, column2, Board, turn)) {
							System.out.println("Checkmate");
							return 2;
						} else {
							System.out.println("Check " + Board[i][j]);
							return 1;
						}
					}
				}
			}
		}
		return 0;
	}
	public int[] getPiece(String color, Piece[][] Board) {
		int r[] = new int[2];
		r[0] = -1;
		r[1] = -1;
		
		for(int i=0; i<8;i++) {
			for(int j=0;j<8;j++) {
				if(Board[i][j] != null && Board[i][j].color.equals(color)) {
					r[0] = i;
					r[1] = j;
					return r;
				}
			}
		}

		return r;
	}
	public int checkMateKing(char team, Piece[][] Board, int turn) {
		int r[] = getPiece("" + team + 'K', Board);
		if(r[0] == -1) {
			return -1;
		}
		return checkMate(r[0], r[1], Board, turn) ? 1 : 0;
	}
	public Piece copyPiece(Piece p) {
		String team = p.getTeam() == 'w' ? "White" : "Black";
		switch(p.getType()){
		case 'p':
			Pawn np = new Pawn(team);
			np.firstMove = ((Pawn)p).firstMove;
			return np;
		case 'K':
			King nk = new King(team);
			nk.firstMove = ((King)p).firstMove;
			return nk;
		case 'Q':
			return new Queen(team);
		case 'R':
			Rook nr = new Rook(team);
			nr.firstMove = ((Rook)p).firstMove;
			return nr;
		case 'B':
			return new Bishop(team);
		case 'N':
			return new Knight(team);
		default:
			//INVALID INPUT ERROR HERE
			return null;
		}
	}
	public Piece[][] copyBoard(Piece[][] Board) {
		Piece[][] newBoard = new Piece[8][8];
		for(int i = 0; i < Board.length; i++) {
			for(int j = 0; j < Board[i].length; j++) {
				newBoard[i][j] = copyPiece(Board[i][j]);
			}
		}
		return newBoard;
	}
	public boolean checkMate(int row, int col, Piece[][]Board, int turn) {
		Piece k = Board[row][col];
		if(k == null || k.getType() != 'K') {
			return false;
		}
		King king = (King) k;
		if(king.underAttack(row, col, Board, turn) != null) {
			// King can move out of check
			for(int i = -1; i < 2; i++) {
				for(int j = -1; j < 2; j++) {
					if((i == 0 && j == 0) || row + i > 7 || col + j > 7 || row + i < 0 || col + j < 0) {
						continue;
					}
					if(king.validMove(row, col, row + i, col + j, Board, turn) > 0 &&
							king.underAttack(row + i, col + j, Board, turn + 1) == null) {
						return false;
					}
				}
			}
			
			// Check if something can block/attack
			Piece attacker = king.underAttack(row, col, Board, turn);
			int[] attackingCoord = king.underAttackPos(row, col, king.getTeam(),Board, turn);
			
//			System.out.println("Attacker coords: " + (char)('a' + attackingCoord[1]) + (8 - attackingCoord[0])); 
			
			int colDiff = attackingCoord[1] - col;
			int colChange = colDiff == 0 ? 0 : colDiff > 0 ? -1 : 1;
			
			int rowDiff = attackingCoord[0] - row;
			int rowChange = rowDiff == 0 ? 0 : rowDiff > 0 ? -1 : 1;
			
			for(int i = 1; i < Math.abs(rowDiff); i++) {
				int attRow = attackingCoord[0] + (rowChange * i);
				int attCol = attackingCoord[1] + (colChange * i);
				if(attacker.underAttack(attRow, attCol, Board, turn) != null) {
					return false;
				}
			}			
			
			return true;
		}
		
		
		return false;
	}
}