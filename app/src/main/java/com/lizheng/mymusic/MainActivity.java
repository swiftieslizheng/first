package com.lizheng.mymusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lizheng.mymusic.adapter.MusicAdapter;
import com.lizheng.mymusic.util.SongsUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private ListView listView;
    private List<Mp3Info> mp3Infos;
    private MusicAdapter musicAdapter;
    private Mp3Info mp3Info;
    private TextView tv_name;
    private int currentTime;    //当前歌曲播放时间
    private int duration;       //歌曲长度
    private boolean isPlaying;              // 正在播放
    private boolean isPause;                // 暂停
//    private boolean lowpause;
//    private boolean highpause;
//    private boolean lowPlaying;
//    private boolean highPlaying;
    private TextView tv_person;
    private TextView start_time;
    private TextView sum_time;
    private Button play_button;
    private Button previous_button;
    private Button next_button;
    private Button speed_low;
    private Button speed_high;
    private Button speed_normal;
    private int listPosition = 0;   //标识列表位置
    public final static int HANDER_UPDATE = 1;
    private  SeekBar seekBar;
    private Timer mTimer;
    public  Handler handler;
//            = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle bundle =msg.getData();
//            int total = bundle.getInt("total");
//            int position = bundle.getInt("position");
//            seekBar.setMax(total);
//            seekBar.setProgress(position);
//        }
//    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mp3Infos = SongsUtil.getMp3Infos(this);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        mediaPlayer = new MediaPlayer(this);
        initfindViewById();
//        handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle bundle =msg.getData();
//            long total = bundle.getLong("total");
//            long position = bundle.getLong("position");
//            seekBar.setMax((int)total);
//            seekBar.setProgress((int)position);
//            start_time.setText(SongsUtil.formatTime(position));
//        }
//    };
        play_button.setOnClickListener(this);
        previous_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        speed_low.setOnClickListener(this);
        speed_high.setOnClickListener(this);
        speed_normal.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            mp3Infos = SongsUtil.getMp3Infos(this);
            musicAdapter = new MusicAdapter(this, mp3Infos);
            //  Intent intent = new Intent(this, PlayerService.class);
            //   bindService(intent, connection, BIND_AUTO_CREATE);
            //   isBound= bindService(intent, connection, BIND_AUTO_CREATE);
            listView.setAdapter(musicAdapter);
            listView.setOnItemClickListener(new MyOnItemClickListener());
            mTimer = new Timer();
            mTimer.schedule(mTimerTask,0,1000);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(isPlaying && fromUser){
                        mediaPlayer.seekTo(progress);
                        mediaPlayer.pause();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.start();
                }
            });

        }
    }


    private void initfindViewById() {
        listView = (ListView) findViewById(R.id.listview);
        tv_name = (TextView) findViewById(R.id.tv_song_name);
        tv_person = (TextView) findViewById(R.id.tv_song_person);
        play_button = (Button) findViewById(R.id.play);
        next_button = (Button) findViewById(R.id.next);
        previous_button = (Button) findViewById(R.id.previous);
        speed_low = (Button) findViewById(R.id.speed_low);
        speed_high = (Button) findViewById(R.id.speed_high);
        speed_normal = (Button) findViewById(R.id.speed_normal);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        start_time = (TextView) findViewById(R.id.start_time);
        sum_time = (TextView) findViewById(R.id.sum_time);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isBound) {
//            unbindService(connection);// 解绑服务
//            isBound = false;
//        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.play:
                if (isPlaying) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    play_button.setBackgroundResource(R.drawable.play_selector);
//                    intent.setClass(this, PlayerService.class);
//                    intent.putExtra("url", mp3Info.getUrl());
//                    intent.putExtra("MSG", AppConstant.PAUSE_MSG);
//                    startService(intent);
//                    musicBinder.pause();
                    pause();
                    isPlaying = false;
                    isPause = true;
                } else if (isPause) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    play_button.setBackgroundResource(R.drawable.pause_selector);
//                    intent.setClass(this, PlayerService.class);
//                    intent.putExtra("url", mp3Info.getUrl());
//                    intent.putExtra("MSG", AppConstant.CONTINUE_MSG);
//                    startService(intent);
                    //                   musicBinder.resume();
                    resume();
                    isPlaying = true;
                    isPause = false;
                }
                break;
            case R.id.previous:
                isPlaying = true;
                isPause = false;
                listPosition = listPosition - 1;
                if (listPosition >= 0) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    tv_name.setText(mp3Info.getTitle());
                    tv_person.setText(mp3Info.getArtist());
                    sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));
