package com.example.mydownloader;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 异步下载任务类
 */

public class DownloadAsyncTask extends AsyncTask<String, Integer, Integer> {
    public static final int FINISHED = 0;
    public static final int PAUSED = 1;
    public static final int CANCELED = 2;
    public static final int FAILURE = 3;

    public boolean isPaused() {
        return isPaused;
    }

    private boolean isPaused = false;
    private boolean isCanceled = false;

    private DownloadListener downloadListener;  //回调接口传入

     DownloadAsyncTask(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case FINISHED:
                downloadListener.onFinished();
                break;
            case PAUSED:
                downloadListener.onPaused();
                break;
            case CANCELED:
                downloadListener.onCanceled();
                break;
            case FAILURE:
                downloadListener.onFailure();
                break;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        downloadListener.onProgress(progress);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        RandomAccessFile randomAccessFile= null;
        InputStream is = null;
        String Url = strings[0];
        String fileName = Url.substring(Url.lastIndexOf("/"));
        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS)
                .getPath() + fileName);
        long downloadedLength = 0;
        if (file.exists()) {
            downloadedLength = file.length();
        }
        try {
            long contentLength = getContentLength(Url);

            Log.i("++++++++++++++", "doInBackground: "+contentLength);
            if (downloadedLength == contentLength) {
                return FINISHED;
            }
            if (contentLength == 0) {

                return FAILURE;
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(Url).build();
            Response response = okHttpClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {  //注意此处
                is = response.body().byteStream();
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(downloadedLength);
                byte[] bytes = new byte[1024];
                int total = 0;
                int len ;
                while ((len = is.read(bytes)) != -1) {
                    if (isPaused) {
                        return PAUSED;
                    } else if (isCanceled) {
                        return CANCELED;
                    } else {
                        total += len;
                        randomAccessFile.write(bytes, 0, len);
                        int progress = (int) ((total + downloadedLength)*100 / contentLength);//计算进度
                        publishProgress(progress);//  刷新进度
                    }
                }
                response.body().close();
                return FINISHED;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(is!=null){
                    is.close();
                }
                if (randomAccessFile!=null){
                    randomAccessFile.close();
                }
                if(isCanceled&&file!=null){
                    file.delete();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        return FAILURE;
    }

    private long getContentLength(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if ( response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            return contentLength;
        }
        return 0;
    }

      void pauseDownload(){
        isPaused = true;
    }
      void cancelDownload(){
        isCanceled = true;
    }


}
