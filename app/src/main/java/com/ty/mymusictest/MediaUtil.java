
package com.ty.mymusictest;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import java.io.FileDescriptor;
import java.io.IOException;
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

    boolean isPause = false;

    private Context context;
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

    public void setContext(Context context) {
        this.context = context;
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

    private void play(final String name, int streamType, String path, Uri uri, boolean block,
                      boolean interrupt) {

        synchronized (MediaUtil.class) {

            if (playerMap.size() > 5)
                return;

            if (TextUtils.isEmpty(name))
                return;

            if (isPause) {
                isPause = false;
                playerMap.get(name).start();
                return;
            }

            if (interrupt) {
                if (playerMap.size() > 0) {
                    // 停止其他所有播放
                    for (MediaPlayer media : playerMap.values()) {

                        if (media.isPlaying() && interruptCallBack != null) {
                            media.stop();
                            interruptCallBack.onInterrupt();
                        }
                        media.release();

                    }
                    // 从Map移除
                    for (String n : playerMap.keySet()) {
                        playerMap.remove(n);
                    }
                }
            }

            final MediaPlayer player = new MediaPlayer();
            playerMap.put(name, player);

            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor fd = null;

            try {
                fd = assetManager.openFd(path);
                player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                player.setAudioStreamType(streamType);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        playerMap.remove(name);
                        if (completeCallBack != null) {
                            completeCallBack.onComplete();
                        }
                    }
                });
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fd.close();
                } catch (Exception e) {

                }
            }

            if (block) {
                try {
                    Thread.sleep(player.getDuration()+200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void pause(String name) {
        MediaPlayer mediaPlayer = playerMap.get(name);
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            isPause = true;
            mediaPlayer.pause();
        }
    }

    public void stop(String name) {
        MediaPlayer mediaPlayer = playerMap.get(name);
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
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
