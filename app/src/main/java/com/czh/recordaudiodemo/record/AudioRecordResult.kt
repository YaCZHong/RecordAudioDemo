package com.czh.recordaudiodemo.record

import java.io.File

/**
 * 录音结果包装类
 */
data class AudioRecordResult(
    val audioRecordResultType: AudioRecordResultType,
    val audioFile: File? = null
)

/**
 * 录音结果类型
 */
enum class AudioRecordResultType {
    SUCCESS, SUCCESS_BUT_UN_SAVE, LACK_PERMISSION, LACK_DURATION, ERROR
}

/**
 * 预设值：录制成功但不做保存
 */
val AudioRecordResult_SuccessButUnSave =
    AudioRecordResult(AudioRecordResultType.SUCCESS_BUT_UN_SAVE)

/**
 * 预设值：缺乏录音权限
 */
val AudioRecordResult_LackPermission = AudioRecordResult(AudioRecordResultType.LACK_PERMISSION)

/**
 * 预设值：录制时长过短
 */
val AudioRecordResult_LackDuration = AudioRecordResult(AudioRecordResultType.LACK_DURATION)

/**
 * 预设值：未知录制出错（如麦克风被占用）
 */
val AudioRecordResult_Error = AudioRecordResult(AudioRecordResultType.ERROR)