//                    intent.setClass(this, PlayerService.class);
//                    intent.putExtra("listPosition", listPosition);
//                    intent.putExtra("url", mp3Info.getUrl());
//                    intent.putExtra("MSG", AppConstant.PRIVIOUS_MSG);
//                    startService(intent);
//                    musicBinder.previous(mp3Info.getUrl());
//                    seekBar.setVisibility(VISIBLE);
//                    start_time.setVisibility(VISIBLE);
                    previous(mp3Info.getUrl());
                } else {
                    Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show();
                    listPosition = listPosition +1;
                }
                break;
            case R.id.next:
                listPosition = listPosition + 1;
                if (listPosition <= mp3Infos.size() - 1) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    tv_name.setText(mp3Info.getTitle());
                    tv_person.setText(mp3Info.getArtist());
                    sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));
//                    intent.setClass(this, PlayerService.class);
//                    intent.putExtra("listPosition", listPosition);
//                    intent.putExtra("url", mp3Info.getUrl());
//                    intent.putExtra("MSG", AppConstant.NEXT_MSG);
//                    startService(intent);
//                    musicBinder.next(mp3Info.getUrl());
//                    seekBar.setVisibility(VISIBLE);
//                    start_time.setVisibility(VISIBLE);
                    next(mp3Info.getUrl());
                } else {
                    Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
                    listPosition = listPosition -1;
                }
                break;
            case R.id.speed_low:
                Mp3Info mp3Info = mp3Infos.get(listPosition);
                mediaPlayer.setPlaybackSpeed(0.8f);

                break;
            case R.id.speed_high:
                mediaPlayer.setPlaybackSpeed(1.2f);
                break;
            case R.id.speed_normal:
                mediaPlayer.setPlaybackSpeed(1.0f);
                break;
        }
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mp3Infos != null) {
                Mp3Info mp3Info = mp3Infos.get(position);
                listPosition = position;
//                Intent intent = new Intent();
//                intent.putExtra("url", mp3Info.getUrl());
//                intent.putExtra("MSG", AppConstant.PLAY_MSG);
//                intent.setClass(MainActivity.this, PlayerService.class);
//                startService(intent);
//                musicBinder.play(0,mp3Info.getUrl());
//                seekBar.setVisibility(VISIBLE);
//                start_time.setVisibility(VISIBLE);
//                mediaPlayer.stop();
                play(position,mp3Info.getUrl());
                play_button.setBackgroundResource(R.drawable.pause_selector);
                tv_name.setText(mp3Info.getTitle());
                tv_person.setText(mp3Info.getArtist());
                sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));
                isPlaying = true;
                isPause = false;
            }
        }
    }

    public void play(int position, String path) {
        try {
            mediaPlayer.reset();//把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();  //进行缓冲
            mediaPlayer.setOnPreparedListener(new PreparedListener(position));//注册一个监听器
            //seekBarProgress();
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
        play_button.setBackgroundResource(R.drawable.pause_selector);
        isPlaying = true;
        isPause = false;
        play(0, path);
    }

    /**
     * 下一首
     */
    public void next(String path) {
        play_button.setBackgroundResource(R.drawable.pause_selector);
        isPlaying = true;
        isPause = false;
        play(0, path);
    }

//    public void seekBarProgress() {
//        final long total = mediaPlayer.getDuration();
//        final Timer timer = new Timer();
//        final TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                long position = mediaPlayer.getCurrentPosition();
//                Message ms = Message.obtain();
//                Bundle bundle=new Bundle();
//                bundle.putLong("total",total);
//                Log.i("seekbar","歌曲总长度"+total);
//                bundle.putLong("position",position);
//                Log.i("seekbar","歌曲"+position);
//                ms.setData(bundle);
//                handler.sendMessage(ms);
//            }
//        };
//        timer.schedule(task, 100, 200);
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                timer.cancel();
//                task.cancel();
//            }
//        });
//    }
private TimerTask mTimerTask = new TimerTask() {
    @Override
    public void run() {
        if(mediaPlayer == null)
            return;
        if(mediaPlayer.isPlaying()) {
            Message msg = Message.obtain();
            msg.what = HANDER_UPDATE;
            mHandler.sendMessage(msg);
        }
    }
};
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaPlayer == null){
                return;
            }
            switch (msg.what){
                case HANDER_UPDATE:
                    long position = mediaPlayer.getCurrentPosition();
                    long total = mediaPlayer.getDuration();
                    seekBar.setMax((int)total);
            seekBar.setProgress((int)position);
            start_time.setText(SongsUtil.formatTime(position));
                    break;
                default:
                    break;
            }


        }
    };
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
