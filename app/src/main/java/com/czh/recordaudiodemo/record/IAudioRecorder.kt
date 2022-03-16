package com.czh.recordaudiodemo.record

/**
 * @Description: 录制器接口，新建录制器类时实现该接口
 * @Author: CZH
 * @Create: 2022/3/10
 */
interface IAudioRecorder {

    /**
     * 开始录制
     */
    fun startRecord(recordTime: Long)

    /**
     * 结束录制
     */
    fun stopRecord(saveAudioFile: Boolean)

    /**
     * 获取保存路径
     */
    fun getSavePath(): String
}