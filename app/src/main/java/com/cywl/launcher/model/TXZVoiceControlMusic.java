package com.cywl.launcher.model;

import com.txznet.sdk.TXZMusicManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.music.TXZMusicTool;

public class TXZVoiceControlMusic {
    private void initMusic() {
        TXZMusicManager.getInstance().setMusicTool(musicTool);
    }

    private TXZMusicManager.MusicTool musicTool = new TXZMusicManager.MusicTool() {

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public void play() {
            toSpeakText("开始播放歌曲");
        }

        @Override
        public void continuePlay() {
            toSpeakText("继续播放歌曲");
        }

        @Override
        public void pause() {
            toSpeakText("暂停播放歌曲");
        }

        @Override
        public void exit() {
            toSpeakText("退出音乐");
        }

        @Override
        public void next() {
            toSpeakText("播放下一首");
        }

        @Override
        public void prev() {
            toSpeakText("播放上一首");
        }

        @Override
        public void switchModeLoopAll() {
            toSpeakText("全部循环模式");
        }

        @Override
        public void switchModeLoopOne() {
            toSpeakText("单曲循环模式");
        }

        @Override
        public void switchModeRandom() {
            toSpeakText("随机播放模式");
        }

        @Override
        public void switchSong() {
            toSpeakText("切换音乐");
        }


        @Override
        public void playRandom() {
            toSpeakText("随便听听");
        }

        @Override
        public void playMusic(TXZMusicManager.MusicModel musicModel) {
            String title = musicModel.getTitle();
            String album = musicModel.getAlbum();
            String[] artist = musicModel.getArtist();
            String[] keywords = musicModel.getKeywords();
            TXZMusicTool.getInstance().playMusic(musicModel);
            toSpeakText("搜索标题是" + title + "专辑名称是" + album + "歌手是" + artist[0]);
        }

        @Override
        public TXZMusicManager.MusicModel getCurrentMusicModel() {
            return null;
        }

        @Override
        public void favourMusic() {
            toSpeakText("播放收藏歌曲");
        }

        @Override
        public void unfavourMusic() {
            toSpeakText("取消收藏当前音乐");
        }

        @Override
        public void playFavourMusic() {

        }

        @Override
        public void setStatusListener(TXZMusicManager.MusicToolStatusListener musicToolStatusListener) {

        }
    };

    private void toSpeakText(String s) {
        TXZTtsManager.getInstance().speakText(s);
    }
}
