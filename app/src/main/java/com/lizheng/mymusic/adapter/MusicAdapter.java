package com.lizheng.mymusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lizheng.mymusic.Mp3Info;
import com.lizheng.mymusic.R;
import com.lizheng.mymusic.util.SongsUtil;

import java.util.List;

import static com.lizheng.mymusic.util.SongsUtil.formatTime;

/**
 * Created by lizheng on 2017/12/5.
 */

public class MusicAdapter extends BaseAdapter {
    private Context context;
    private List<Mp3Info> mp3Infos;   //存放Mp3Info引用的集合
    private Mp3Info mp3Info;        //Mp3Info对象引用
    public MusicAdapter(Context context,List<Mp3Info> mp3Infos){
        this.context = context;
        this.mp3Infos = mp3Infos;
    }
    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.music_list_item_layout, null);
            holder.music_title = (TextView)convertView.findViewById(R.id.music_title);
            holder.music_artist = (TextView)convertView.findViewById(R.id.music_Artist);
            holder.music_duration = (TextView)convertView.findViewById(R.id.music_duration);
          //  holder.album_image = (ImageView) convertView.findViewById(R.id.album_image);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }
        mp3Info = mp3Infos.get(position);
        holder.music_title.setText(mp3Info.getTitle());         //显示标题
        holder.music_artist.setText(mp3Info.getArtist());       //显示艺术家
        holder.music_duration.setText(SongsUtil.formatTime(mp3Info.getDuration())); //显示长度
        //Bitmap bm = BitmapFactory.decodeFile(mp3Info.getAlbum());
       // BitmapDrawable bmDraw = new BitmapDrawable(bm);
        return convertView;
    }



    private class ViewHolder{
      //  public ImageView album_image;
        public TextView music_title;
        public TextView music_artist;
        public TextView music_duration;
    }
}