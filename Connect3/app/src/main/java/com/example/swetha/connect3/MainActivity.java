package com.example.swetha.connect3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int flag = 0; //flag represents the player

    int[] gameState = {2,2,2,2,2,2,2,2,2}; //2 means unplayed

    int[][] winPositions = {{0,1,2},{3,4,5},{6,7,8},{0,4,8},{2,4,6},{0,3,6},{1,4,7},{2,5,8}};

    boolean gameActive = true;


    public void clickMe(View view) {

        ImageView img = (ImageView) view;

        int gridTag = Integer.parseInt(img.getTag().toString());
        System.out.println(gridTag);

        if (gameState[gridTag] == 2 && gameActive == true) {
            gameState[gridTag] = flag;
            if (flag == 0) {
                img.setImageResource(R.drawable.red);
                flag = 1;
            } else {
                img.setImageResource(R.drawable.yellow);
                flag = 0;
            }

            for (int[] winPosition : winPositions) {
                if ((gameState[winPosition[0]] != 2)
                        && (gameState[winPosition[0]] == gameState[winPosition[1]])
                        && (gameState[winPosition[1]] == gameState[winPosition[2]])) {
                    System.out.println(gameState[winPosition[0]] + "won");
                    gameActive = false;

                    LinearLayout ll = (LinearLayout) findViewById(R.id.playAgain);
                    TextView text = findViewById(R.id.displayText);

                    if (ll.getVisibility() == View.INVISIBLE) {
                        ll.setVisibility(View.VISIBLE);
                        if (gameState[winPosition[0]] == 0) {
                            text.setText("Player RED won the game!!");
                        } else {
                            text.setText("Player YELLOW won the game!!");
                        }

                    }
                }
                else{

                    boolean gameIsOver = true;

                    for(int count:gameState) {
                        if (count == 2) {
                            gameIsOver = false;
                        }
                    }
                        if(gameIsOver){

                            LinearLayout ll = (LinearLayout) findViewById(R.id.playAgain);
                            TextView text = findViewById(R.id.displayText);
                            ll.setVisibility(View.VISIBLE);
                            text.setText("Its a draw!!!");
                    }

                }
            }
        }
    }

    public void playAgain(View view){
        LinearLayout ll = (LinearLayout) findViewById(R.id.playAgain);
        ll.setVisibility(View.INVISIBLE);
        flag = 0;
        for(int i=0;i<gameState.length;i++){
            gameState[i] = 2;
        }

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        for(int i=0 ; i< grid.getChildCount();i++){
            ((ImageView) grid.getChildAt(i)).setImageResource(0);
        }
        gameActive = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
