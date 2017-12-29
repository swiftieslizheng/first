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
    private TextView tv_name;
    private int currentTime;    //当前歌曲播放时间
    private int duration;       //歌曲长度
    private boolean isPlaying;              // 正在播放
    private boolean isPause;                // 暂停
    private TextView tv_person;
    private TextView start_time;
    private TextView sum_time;
    private Button play_button;
    private Button previous_button;
    private Button next_button;
    private Button speed_low;            //0.8的倍速
    private Button speed_high;          //1.2的倍速
    private Button speed_normal;       //正常倍速
    private int listPosition = 0;   //标识列表位置
    public final static int HANDER_UPDATE = 1;
    private  SeekBar seekBar;       //进度条
    private Timer mTimer;          //计时器timer
    public  Handler handler;

/*
*动态设置权限，安卓6。0开始把部分权限设置为更高级的权限，需要动态设置
 */
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
        if (!LibsChecker.checkVitamioLibs(this))      //检查是否开始fffmeg解码
            return;
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();   //隐藏标题栏
        if (actionbar != null) {
            actionbar.hide();
        }
        mediaPlayer = new MediaPlayer(this);        //new一个mediaplayer
        initfindViewById();                         //把所有布局创建整合到这个方法里

        play_button.setOnClickListener(this);
        previous_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        speed_low.setOnClickListener(this);
        speed_high.setOnClickListener(this);
        speed_normal.setOnClickListener(this);
       // 动态设置权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            mp3Infos = SongsUtil.getMp3Infos(this);
            musicAdapter = new MusicAdapter(this, mp3Infos);
            listView.setAdapter(musicAdapter);
            listView.setOnItemClickListener(new MyOnItemClickListener());
            mTimer = new Timer();
            //每1000ms调用一次
            mTimer.schedule(mTimerTask,0,1000);
            //设置进度条seekbar的监听
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(isPlaying && fromUser){   //是否进行播放且是否是用户操作
                        mediaPlayer.seekTo(progress);   //在指定位置播放
                        mediaPlayer.pause();          //拖动时暂停播放
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.start();  //结束拖动时开始播放
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
    //销毁操作
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    //按钮点击事件的监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (isPlaying) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition); //获取当前歌曲的位置
                    play_button.setBackgroundResource(R.drawable.play_selector); //更改图标
                    pause(); //暂停的方法（整合）
                    isPlaying = false;
                    isPause = true;
                } else if (isPause) {
                    Mp3Info mp3Info = mp3Infos.get(listPosition);//获取当前歌曲的位置
                    play_button.setBackgroundResource(R.drawable.pause_selector);//更改图标
                    resume();//继续播放方法（整合）
                    isPlaying = true;
                    isPause = false;
                }
                break;
            case R.id.previous:
                isPlaying = true;
                isPause = false;
                listPosition = listPosition - 1; //获取上一首歌曲的位置
                if (listPosition >= 0) { //如果存在
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    tv_name.setText(mp3Info.getTitle());
                    tv_person.setText(mp3Info.getArtist());
                    sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));
                    previous(mp3Info.getUrl()); //上一首歌曲方法（整合）
                } else {//如果不存在
                    Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show();
                    listPosition = listPosition +1;//返回当前歌曲位置
                }
                break;
            case R.id.next:
                listPosition = listPosition + 1;//获取下一首歌曲的位置
                if (listPosition <= mp3Infos.size() - 1) {//如果存在
                    Mp3Info mp3Info = mp3Infos.get(listPosition);
                    tv_name.setText(mp3Info.getTitle());
                    tv_person.setText(mp3Info.getArtist());
                    sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));
                    next(mp3Info.getUrl());//下一首歌曲方法（整合）
                } else {//如果不存在
                    Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
                    listPosition = listPosition -1;//返回当前歌曲位置
                }
                break;
            case R.id.speed_low:
                Mp3Info mp3Info = mp3Infos.get(listPosition);//获取当前歌曲的位置
                mediaPlayer.setPlaybackSpeed(0.8f);//调整为0。8倍速

                break;
            case R.id.speed_high:
                mediaPlayer.setPlaybackSpeed(1.2f);//调整为1。2倍速
                break;
            case R.id.speed_normal:
                mediaPlayer.setPlaybackSpeed(1.0f);//调整为1。0倍速
                break;
        }
    }

    //listview里item的监听

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mp3Infos != null) {
                Mp3Info mp3Info = mp3Infos.get(position);
                listPosition = position;                            //获取当前歌曲的位置
                play(position,mp3Info.getUrl());                  //播放方法（整合）
                play_button.setBackgroundResource(R.drawable.pause_selector);
                tv_name.setText(mp3Info.getTitle());    //设置底部歌曲名称
                tv_person.setText(mp3Info.getArtist());  //设置底部歌曲的歌手信息
                sum_time.setText(SongsUtil.formatTime(mp3Info.getDuration()));//设置底部歌曲时长
                isPlaying = true;
                isPause = false;
            }
        }
    }
//播放歌曲
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
    //暂停歌曲
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }
    //继续播放歌曲
    public void resume() {
        if (isPause) {
            mediaPlayer.start();
            isPause = false;
        }
    }
    //停止歌曲
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

//计时器工具
private TimerTask mTimerTask = new TimerTask() {
    @Override
    public void run() {
        if(mediaPlayer == null)
            return;
        if(mediaPlayer.isPlaying()) {
            Message msg = Message.obtain(); //发送消息
            msg.what = HANDER_UPDATE;
            mHandler.sendMessage(msg); //hanler接收消息
        }
    }
};
// handler处理message
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mediaPlayer == null){
                return;
            }
            switch (msg.what){
                case HANDER_UPDATE:
                    long position = mediaPlayer.getCurrentPosition(); //得到当前歌曲进度
                    long total = mediaPlayer.getDuration(); //得到当前歌曲总长
                    seekBar.setMax((int)total); //设置进度条的总长
            seekBar.setProgress((int)position); //设置进度条的位置
            start_time.setText(SongsUtil.formatTime(position));//设置实时歌曲时间
                    break;
                default:
                    break;
            }


        }
    };
    //播放前调用重写的PreparedListener方法
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
