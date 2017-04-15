package com.tsunami.timeapp.circle.widgets.videolist.model;

import android.media.MediaPlayer;

import com.tsunami.timeapp.circle.widgets.videolist.widget.TextureVideoView;


/**
 * @author jilihao
 */
public interface VideoLoadMvpView {

    TextureVideoView getVideoView();

    void videoBeginning();

    void videoStopped();

    void videoPrepared(MediaPlayer player);

    void videoResourceReady(String videoPath);
}
