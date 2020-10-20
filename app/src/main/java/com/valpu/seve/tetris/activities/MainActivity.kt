package com.valpu.seve.tetris.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.valpu.seve.tetris.R
import com.valpu.seve.tetris.storage.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var tvHighScore: TextView? = null
    private var appPreferences: AppPreferences? = null
    private var animTranslate: Animation? = null
    private var animRotate: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate)
        animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate)

        val btnNewGame = btn_new_game
        val btnResetScore = btn_reset_score
        val btnExit = btn_exit
        tvHighScore = tv_high_score

        appPreferences = AppPreferences(this)

        tvHighScore?.text = (getString(R.string.tv_main_layout_high_score, appPreferences?.getHighScore().toString()))

        btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        btnResetScore.setOnClickListener(this::onBtnResetScore)
        btnExit.setOnClickListener(this::onBtnExit)

    }

    override fun onRestart() {
        super.onRestart()
        tvHighScore?.text = (getString(R.string.tv_main_layout_high_score, appPreferences?.getHighScore().toString()))
    }

    private fun onBtnNewGameClick(view: View) {
        view.startAnimation(animTranslate)
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnResetScore(view: View) {
        view.startAnimation(animRotate)
        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        Snackbar.make(view, getString(R.string.main_snackbar_reset_score), Snackbar.LENGTH_SHORT).show()
        tvHighScore?.text = (getString(R.string.tv_main_layout_high_score, appPreferences?.getHighScore().toString()))
    }

    private fun onBtnExit(3view: View) {
        System.exit(0)
    }
}
