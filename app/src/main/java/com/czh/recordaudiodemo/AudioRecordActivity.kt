package com.czh.recordaudiodemo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.czh.recordaudiodemo.player.AudioPlayCallback
import com.czh.recordaudiodemo.player.SimpleAudioPlayer
import com.czh.recordaudiodemo.record.AudioRecordCallback
import com.czh.recordaudiodemo.record.AudioRecordResult
import com.czh.recordaudiodemo.record.AudioRecordResultType
import com.czh.recordaudiodemo.record.SimpleAudioRecorder
import com.czh.recordaudiodemo.record.view.WaveView
import java.io.File

class AudioRecordActivity : AppCompatActivity() {

    companion object {
        private const val ONE_MINUTE = 60 * 1000L
        private const val REQUEST_CODE_RECORD_AUDIO = 1001
    }

    private lateinit var motionLayout: MotionLayout
    private lateinit var tvCountdown: TextView
    private lateinit var btnRecord: Button
    private lateinit var btnListen: Button
    private lateinit var btnFinish: Button
    private lateinit var waveView: WaveView

    private var mRecording: Boolean = false
    private var mPlaying: Boolean = false
    private var mAudioFile: File? = null

    private val mSimpleAudioRecorder by lazy {
        SimpleAudioRecorder(this.applicationContext,
            object : AudioRecordCallback {
                override fun start() {
                    mRecording = true
                    toStartUI()
                }

                override fun onTick(untilFinished: Int, decibel: Int) {
                    updateTickUI(untilFinished, decibel)
                }

                override fun stop(audioRecordResult: AudioRecordResult) {
                    mRecording = false
                    mAudioFile = audioRecordResult.audioFile
                    toStopUI(audioRecordResult)
                }
            })
    }

    private val mSimpleAudioPlayer by lazy {
        SimpleAudioPlayer(object : AudioPlayCallback {
            override fun startPlay() {
                mPlaying = true
                btnListen.text = "停止"
            }

            override fun pausePlay() {}

            override fun stopPlay() {
                mPlaying = false
                btnListen.text = "试听"
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)

        motionLayout = findViewById(R.id.motion_layout)
        tvCountdown = findViewById(R.id.tv_countdown)
        waveView = findViewById(R.id.wave)
        btnRecord = findViewById(R.id.btn_record)
        btnListen = findViewById(R.id.btn_listen)
        btnFinish = findViewById(R.id.btn_finish)

        btnRecord.setOnClickListener {
            if (mRecording) {
                mSimpleAudioRecorder.stopRecord(true)
            } else {
                mSimpleAudioRecorder.startRecord(ONE_MINUTE)
            }
        }
        btnListen.setOnClickListener {
            if (mPlaying) {
                mSimpleAudioPlayer.stopPlay()
            } else {
                checkNotNull(mAudioFile) {
                    "音频文件不存在！"
                }
                mSimpleAudioPlayer.startPlay(mAudioFile!!.absolutePath)
            }
        }
        btnFinish.setOnClickListener {
            finish()
        }

        requestAudioPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        mSimpleAudioRecorder.stopRecord(false)
        mSimpleAudioPlayer.release()
    }

    private fun toStartUI() {
        waveView.reset()
        motionLayout.transitionToStart()
        btnListen.isEnabled = false
        btnFinish.isEnabled = false
        tvCountdown.setTextColor(Color.GRAY)
        btnRecord.text = "结束"
    }

    private fun updateTickUI(untilFinished: Int, decibel: Int) {
        tvCountdown.text = "录音将在：$untilFinished s 后结束"
        waveView.updateValue(decibel)
    }

    private fun toStopUI(audioRecordResult: AudioRecordResult) {
        waveView.reset()
        btnRecord.text = "重录"
        when (audioRecordResult.audioRecordResultType) {
            AudioRecordResultType.SUCCESS -> {
                motionLayout.transitionToEnd()
                tvCountdown.text = "录音成功"
                tvCountdown.setTextColor(Color.GREEN)
                btnListen.isEnabled = true
                btnFinish.isEnabled = true
            }
            AudioRecordResultType.LACK_PERMISSION -> {
                tvCountdown.text = "缺少录音权限"
                tvCountdown.setTextColor(Color.RED)
            }
            AudioRecordResultType.LACK_DURATION -> {
                tvCountdown.text = "录音时间过短"
                tvCountdown.setTextColor(Color.RED)
            }
            AudioRecordResultType.ERROR -> {
                tvCountdown.text = "录音失败"
                tvCountdown.setTextColor(Color.RED)
            }
        }
    }

    private fun requestAudioPermission(): Boolean {
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasAudioPermission) {
            return true
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE_RECORD_AUDIO
        )
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_RECORD_AUDIO -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请授予应用程序录制音频所需要的录音权限", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}