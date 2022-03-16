package com.czh.recordaudiodemo.player

interface IAudioPlayer {
    fun startPlay(path: String)
    fun pausePlay()
    fun stopPlay()
    fun release()
}