package com.lizheng.mymusic;

/**
 * Created by lizheng on 2017/12/5.
 * 歌曲类，存放歌曲的所有信息
 */

public class Mp3Info{
    private long id; //歌曲id
    private long album_id;//专辑id
    private String title; //歌曲名称
    private String artist; //歌手名称
    private long duration; //歌曲时长
    private long size; //歌曲大小
    private String url;//歌曲地址
    private String album;//专辑名称
    private int isMusic;
    private boolean isFavorite = false;

    public void setId(long id){
        this.id = id;
    }

    public long getId(){return this.id;}

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){return this.title;}

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getArtist(){return this.artist;}

    public void setDuration(long duration){this.duration = duration;}

    public long getDuration(){return this.duration;}

    public void setSize(long size){this.size = size;}

    public long getSize(){return this.size;}

    public void setUrl(String url){this.url = url;}

    public String getUrl(){return this.url;}

    public void setAlbum(String album){this.album = album;}

    public String getAlbum(){return this.album;}

    public void setAlbum_id(long album_id){this.album_id = album_id;}

    public long getAlbum_id(){return this.album_id;}

    public void setFavorite(boolean favorite){this.isFavorite =favorite;}

    public boolean getFavorite(){return isFavorite;}

}