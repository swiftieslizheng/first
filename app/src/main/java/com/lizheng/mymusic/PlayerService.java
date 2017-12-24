package com.lizheng.mymusic;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import com.lizheng.mymusic.util.AppConstant;
import com.lizheng.mymusic.util.SongsUtil;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.LibsChecker;


public class PlayerService extends Service {
    private MusicBinder musicBinder =new MusicBinder();
    class MusicBinder extends Binder{
        public void play(int position,String path) {
            try {
                mediaPlayer.reset();//把各项参数恢复到初始状态
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();  //进行缓冲
                mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
                seekBarProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPause = true;
            }
        }
        public void resume() {
            if (isPause) {
                mediaPlayer.start();
                isPause = false;
            }
        }
        public void stop() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
        /**
         * 上一首
         */
        public void previous(String path) {
            play(0,path);
        }
        /**
         * 下一首
         */
        public void next(String path) {
            play(0,path);
        }
        public void seekbar(int prgress){
            mediaPlayer.seekTo(prgress);
        }
//        public int current(){
//            int time= mediaPlayer.getCurrentPosition();
//            Log.i("TAG", "current: "+time);
//            return mediaPlayer.getCurrentPosition() /1000;
//        }
//        public int total(){
//
//            Log.i("TAG", "current: total......"+mediaPlayer.getDuration());
//            return mediaPlayer.getDuration() /1000;
//        }


//        public void speednormal(String path){
//            try {
//                mediaPlayer.setDataSource(path);
//                mediaPlayer.setPlaybackSpeed(1.4f);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        public void speedlow(String path){
//            try {
//                mediaPlayer.setDataSource(path);
//                mediaPlayer.setPlaybackSpeed(0.8f);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }
    public void seekBarProgress(){
         final int total =  mediaPlayer.getDuration();
         final  Timer timer =new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                int position =  mediaPlayer.getCurrentPosition();
//                Message ms = Message.obtain();
//                Bundle bundle=new Bundle();
//                bundle.putInt("total",total);
//                Log.i("seekbar","歌曲总长度"+total);
//                bundle.putInt("position",position);
//                Log.i("seekbar","歌曲"+position);
//                ms.setData(bundle);
//                MainActivity.handler.sendMessage(ms);
            }
        };
        timer.schedule(task,100,200);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                timer.cancel();
                task.cancel();
            }
        });
    }
    private MediaPlayer mediaPlayer = new MediaPlayer();       //媒体播放器对象
   // private io.vov.vitamio.MediaPlayer speedmediaPlayer = new io.vov.vitamio.MediaPlayer(this);
 //   private String path;                        //音乐文件路径
    private boolean isPause;//暂停状态
    //  private List<Mp3Info> mp3Infos;   //存放Mp3Info对象的集合


    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        path = intent.getStringExtra("url");
//
//        int msg = intent.getIntExtra("MSG", 0);
//        if (msg == AppConstant.PLAY_MSG) {
//            play(0);
//        } else if (msg == AppConstant.PAUSE_MSG) {
//            pause();
//        } else if (msg == AppConstant.CONTINUE_MSG) { //继续播放
//            resume();
//        } else if (msg == AppConstant.PRIVIOUS_MSG) { //上一首
//            previous();
//        } else if (msg == AppConstant.NEXT_MSG) {     //下一首
//            next();
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    /**
     * 播放音乐
     *
     * @param position
     */
//    private void play(int position) {
//        try {
//            mediaPlayer.reset();//把各项参数恢复到初始状态
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare();  //进行缓冲
//            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 暂停音乐
     */
//    private void pause() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            isPause = true;
//        }
//    }

    /**
     * 继续音乐
     */
//    private void resume() {
//        if (isPause) {
//            mediaPlayer.start();
//            isPause = false;
//        }
//    }

    /**
     * 停止音乐
     */
//    private void stop() {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//        }
//    }
    /**
     * 上一首
     */
//    private void previous() {
//        play(0);
//    }
    /**
     * 下一首
     */
//    private void next() {
//        play(0);
//    }
    private class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int positon;
        public PreparedListener(int position) {
            this.positon = positon;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();    //开始播放
            if(positon > 0) {    //如果音乐不是从头播放
                mediaPlayer.seekTo(positon);
            }
        }
    }
}
