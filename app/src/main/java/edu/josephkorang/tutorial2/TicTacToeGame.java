package edu.josephkorang.tutorial2;


import java.util.Random;


public class TicTacToeGame {

    public static final int BOARD_SIZE = 9;

    // Characters used to represent the human, computer, and open spots
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    // Random number generator
    private Random mRand;

    // Represents the game board
    private char mBoard[];

    public TicTacToeGame() {
        mBoard = new char[BOARD_SIZE];
        mRand = new Random();
    }

    /** Clear the board of all X's and O's. */
    public void clearBoard(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /** Set the given player at the given location on the game board.
     *  The location must be available, or the board will not be changed.
     *
     * @param player - The human or computer player
     * @param location - The location (0-8) to place the move
     */
    public void setMove(char player, int location){
        mBoard[location] = player;
    }

    /**
     * Check for a winner.  Return a status value indicating the board status.
     * @return Return 0 if no winner or tie yet, 1 if it's a tie, 2 if X won,
     * or 3 if O won.
     */
    public int checkForWinner(){
        //Horizontal
        for (int i = 0; i <= 6; i = i +3) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i+1] == HUMAN_PLAYER && mBoard[i+2] == HUMAN_PLAYER) {
                return 2;
            }
        }

        for (int i = 0; i <= 6; i = i +3) {
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i+1] == COMPUTER_PLAYER && mBoard[i+2] == COMPUTER_PLAYER) {
                return 3;
            }
        }

        //Vertical
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i+3] == HUMAN_PLAYER && mBoard[i+6] == HUMAN_PLAYER) {
                return 2;
            }
        }

        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i+3] == COMPUTER_PLAYER && mBoard[i+6] == COMPUTER_PLAYER) {
                return 2;
            }
        }

        //Diagonal
        if ((mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER)) {
            return 2;
        }
        if ((mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER)) {
            return 2;
        }

        if ((mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER)) {
            return 3;
        }
        if ((mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER)) {
            return 3;
        }

        //Check if finished
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                return 0;
            }
        }

        //No match found, tie
        return 1;
    }


    /** Return the best move for the computer to make. You must call setMove() to
     * actually make the computer move to that location.
     * @return The best move for the computer to make.
     */
    public int getComputerMove(){
        return getComputerRandomMove();
    }

    /** Generate a random move. Just need to make sure that the space is not yet occupied.
     *
     * @return The randomly chosen open location.
     */
    public int getComputerRandomMove() {
        int decision;
        do {
            decision = mRand.nextInt(9);
        }
        while (mBoard[decision] == HUMAN_PLAYER || mBoard[decision] == COMPUTER_PLAYER);
        return decision;
    }
}

