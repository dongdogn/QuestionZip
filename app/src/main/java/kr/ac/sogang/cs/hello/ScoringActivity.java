package kr.ac.sogang.cs.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/* scoring activity는 recommend activity 또는 practical activity에서
* 불러야만 나타나는 activity이다
* scoring activity를 부를 때 전달할 parameter들:
* int prevActivity: 이전에 recommend였으면 1, practical이었으면 2
* int timer: 문제 푸는데 소요된 시간
* int answer: 00000~11111 까지의 이진수처럼 다룬다 5문제 각각 맞으면 0, 틀리면 1
 */

public class ScoringActivity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        intent = getIntent();

        TextView el_time = (TextView)findViewById(R.id.el_time);
        int min, sec;
        min = intent.getIntExtra("timer", -1);
        sec = min%60;
        min /= 60;
        el_time.setText(String.format("%02d : %02d", min, sec));

        TextView[] score = new TextView[5];
        score[0] = (TextView)findViewById(R.id.score11);
        score[1] = (TextView)findViewById(R.id.score12);
        score[2] = (TextView)findViewById(R.id.score13);
        score[3] = (TextView)findViewById(R.id.score14);
        score[4] = (TextView)findViewById(R.id.score15);

        int answer = intent.getIntExtra("answer", -1);
        for(int i=0; i<5; i++){
            if(answer%10 == 1)
                score[4-i].setText("X");
            answer /= 10;
        }

        //print the answer of user
/*        Intent intent = getIntent();
        for(int i=0; i<5; i++){
            int tmp = intent.getIntExtra("a"+(i+1), -1);
            TextView tv = (TextView)findViewById(R.id.score11);
            switch(i){
                case 0: tv = (TextView)findViewById(R.id.score11);
                    break;
                case 1: tv = (TextView)findViewById(R.id.score21);
                    break;
                case 2: tv = (TextView)findViewById(R.id.score31);
                    break;
                case 3: tv = (TextView)findViewById(R.id.score41);
                    break;
                case 4: tv = (TextView)findViewById(R.id.score51);
                    break;
                default: break;
            }
            tv.setText(String.valueOf(tmp));
        }

        for(int i=0; i<5; i++){
            int tmp = intent.getIntExtra("ra"+(i+1), -1);
            TextView tv = (TextView)findViewById(R.id.score12);
            switch(i){
                case 0: tv = (TextView)findViewById(R.id.score12);
                    break;
                case 1: tv = (TextView)findViewById(R.id.score22);
                    break;
                case 2: tv = (TextView)findViewById(R.id.score32);
                    break;
                case 3: tv = (TextView)findViewById(R.id.score42);
                    break;
                case 4: tv = (TextView)findViewById(R.id.score52);
                    break;
                default: break;
            }
            tv.setText(String.valueOf(tmp));
        }*/

    }

    public void sendToWrongNote(View v){
        //to do
        Toast.makeText(getApplicationContext(), "into wrong note", Toast.LENGTH_LONG).show();
    }

    public void viewComment(View v){
        int selected = -1;
        Intent next_intent;
        switch(v.getId()){
            case R.id.comment1:
                selected = intent.getIntExtra("q0", -1);
                break;
            case R.id.comment2:
                selected = intent.getIntExtra("q1", -1);
                break;
            case R.id.comment3:
                selected = intent.getIntExtra("q2", -1);
                break;
            case R.id.comment4:
                selected = intent.getIntExtra("q3", -1);
                break;
            case R.id.comment5:
                selected = intent.getIntExtra("q4", -1);
                break;
        }

        if(selected!=-1){
            next_intent = new Intent(ScoringActivity.this, CommentActivity.class);
            next_intent.putExtra("question", selected);
            startActivity(next_intent);
        }
    }

    public void moreQuestion(View v){
        int prevActivity = intent.getIntExtra("prevActivity", -1);
        Intent next_intent;
        switch (prevActivity){
            case 1:
                next_intent = new Intent(ScoringActivity.this, Recommend.class);
                startActivity(next_intent);
                break;
            case 2:
                next_intent = new Intent(ScoringActivity.this, PracticalActivity.class);
                startActivity(next_intent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Error exists", Toast.LENGTH_LONG).show();
                break;
        }
        finish();
    }

    public void onBackButton(View v){
        finish();
    }

}
