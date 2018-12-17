package com.example.swetha.braintrainer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button go;
    TextView question;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    int locationOfCorrectAns;
    TextView result;
    TextView score;
    TextView timer;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button playAgain;
    int scoreNumo=0;
    int scoreDeno=0;

    boolean gameIsover = false;


    public void onStart(View view){
        go.setVisibility(View.INVISIBLE);

    }

    public void chooseOption(View view){

        if(gameIsover == false) {
            if (Integer.toString(locationOfCorrectAns).equals(view.getTag().toString())) {
                Log.i("Correct", "correct");
                result.setText("Correct!!");
                result.setBackgroundResource(R.color.green);
                scoreNumo = scoreNumo + 1;
            } else {
                result.setText("Wrong!!!");
                result.setBackgroundResource(R.color.red);

            }
            scoreDeno++;
            score.setText(scoreNumo + "/" + scoreDeno);
            generateQuestion();
        }

    }

    public void generateQuestion(){

        if(gameIsover == false) {
            Random random = new Random();

            int a = random.nextInt(50) + 1;
            int b = random.nextInt(50) + 1;

            question = (TextView) findViewById(R.id.questionTextView);
            question.setText(a + " + " + b);

            answers.clear();
            int incorrectAns;

            locationOfCorrectAns = random.nextInt(4) + 1;
            for (int i = 1; i < 5; i++) {
                if (i == locationOfCorrectAns) {
                    answers.add(a + b);
                } else {
                    incorrectAns = random.nextInt(51);
                    while (incorrectAns == a + b) {
                        incorrectAns = random.nextInt(51);
                    }
                    answers.add(incorrectAns);
                }
            }

            button0 = findViewById(R.id.button0);
            button1 = findViewById(R.id.button1);
            button2 = findViewById(R.id.button2);
            button3 = findViewById(R.id.button3);

            button0.setText(Integer.toString(answers.get(0)));
            button1.setText(Integer.toString(answers.get(1)));
            button2.setText(Integer.toString(answers.get(2)));
            button3.setText(Integer.toString(answers.get(3)));

        }

    }

    public void playGameAgain(View view){
       gameIsover=false;
       scoreDeno=0;
       scoreNumo=0;

       if(gameIsover == false) {

           generateQuestion();


           new CountDownTimer(10100, 1000) {

               @Override
               public void onTick(long l) {
                   timer.setText(String.valueOf(l / 1000) + "s");
               }

               @Override
               public void onFinish() {
                   timer.setText("0s");
                   result.animate().translationY(1f).setDuration(2000);
                   result.animate().rotation(360).setDuration(2000);
                   result.setText("Quiz completed");
                   result.setBackgroundResource(R.color.lightBlue);
                   gameIsover = true;
                   playAgain.setVisibility(View.VISIBLE);

               }
           }.start();

       }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            go = (Button) findViewById(R.id.Gobutton);
            result = findViewById(R.id.resultTextView);
            score = findViewById(R.id.scoreTextView);
            timer = findViewById(R.id.timerTextView);
            playAgain=findViewById(R.id.playAgainbutton);
            playGameAgain(findViewById(R.id.playAgainbutton));



    }
}
