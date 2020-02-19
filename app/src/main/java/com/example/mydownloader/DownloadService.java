package com.example.mydownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {


    DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onCanceled() {

        }

        @Override
        public void onFinished() {

        }

        @Override
        public void onFailure() {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    NotificationManager getNoficationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    Notification getNotification(String str,int progress){
        Notification notification = new NotificationCompat.Builder(this).build();

        return null;
    }


}
