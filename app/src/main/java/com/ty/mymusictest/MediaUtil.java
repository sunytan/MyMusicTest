
package com.ty.mymusictest;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by edz on 2017/9/1.
 */

@SuppressWarnings("unused")
public class MediaUtil {

    // 用于保存 每个MediaPlayer实例以及其来源
    private static ConcurrentHashMap<String, MediaPlayer> playerMap = new ConcurrentHashMap<>();

    // 创建一个最多5个线程的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    MediaPlayer mp;

    // 创建MediaPlayer单例
    private static MediaUtil instance = new MediaUtil();

    public static MediaUtil getInstance() {
        return instance;
    }

    public MediaUtil createMediaPlayer(String name) {
        synchronized (MediaUtil.class) {
            if (playerMap.size() > 5) {

            }
            mp = new MediaPlayer();
            playerMap.put(name, mp);
        }
        //executorService.execute(new MediaThread(name, streamType, path, uri, block, interrupt));
        return this;
    }

/*    private class MediaThread extends Thread {
        String name;
        String path;
        boolean interrupt;
        boolean block;
        int streamType;
        Uri uri;

        public MediaThread(String name, int streamType, String path, Uri uri, boolean block,
                boolean interrupt) {
            super();
            this.name = name;
            this.path = path;
            this.interrupt = interrupt;
            this.block = block;
            this.uri = uri;
            this.streamType = streamType;
        }

        @Override
        public void run() {
            super.run();
            synchronized (MediaUtil.class){

            }
        }
    }*/

    public void play(String name, String path) {
        play(name, path, false);
    }

    public void play(String name, Uri uri) {
        play(name, uri, false);
    }

    public void play(String name, Uri uri, boolean interrupt) {
        play(name, -1, null, uri, false, interrupt);
    }

    public void play(String name, String path, boolean interrupt) {
        play(name, -1, path, null, false, interrupt);
    }

    public void play(String name, String path, boolean block, boolean interrupt) {
        play(name, -1, path, null, false, interrupt);
    }

    public void play(String name, int streamType, Uri uri, boolean block, boolean interrupt) {
        play(name, streamType, null, uri, block, interrupt);
    }

    public void play(String name, int streamType, String path, boolean block, boolean interrupt) {
        play(name, streamType, path, null, block, interrupt);
    }

    private void play(String name, int streamType, String path, Uri uri, boolean block,
            boolean interrupt) {

        synchronized (MediaUtil.class) {

            if (playerMap.size() > 5)
                return;

            if (TextUtils.isEmpty(name))
                return;

            MediaPlayer player = new MediaPlayer();
            playerMap.put(name, player);

            if (interrupt) {
                if (playerMap.size() > 0){

                   for (MediaPlayer media:playerMap.values()){
                       media.release();

                       if (media.isPlaying() && interruptCallBack != null){
                           interruptCallBack.onInterrupt();
                       }
                   }
                }
            }




        }
    }

    public void pause(){

    }

    public void stop(){

    }


    public interface CompleteCallBack {
        void onComplete();
    }

    private CompleteCallBack completeCallBack;

    public void setCompleteCallBack(CompleteCallBack completeCallBack) {
        this.completeCallBack = completeCallBack;
    }

    public interface ErrorCallBack {
        void onError();
    }

    private ErrorCallBack errorCallBack;

    public void setErrorCallBack(ErrorCallBack errorCallBack) {
        this.errorCallBack = errorCallBack;
    }

    public interface InterruptCallBack {
        void onInterrupt();
    }

    private InterruptCallBack interruptCallBack;

    public void setInterruptCallBack(InterruptCallBack interruptCallBack) {
        this.interruptCallBack = interruptCallBack;
    }

}
