package kr.ac.sogang.cs.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import static kr.ac.sogang.cs.hello.PracticalActivity.connectionURL;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        int qnum = intent.getIntExtra("question", -1);
        if(qnum!=-1){
            viewQuestions(qnum);
        }
    }

    private void onBackButton(View v){
        finish();
    }

    public void viewQuestions(int index){
        String urladdr = "http://questionzip.dothome.co.kr/SelectQ.php";
        String result = connectionURL(urladdr);
        ParseJSON_Q(result, index);
    }

    public void ParseJSON_Q(String target, int index){
        TextView txtView = (TextView)findViewById(R.id.comment);
        String tmp;
        try{
            JSONObject json = new JSONObject(target);
            JSONArray arr = json.getJSONArray("result");
            JSONObject json2 = arr.getJSONObject(index);
            tmp = "";
            tmp += json2.get("comment") + "\n\n";
            tmp += json2.get("zimoon");
            txtView.setText(tmp);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
