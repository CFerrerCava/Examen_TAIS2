package com.example.loginmaterialg1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginmaterialg1.exam1.unidad1Activity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btninicio.setOnClickListener {
            startActivity(Intent(this, unidad1Activity::class.java))
        }
    }
}