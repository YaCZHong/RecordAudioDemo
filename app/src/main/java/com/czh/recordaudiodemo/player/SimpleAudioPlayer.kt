package com.czh.recordaudiodemo.player

import android.media.MediaPlayer
import android.util.Log

class SimpleAudioPlayer(private val mCallback: AudioPlayCallback) : IAudioPlayer {

    private var mMediaPlayer: MediaPlayer? = null

    init {
        MediaPlayer().also {
            it.setOnCompletionListener {
                stopPlay()
            }
            it.setOnPreparedListener { mp ->
                mp.start()
                mCallback.startPlay()
                Log.d("SimpleAudioPlayer", "startPlay")
            }
            mMediaPlayer = it
        }
    }

    override fun startPlay(path: String) {
        checkNotNull(mMediaPlayer) {
            "MediaPlayer未初始化或者已被回收释放"
        }
        mMediaPlayer!!.reset()
        mMediaPlayer!!.setDataSource(path)
        mMediaPlayer!!.prepareAsync()
    }

    override fun pausePlay() {
        checkNotNull(mMediaPlayer) {
            "MediaPlayer未初始化或者已被回收释放"
        }
        mMediaPlayer!!.pause()
        mCallback.pausePlay()
        Log.d("SimpleAudioPlayer", "pausePlay")
    }

    override fun stopPlay() {
        checkNotNull(mMediaPlayer) {
            "MediaPlayer未初始化或者已被回收释放"
        }
        mMediaPlayer!!.stop()
        mCallback.stopPlay()
        Log.d("SimpleAudioPlayer", "stopPlay")
    }

    override fun release() {
        mMediaPlayer?.release()
        mMediaPlayer = null
        Log.d("SimpleAudioPlayer", "release")
    }
}