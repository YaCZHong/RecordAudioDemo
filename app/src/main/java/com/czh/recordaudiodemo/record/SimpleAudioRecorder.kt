package com.czh.recordaudiodemo.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.math.log10

/**
 * @Description: 录制器的简单示例
 * @Author: CZH
 * @Create: 2022/3/10
 */
class SimpleAudioRecorder(
    private val mContext: Context,
    private val mCallback: AudioRecordCallback
) : IAudioRecorder {

    private var mMediaRecorder: MediaRecorder? = null
    private var mAudioFile: File? = null
    private var mCountDownTimer: CountDownTimer? = null

    override fun startRecord(recordTime: Long) {
        if (!hasRecordAudioPermission()) {
            mCallback.stop(AudioRecordResult_LackPermission)
            return
        }
        startRec(recordTime)
    }

    override fun stopRecord(saveAudioFile: Boolean) {
        stopRec(saveAudioFile)
    }

    override fun getSavePath(): String {
        return (mContext.applicationContext.externalCacheDir?.absolutePath
            ?: mContext.applicationContext.cacheDir.absolutePath) + File.separator + "audio" + File.separator
    }

    private fun startRec(recordTime: Long) {
        mCountDownTimer = createCountDownTimer(recordTime, onTick = {
            val decibel = calculateDecibel(mMediaRecorder?.maxAmplitude ?: 0)
            mCallback.onTick((it / 1000).toInt(), decibel)
        }, onFinish = {
            stopRecord(true)
        })
        try {
            mMediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC) //声音来源
                setAudioSamplingRate(44100) // 采样频率
                setAudioEncodingBitRate(96000) // 编码比特率
                setAudioChannels(1) //1 单通道(mono) or 2 双通道(stereo)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // 输出文件格式
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC) // 编码格式
                } else {
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                }
                setOutputFile(createAudioFile().also { mAudioFile = it }.absolutePath)
                prepare()
                start()
            }
            mCallback.start()
            mCountDownTimer!!.start()
        } catch (e: Exception) {
            mMediaRecorder?.release()
            mMediaRecorder = null
            if (mAudioFile?.exists() == true) {
                mAudioFile?.delete()
                mAudioFile = null
            }
            mCountDownTimer?.cancel()
            mCountDownTimer = null
            mCallback.stop(AudioRecordResult_Error)
            e.printStackTrace()
        }
    }

    private fun stopRec(saveAudioFile: Boolean) {
        try {
            mMediaRecorder?.stop()
            mCountDownTimer?.cancel()
            mCountDownTimer = null
            if (mAudioFile == null) {
                mCallback.stop(AudioRecordResult_Error)
                return
            }
            if (!saveAudioFile) {
                mAudioFile!!.delete()
                mAudioFile = null
                mCallback.stop(AudioRecordResult_SuccessButUnSave)
                return
            }
            if (!checkAudioDuration(mAudioFile!!.absolutePath)) {
                mAudioFile!!.delete()
                mAudioFile = null
                mCallback.stop(AudioRecordResult_LackDuration)
            } else {
                mCallback.stop(AudioRecordResult(AudioRecordResultType.SUCCESS, mAudioFile))
                mAudioFile = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mMediaRecorder?.release()
            mMediaRecorder = null
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext.applicationContext,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createAudioFile(): File {
        val audioCacheDir = File(getSavePath())
        if (!audioCacheDir.exists()) {
            audioCacheDir.mkdir()
        }
        return File(audioCacheDir, "${System.currentTimeMillis()}.aac")
    }

    private fun createCountDownTimer(
        totalTime: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(totalTime + 100, 16) {
            override fun onTick(millisUntilFinished: Long) {
                onTick.invoke(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish.invoke()
            }
        }
    }

    private fun checkAudioDuration(audioFilePath: String): Boolean {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(audioFilePath)
        val duration =
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong()
        mmr.release()
        return duration >= 3000L
    }

    private fun calculateDecibel(amplitude: Int): Int {
        val baseAmplitude = 1
        val ratio = amplitude.toFloat() / baseAmplitude
        if (ratio > 1) {
            return (log10(ratio) * 20).toInt()
        }
        return 0
    }
}