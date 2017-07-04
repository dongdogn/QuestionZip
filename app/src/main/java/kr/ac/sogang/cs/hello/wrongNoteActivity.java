package kr.ac.sogang.cs.hello;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class wrongNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_note);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }


    public void onUpload(View v){
        Intent intent = new Intent(wrongNoteActivity.this, UploadQuestion.class);
        startActivity(intent);
        finish();
    }


    public void pdfprint(View v){
        //레이아웃은 res>layout>activity_wrong_note.xml 에서 확인
        //오답노트 activity에서 버튼을 누르면 이 함수를 실행함


        //res/assets에 있는 pdf파일 읽어서 open하자
        CopyReadPDFFromAssets();

        //openPDF();


    }


    //private void openPDF(String contentsPath){
    private void openPDF(){
        //File file = new File(getFilesDir(), "my_wrong_qs.pdf");

        File file = new File("android.resource://"+getPackageName()+"/"+R.raw.my_wrong_qs);

        if(file.exists()){
            //입력한 파일 정보로 Uri 객체 생성
            Uri path = Uri.fromFile(file);


            //인텐트 객체를 만들고 setDataAndType()에 application/pdf로 선택하여
            //암시적 인텐트 구성 : 설치된 앱 중 PDF 뷰어 선택 가능하도록
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try{
                //액티비티 띄우기
                startActivity(intent);
            } catch (ActivityNotFoundException ex){
                //application/pdf에 해당되는 앱이 없을 경우
                Toast.makeText(this,"PDF파일을 보기 위한 뷰어 맵이 없습니다.",Toast.LENGTH_SHORT).show();

            }

        }else {
            Toast.makeText(this,"PDF파일이 없습니다.",Toast.LENGTH_SHORT).show();
        }

    }
    private void CopyReadPDFFromAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "my_wrong_qs.pdf");
        //File file = new File("android.resource://"+getPackageName()+"/"+R.raw.test);

        try
        {
            in = assetManager.open("my_wrong_qs.pdf");
    /*        if(file.exists()) {
                Toast.makeText(this,"PDF파일이 있습니다.",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"PDF파일이 없습니다.",Toast.LENGTH_SHORT).show();
            }*/
            out = openFileOutput(file.getName(), Context.MODE_PRIVATE);

            copyPdfFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("exception", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/my_wrong_qs.pdf"),
                "application/pdf");

        try{
            //액티비티 띄우기
            startActivity(intent);
        } catch (ActivityNotFoundException ex){
            //application/pdf에 해당되는 앱이 없을 경우
            Toast.makeText(this,"PDF파일을 보기 위한 뷰어 맵이 없습니다.",Toast.LENGTH_SHORT).show();

        }

        //openPDF();

    }

    private void copyPdfFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }


}