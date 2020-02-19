package com.example.mydownloader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textViewFileName;
    TextView textViewNumber;
    EditText editText;
    String url;
    Button buttonStart,buttonPause,buttonCancel;
    DownloadAsyncTask downloadAsyncTask;
    DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            progressBar.setProgress(progress);
            textViewNumber.setText(progress+"%");
        }

        @Override
        public void onPaused() {
            downloadAsyncTask = null;
            Toast.makeText(MainActivity.this,"Paused",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadAsyncTask = null;
            progressBar.setProgress(0);
            textViewNumber.setText(0+"%");
            Toast.makeText(MainActivity.this,"Canceled",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinished() {
            downloadAsyncTask = null;
            Toast.makeText(MainActivity.this,"Finished",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure() {
            downloadAsyncTask = null;
            Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if (grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"No Permission",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        textViewFileName = findViewById(R.id.textViewFileName);
        textViewNumber = findViewById(R.id.textViewNumber);
        editText = findViewById(R.id.editText);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonPause = findViewById(R.id.buttonPause);
        buttonStart = findViewById(R.id.buttonStart);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    "Manifest.permission.WRITE_EXTERNAL_STORAGE"
            },1);
        }

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    downloadAsyncTask = new DownloadAsyncTask(downloadListener);
                    url = editText.getText().toString();
                    url = "http://soft.imtt.qq.com/browser/21/qqbrowser_10.1.0.6331_GA_20820.apk";
                    downloadAsyncTask.execute(url);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadAsyncTask!=null){
                    downloadAsyncTask.pauseDownload();
                }
                return;
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadAsyncTask!=null){
                    downloadAsyncTask.cancelDownload();
                }
                else{
                    if(url==null){
                        return;
                    }
                    deleteDownloadedFile(url);
                    progressBar.setProgress(0);
                    textViewNumber.setText(0+"%");
                }
            }
        });


    }

    void deleteDownloadedFile(String url){
        if(url!=null){
            String fileName = url.substring(url.lastIndexOf("/"));
            File file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS)
                    .getPath() + fileName);
            if(file.exists()){
                file.delete();
            }
        }
    }
}
