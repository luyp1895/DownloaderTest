package com.example.mydownloader;

public interface DownloadListener {
    void onProgress(int progress);
    void onPaused();
    void onCanceled();
    void onFinished();
    void onFailure();
}
