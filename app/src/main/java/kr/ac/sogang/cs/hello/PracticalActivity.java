package kr.ac.sogang.cs.hello;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PracticalActivity extends AppCompatActivity {
    final int questions = 30;
    int questionNum = 0;
    TextView[] q = new TextView[6];
    TextView q_pr;
    ImageView[] ans_image = new ImageView[5];
    int[] userAnswer = {-1, -1, -1, -1, -1};
    int[] questionAnswer = new int[5];
    ScrollView scrl;
    Handler mHandler;
    public int mainTime = 0;
    int[] randidx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        q[0] = (TextView) findViewById(R.id.question10);
        q[1] = (TextView) findViewById(R.id.question11);
        q[2] = (TextView) findViewById(R.id.question12);
        q[3] = (TextView) findViewById(R.id.question13);
        q[4] = (TextView) findViewById(R.id.question14);
        q[5] = (TextView) findViewById(R.id.question15);
        scrl = (ScrollView) findViewById(R.id.verScroll);
        ans_image[0] = (ImageView) findViewById(R.id.answer1);
        ans_image[1] = (ImageView) findViewById(R.id.answer2);
        ans_image[2] = (ImageView) findViewById(R.id.answer3);
        ans_image[3] = (ImageView) findViewById(R.id.answer4);
        ans_image[4] = (ImageView) findViewById(R.id.answer5);

        viewAnswer(-1);

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                TextView timertest = (TextView)findViewById(R.id.q_timer);
                super.handleMessage(msg);
                int div = msg.what;
                int min = mainTime / 60;
                int sec = mainTime % 60;
                String strTime = String.format("%02d : %02d", min, sec);
                this.sendEmptyMessageDelayed(0,1000);
                timertest.setText(strTime);
                timertest.invalidate();
                mainTime++;
            }
        };
        mHandler.sendEmptyMessage(1);

        q_pr = (TextView)findViewById(R.id.q_process);
        q_pr.setText(questionNum + "/5");

        recommend();
    }

    private void recommend(){
        randidx = new int[5];
        for(int i=0; i<5; i++){
            randidx[i] = (int)(Math.random()*questions);
            for(int j=0; j<i; j++){
                if(randidx[i] == randidx[j]){
                    i--;
                    break;
                }
            }
        }
        String tmp = "모의고사 기출문제 중 랜덤으로 문제를 추천하겠습니다\n랜덤문제: ";
        for(int i=0; i<5; i++)
            tmp += randidx[i] + " ";
        tmp += "\n";
        q[0].setText(tmp);
//        q[0].setText("모의고사 기출문제 중 랜덤으로 문제를 추천하겠습니다");
        viewQuestions(randidx);
    }

    public void viewQuestions(int index[]){
        String urladdr = "http://questionzip.dothome.co.kr/SelectQ.php";
        String result = connectionURL(urladdr);
        ParseJSON_Q(result, index);
    }

    public void ParseJSON_Q(String target, int index[]){
        String tmp;
        try{
            JSONObject json = new JSONObject(target);
            JSONArray arr = json.getJSONArray("result");
            for(int i=0; i<5; i++){
                JSONObject json2 = arr.getJSONObject(index[i]);
                tmp = "";
                tmp += json2.get("q") + "\n\n";
                tmp += json2.get("zimoon") + "\n\n";
                tmp += ("① " + json2.get("sol1") + "\n");
                tmp += ("② " + json2.get("sol2") + "\n");
                tmp += ("③ " + json2.get("sol3") + "\n");
                tmp += ("④ " + json2.get("sol4") + "\n");
                tmp += ("⑤ " + json2.get("sol5") + "\n");
                q[i+1].setText(tmp);
                tmp = "";
                tmp += json2.get("answer");
                questionAnswer[i] = Integer.parseInt(tmp);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackButton(View v){
        Toast.makeText(getApplicationContext(), "Back to menu", Toast.LENGTH_LONG).show();
        finish();
    }

    public void onRecommendPrev(View v){
        if(questionNum!=0){
            q[questionNum].setVisibility(View.INVISIBLE);
            questionNum--;
            scrl.smoothScrollTo(0,0);
            q[questionNum].setVisibility(View.VISIBLE);
            if(questionNum!=0)
                viewAnswer(userAnswer[questionNum-1]-1);
            else
                viewAnswer(-1);
        }
        q_pr.setText(questionNum + "/5");
    }

    public void onRecommendNext(View v){
        if(questionNum!=5){
            q[questionNum].setVisibility(View.INVISIBLE);
            questionNum++;
            scrl.smoothScrollTo(0,0);
            q[questionNum].setVisibility(View.VISIBLE);
            viewAnswer(userAnswer[questionNum-1]-1);

            q_pr.setText(questionNum + "/5");
        }
        else{
            if(userAnswer[0]==-1 || userAnswer[1]==-1 || userAnswer[2]==-1 || userAnswer[3]==-1 || userAnswer[4]==-1 ) {
                startActivity(new Intent(this, DialogActivity.class));
            }
            else {
                int answer = 0;         //00000~11111 까지의 이진수처럼 다룬다 5문제 각각 맞으면 0, 틀리면 1
                for(int i=0; i<5; i++){
                    if(userAnswer[i] != questionAnswer[i]){
                        answer++;
                    }
                    if(i != 4)    answer *= 10;
                }
                Intent intent = new Intent(PracticalActivity.this, ScoringActivity.class);
                intent.putExtra("prevActivity", 2);
                intent.putExtra("timer", mainTime);
                intent.putExtra("answer", answer);
                for(int i=0; i<5; i++)
                    intent.putExtra("q" + i, randidx[i]);
                startActivity(intent);
                finish();
            }
        }
    }

    public void ansSelected(View v){
        int selected = -1;

        switch(v.getId()){
            case R.id.answer1:
                selected = 1;     break;
            case R.id.answer2:
                selected = 2;     break;
            case R.id.answer3:
                selected = 3;     break;
            case R.id.answer4:
                selected = 4;     break;
            case R.id.answer5:
                selected = 5;     break;
            default:              break;
        }

        viewAnswer(selected-1);
        if(questionNum!=0)
            userAnswer[questionNum-1] = selected;
    }

    private void viewAnswer(int selected){
        for(int i=0; i<5; i++)
            ans_image[i].setAlpha(0);
        if(selected > -1)
            ans_image[selected].setAlpha(200);
    }

    static public String connectionURL(String urladdr){
        HttpURLConnection conn;
        String line = "";
        String result = "";
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(urladdr);
            int redirectedCount = 0;
            while(redirectedCount <= 1) {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Android");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    while((line=in.readLine())!=null){
                        result = result.concat(line);
                    }

                    is.close();
                    in.close();
                    conn.disconnect();
                    break;
                } else if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                    String redirectURL = conn.getHeaderField("Location");
                    url = new URL(redirectURL);
                }
                redirectedCount++;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

}
