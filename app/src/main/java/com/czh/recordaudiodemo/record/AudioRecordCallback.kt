package com.czh.recordaudiodemo.record

/**
 * @Description: 录制时的回调接口
 * @Author: CZH
 * @Create: 2022/3/10
 */
interface AudioRecordCallback {

    /**
     * 开始录制的回调
     */
    fun start()

    /**
     * 倒计时回调
     * @param untilFinished 距离结束的时间
     * @param decibel 此刻的分贝值
     */
    fun onTick(untilFinished: Int, decibel: Int)

    /**
     * 结束录制
     * @param resultAudio 录制结果
     */
    fun stop(resultAudio: AudioRecordResult)
}