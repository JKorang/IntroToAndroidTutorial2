package edu.josephkorang.tutorial2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TicTacToeActivity extends ActionBarActivity {

    // Buttons making up the board
    private Button mBoardButtons[];

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mWinsValue;
    private TextView mLossesValue;
    private TextView mTiesValue;

    // Who plays first next game
    private Boolean mFirstMove;

    // Prevents input after game ends
    private Boolean mGameOver;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mWinsValue = (TextView) findViewById(R.id.humanWinsValue);
        mLossesValue = (TextView) findViewById(R.id.androidWinsValue);
        mTiesValue = (TextView) findViewById(R.id.tiesValue);

        initScores();

        mFirstMove = true;
        mGame = new TicTacToeGame();

        startNewGame();
    }

    private void initScores() {
        //Handle saving/loading of wins and losses
        SharedPreferences scores = this.getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE);
        Boolean init = scores.getBoolean("init", false);
        //Verify if the fields have been setup yet
        //If not, create them within SharedPreferences
        if (init == false) {
            scores.edit().putInt("wins", 0).commit();
            scores.edit().putInt("ties", 0).commit();
            scores.edit().putInt("losses", 0).commit();
            scores.edit().putBoolean("init", true).commit();
        }
        //The scores do exist. Load them into the appropriate TextViews.
        else {
            mWinsValue.setText(String.valueOf(scores.getInt("wins", 0)));
            mTiesValue.setText(String.valueOf(scores.getInt("ties", 0)));
            mLossesValue.setText(String.valueOf(scores.getInt("losses", 0)));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }


    // Handles menu item selections
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }


    // Set up the game board.
    private void startNewGame() {

        mGame.clearBoard();
        mGameOver = false;

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        if (mFirstMove == true) {
            // Human goes first
            mInfoTextView.setText(R.string.first_human);
        }
        else {
            // Computer goes first
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
        }

    }

    private void setMove(char player, int location) {
            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);

                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    setScores("ties");
                }

                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    setScores("wins");
                }

                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    setScores("losses");
                }
            }

            // Remove the buttons as clickable
            if (mGameOver == true) {
                for (int i = 0; i < mBoardButtons.length; i++) {
                    mBoardButtons[i].setEnabled(false);
                }

                //Change who goes first next game
                if (mFirstMove == true) {
                    mFirstMove = false;
                }
                else {
                    mFirstMove = true;
                }
            }
        }
    }

    private void setScores(String typeOfVictory) {
        int scoreToChange = getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE).getInt(typeOfVictory, 0);
        getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE).edit().putInt(typeOfVictory, scoreToChange+1).commit();

        mGameOver = true;
        initScores();
    }
}