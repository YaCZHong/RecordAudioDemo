package com.czh.recordaudiodemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var mBtnAudioRecord: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnAudioRecord = findViewById(R.id.btn_audio_record)
        mBtnAudioRecord.setOnClickListener {
            Intent(this, AudioRecordActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}