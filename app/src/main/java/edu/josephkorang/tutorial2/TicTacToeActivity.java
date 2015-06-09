package edu.josephkorang.tutorial2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;


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

    // Current turn tracker
    // True = Player, False = Computer
    private Boolean mTurnPlayer;

    // Prevents input after game ends
    private Boolean mGameOver;

    // Dialog Constraints
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_RESET_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    // Sound Effects
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        mGame = new TicTacToeGame();
        Log.i("TTTAct", "Build buttons, onCreate");

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

        if (savedInstanceState == null) {
            Log.i("TTTAct", "Begin game");
            mFirstMove = true;
            startNewGame();
        }
        initScores();
    }

    @Override
    protected void onResume() {
        Log.i("TTTAct", "Running onResume");
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sword);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swish);
    }

    @Override
    protected void onPause() {
        Log.i("TTTAct", "Running onPause");
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mFirstMove", mFirstMove);
        outState.putBoolean("mTurnPlayer", mTurnPlayer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        rebuildBoard(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mFirstMove = savedInstanceState.getBoolean("mFirstMove");
        mTurnPlayer = savedInstanceState.getBoolean("mTurnPlayer");
    }

    private void rebuildBoard(char[] board) {
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                mBoardButtons[i].setText("");
                mBoardButtons[i].setEnabled(true);
                mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
            } else {
                mBoardButtons[i].setText(String.valueOf(board[i]));
                mBoardButtons[i].setEnabled(false);
                if (board[i] == TicTacToeGame.HUMAN_PLAYER) {
                    mBoardButtons[i].setTextColor(Color.rgb(0, 200, 0));
                } else {
                    mBoardButtons[i].setTextColor(Color.rgb(200, 0, 0));
                }
            }
        }
    }

    private void initScores() {

        Log.i("TTTAct", "Begin obtaining scores");
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
        Log.i("TTTAct", "End obtaining scores");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("TTTAct", "Running onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    // Handles menu item selections
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                Log.i("TTTAct", "new_game menu button pressed");
                startNewGame();
                Toast.makeText(getApplicationContext(), R.string.newGame, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.ai_difficulty:
                Log.i("TTTAct", "ai_difficulty menu button pressed");
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;

            case R.id.about:
                Log.i("TTTAct", "about menu button pressed");
                showDialog(DIALOG_ABOUT_ID);
                return true;

            case R.id.resetScores:
                Log.i("TTTAct", "reset button pressed");
                showDialog(DIALOG_RESET_ID);
                return true;
        }
        return false;
    }


    // Set up the game board.
    private void startNewGame() {

        mGame.clearBoard();
        mGameOver = false;

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            Log.i("TTTAct", "Resetting buttons");
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        Log.i("TTTAct", "Set/begin first move");

        if (mFirstMove == true) {
            // Human goes first
            Log.i("TTTAct", "Waiting on human");
            mInfoTextView.setText(R.string.first_human);
            mTurnPlayer = true;
        } else {
            // Computer goes first
            Log.i("TTTAct", "Waiting on computer");
            mInfoTextView.setText(R.string.turn_computer);
            mTurnPlayer = false;
            computerPause();
        }

    }

    private void computerPause() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.v("TTTAct", "Computer Delay");
                int move = mGame.getComputerMove();
                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mInfoTextView.setText(R.string.turn_human);
            }
        }, 1000);
    }

    private void setMove(char player, int location) {
        Log.i("TTTAct", "Begin set move, player: " + player + ", loc: " + location);
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER) {
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            mHumanMediaPlayer.start();
        } else {
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
            mComputerMediaPlayer.start();
            Log.i("TTTAct", "End set move");
        }
        // Handle flop of button control
        if (mTurnPlayer == true) {
            mTurnPlayer = false;
        } else {
            mTurnPlayer = true;
        }
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mTurnPlayer == true) {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);

                    // If no winner yet, let the computer make a move
                    int winner = checkWinner();
                    if (winner == 0) {

                        mInfoTextView.setText(R.string.turn_computer);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Log.v("TTTAct", "Computer Delay");
                                int move = mGame.getComputerMove();
                                setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                                mInfoTextView.setText(R.string.turn_human);
                                int win = checkWinner();
                            }
                        }, 1000);
                    }
                }
                //Change who goes first next game
                if (mFirstMove == true) {
                    mFirstMove = false;
                } else {
                    mFirstMove = true;
                }
            }
        }
    }


    // Check for winner.
    // Triggered after each move to verify whether or not TTT is reached
    private int checkWinner() {
        int winner = mGame.checkForWinner();
        if (winner == 0)
            mInfoTextView.setText(R.string.turn_human);

        else if (winner == 1) {
            mInfoTextView.setText(R.string.result_tie);
            setScores("ties");
        } else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            setScores("wins");
        } else {
            mInfoTextView.setText(R.string.result_computer_wins);
            setScores("losses");
        }

        // Remove the buttons as clickable
        if (mGameOver == true) {
            for (int i = 0; i < mBoardButtons.length; i++) {
                mBoardButtons[i].setEnabled(false);
            }
        }
        return winner;
    }

    // Set the score within the Shared Preferences
    private void setScores(String typeOfVictory) {
        int scoreToChange = getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE).getInt(typeOfVictory, 0);
        getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE).edit().putInt(typeOfVictory, scoreToChange + 1).commit();

        mGameOver = true;
        initScores();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                Log.i("TTTAct", "Begin choose difficulty");
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // selected is the radio button that should be selected.
                int selected = -1;
                if (mGame.getmDifficultyLevel() == TicTacToeGame.difficultyLevel.Easy)
                    selected = 0;
                else if (mGame.getmDifficultyLevel() == TicTacToeGame.difficultyLevel.Harder)
                    selected = 1;
                else if (mGame.getmDifficultyLevel() == TicTacToeGame.difficultyLevel.Expert)
                    selected = 2;

                // final int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog

                                switch (item) {
                                    case 0:
                                        mGame.setmDifficultyLevel(TicTacToeGame.difficultyLevel.Easy);
                                        break;
                                    case 1:
                                        mGame.setmDifficultyLevel(TicTacToeGame.difficultyLevel.Harder);
                                        break;
                                    case 2:
                                        mGame.setmDifficultyLevel(TicTacToeGame.difficultyLevel.Expert);
                                        break;
                                }

                                startNewGame();

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;

            case DIALOG_RESET_ID:
                // Create the reset confirmation dialog
                Log.i("TTTAct", "Begin reset");
                builder.setMessage(R.string.reset_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences scores = getSharedPreferences("edu.josephkorang.tutorial2", MODE_PRIVATE);
                                //Verify if the fields have been setup yet
                                //If not, create them within SharedPreferences
                                scores.edit().putInt("wins", 0).commit();
                                scores.edit().putInt("ties", 0).commit();
                                scores.edit().putInt("losses", 0).commit();
                                scores.edit().putBoolean("init", true).commit();
                                initScores();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_ABOUT_ID:
                Log.i("TTTAct", "Begin about dialog");
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.dialog_about, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }

}