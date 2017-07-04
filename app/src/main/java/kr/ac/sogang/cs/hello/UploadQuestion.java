package kr.ac.sogang.cs.hello;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadQuestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_question);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void onUpload(View v){
        String writedata = "";

        final CheckBox[] cb = new CheckBox[27];
        cb[0] = (CheckBox)findViewById(R.id.rq1);
        cb[1] = (CheckBox)findViewById(R.id.rq2);
        cb[2] = (CheckBox)findViewById(R.id.rq3);
        cb[3] = (CheckBox)findViewById(R.id.rq4);
        cb[4] = (CheckBox)findViewById(R.id.rq5);
        cb[5] = (CheckBox)findViewById(R.id.rq6);
        cb[6] = (CheckBox)findViewById(R.id.rq7);
        cb[7] = (CheckBox)findViewById(R.id.rq8);
        cb[8] = (CheckBox)findViewById(R.id.rq9);
        cb[9] = (CheckBox)findViewById(R.id.rq10);
        cb[10] = (CheckBox)findViewById(R.id.rq11);
        cb[11] = (CheckBox)findViewById(R.id.rq12);
        cb[12] = (CheckBox)findViewById(R.id.rq13);
        cb[13] = (CheckBox)findViewById(R.id.rq14);
        cb[14] = (CheckBox)findViewById(R.id.rq15);
        cb[15] = (CheckBox)findViewById(R.id.rq16);
        cb[16] = (CheckBox)findViewById(R.id.rq17);
        cb[17] = (CheckBox)findViewById(R.id.rq18);
        cb[18] = (CheckBox)findViewById(R.id.rq19);
        cb[19] = (CheckBox)findViewById(R.id.rq20);
        cb[20] = (CheckBox)findViewById(R.id.rq21);
        cb[21] = (CheckBox)findViewById(R.id.rq22);
        cb[22] = (CheckBox)findViewById(R.id.rq23);
        cb[23] = (CheckBox)findViewById(R.id.rq24);
        cb[24] = (CheckBox)findViewById(R.id.rq25);
        cb[25] = (CheckBox)findViewById(R.id.rq26);
        cb[26] = (CheckBox)findViewById(R.id.rq27);

        for(int i=0; i<27; i++){
            if(cb[i].isChecked()){
                boolean result = phpTest(i);     //start from 0
                if(result)
                    Toast.makeText(getApplication(), "upload completed: "+(i+19), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplication(), "upload failed: "+(i+19), Toast.LENGTH_SHORT).show();
            }
        }

        finish();

    }

    public boolean phpTest(final int qnum){
        String urladdr = "http://questionzip.dothome.co.kr/UploadR.php";

        try{
            String postData = "qnum=1";
            URL url = new URL(urladdr);

            HttpURLConnection conn2 = (HttpURLConnection)url.openConnection();
            conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn2.setRequestProperty("Connection", "close");
            conn2.setRequestMethod("POST");
            conn2.setConnectTimeout(10000);
            conn2.setDoOutput(true);
            conn2.setDoInput(true);
            conn2.setUseCaches(false);
            conn2.connect();

            OutputStream os = conn2.getOutputStream();
            os.write(postData.getBytes("EUC-KR"));
            os.flush();
            os.close();

            conn2.disconnect();
            return true;

        } catch(Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.e("phpTest", exceptionAsString);
        }
        return false;
    }

    public void onJuly(View v){
        Button btn = (Button)findViewById(R.id.july);
        if(btn.getAlpha()==0)
            btn.setAlpha(0.4f);
        else
            btn.setAlpha(0);
    }


}
