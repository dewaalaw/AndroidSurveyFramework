package com.askonthego.service;

import android.content.Context;
import android.media.MediaPlayer;

import com.askonthego.util.LogUtils;

public class AudioPlayerService {

    private MediaPlayer mediaPlayer;

    public void play(Context context, int audioResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        try {
            mediaPlayer = MediaPlayer.create(context, audioResourceId);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            LogUtils.e(getClass(), "Error playing audio", e);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                LogUtils.e(getClass(), "Error stopping the media player", e);
            }
        }
    }
}
