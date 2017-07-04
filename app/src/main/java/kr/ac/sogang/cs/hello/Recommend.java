package kr.ac.sogang.cs.hello;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Jama.Matrix;

import static kr.ac.sogang.cs.hello.PracticalActivity.connectionURL;

public class Recommend extends AppCompatActivity {
    final int row = 4, column = 30;
    int questionNum = 0;
    TextView[] q = new TextView[6];
    TextView q_pr;
    ImageView[] ans_image = new ImageView[5];
    int[] userAnswer = {-1, -1, -1, -1, -1};
    int[] questionAnswer = new int[5];
    int[] index = new int[30];                  //추천 문제 목록
    ScrollView scrl;
    Handler mHandler;
    public int mainTime = 0;
    Matrix mR, newmR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        //initialize matrix R
        double[][] A = new double[row][column];
        mR = new Matrix(A);

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
        viewQuestions();

    }


    public void recommend(){
        String urladdr = "http://questionzip.dothome.co.kr/SelectR2.php";
        String result = "";

        result = connectionURL(urladdr);

        ParseJSON_R(result);
        q[0].setText("Parsing Completed");
        matrix_factorization();
        q[0].setText("Calculating matrix R Completed");
        sort_recommend();
    }

    public void viewQuestions(){
        if(index[0]!=-1){
            String urladdr = "http://questionzip.dothome.co.kr/SelectQ.php";
            String result = connectionURL(urladdr);
            ParseJSON_Q(result);
        }
    }

    public void ParseJSON_R(String target){
        try{
            JSONObject json = new JSONObject(target);
            JSONArray arr = json.getJSONArray("result");
            for(int i=0; i<4; i++){
                JSONObject json2 = arr.getJSONObject(i);
                for(int j=1; j<=30; j++){
                    String tmp = json2.get("q"+j).toString();
                    double tmp2 = Double.parseDouble(tmp);
                    mR.set(i, j-1, tmp2);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void ParseJSON_Q(String target){
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


    public void matrix_factorization(){
        final int steps = 10000;
        final double alpha = 0.0002;
        final double beta = 0.02;

        double[][] PP = {{1, 3}, {2, 3}, {7, 1}, {4, 6}};
        double[][] QQ = {{1, 3}, {4, 1}, {2, 3}, {5, 1}, {1, 3}, {4, 2}, {2, 3}, {5, 1}, {2, 3}, {5, 1}, {1, 3}, {4, 1}, {2, 3}, {5, 1}, {1, 3}, {4, 2}, {2, 3}, {5, 1}, {2, 3}, {5, 1}, {1, 3}, {4, 1}, {2, 3}, {5, 1}, {1, 3}, {4, 2}, {2, 3}, {5, 1}, {2, 3}, {5, 1}};
        Matrix P = new Matrix(PP);
        Matrix Q = new Matrix(QQ);
        int K = 2;

        double eij;
        Q = Q.transpose();
        for(int step=0; step<steps; step++){
            for(int i=0; i<mR.getRowDimension(); i++){
                for(int j=0; j<mR.getColumnDimension(); j++){
                    if(mR.get(i, j)>0){
                        eij = mR.get(i, j) - ((P.getMatrix(i, i, 0, P.getColumnDimension()-1)).times(Q.getMatrix(0, Q.getRowDimension()-1, j, j))).get(0, 0);
                        for(int k=0; k<K; k++){
                            P.set(i, k, P.get(i, k) + alpha * (2*eij*Q.get(k, j) - beta * P.get(i, k)));
                            Q.set(k, j, Q.get(k, j) + alpha * (2*eij*P.get(i, k) - beta * Q.get(k, j)));
                        }
                    }
                }
            }
            double e = 0;
            for(int i=0; i<mR.getRowDimension(); i++){
                for(int j=0; j<mR.getColumnDimension(); j++){
                    if(mR.get(i, j)>0){
                        e = e + (mR.get(i, j) - ((P.getMatrix(i, i, 0, P.getColumnDimension()-1)).times(Q.getMatrix(0, Q.getRowDimension()-1, j, j))).get(0, 0))
                                * (mR.get(i, j) - ((P.getMatrix(i, i, 0, P.getColumnDimension()-1)).times(Q.getMatrix(0, Q.getRowDimension()-1, j, j))).get(0, 0));
                        for(int k=0; k<K; k++)
                            e = e + (beta/2) * ((P.get(i, k)* P.get(i, k)) + Q.get(k, j) * Q.get(k, j));
                    }
                }
            }
            if(e<0.001)
                break;
        }

        newmR = P.times(Q);

    }

    public void sort_recommend(){
        int idx = 1;
        double[] qarray = new double[30];

        for(int i=0; i<30; i++){
            qarray[i] = newmR.get(idx, i);
            index[i] = i;
        }

        for(int i=0; i<30; i++){
            double max = qarray[i];
            int indexmax = index[i];
            int tmp = i;
            for(int j=i+1; j<30; j++){
                if(qarray[j] >= max){
                    max = qarray[j];
                    indexmax = index[j];
                    tmp = j;
                }
            }
            qarray[tmp] = qarray[i];
            qarray[i] = max;
            index[tmp] = index[i];
            index[i] = indexmax;
        }

        String printstr = "당신을 위한 추천문제:\n";
        for(int i=0; i<5; i++){
            printstr += String.valueOf(index[i]+1);
            printstr += "번 (틀릴확률: ";
            printstr += String.format("%.2f", qarray[i]);
            printstr += ")\n";
        }
        q[0].setText(printstr);
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
                int answer = 0;         //00000~11111 이진수, 5문제 각각 맞으면 0, 틀리면 1
                for(int i=0; i<5; i++){
                    if(userAnswer[i] != questionAnswer[i]){
                        answer++;
                    }
                    if(i != 4)    answer *= 10;
                }
                Intent intent = new Intent(Recommend.this, ScoringActivity.class);
                intent.putExtra("prevActivity", 1);
                intent.putExtra("timer", mainTime);
                intent.putExtra("answer", answer);
                for(int i=0; i<5; i++)
                    intent.putExtra("q" + i, index[i]);
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

}
