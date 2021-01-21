package com.valeria.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders

private const val TAG_ = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_CHEATER = "cheater"
private const val KEY_COUNT = "cheat"
private const val KEY_TRUE_ANSWER = "trueAnswer"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueBtn: Button
    private lateinit var falseBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var cheatBtn: Button
    private lateinit var againBtn: Button
    private lateinit var questionTv: TextView
    private lateinit var quantityTv: TextView
    private lateinit var resultTv: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val isCheater = savedInstanceState?.getBoolean(KEY_CHEATER, false) ?: false
        quizViewModel.isCheater = isCheater

        val count = savedInstanceState?.getInt(KEY_COUNT, 0) ?: 0
        quizViewModel.count = count

        val trueAnswer = savedInstanceState?.getInt(KEY_TRUE_ANSWER, 0) ?: 0
        quizViewModel.trueAnswer = trueAnswer


        trueBtn = findViewById(R.id.btn_true)
        falseBtn = findViewById(R.id.btn_false)
        nextBtn = findViewById(R.id.btn_next)
        cheatBtn = findViewById(R.id.btn_cheat)
        againBtn = findViewById(R.id.btn_try_again)
        questionTv = findViewById(R.id.tv)
        quantityTv = findViewById(R.id.TV_quantity)
        resultTv = findViewById(R.id.TV_result)

        againBtn.setOnClickListener {
            quizViewModel.trueAnswer = 0
            quizViewModel.moveToNext()
            updateQuestion()
        }
        trueBtn.setOnClickListener {
            quizViewModel.wasClicked = true
            wasClicked(quizViewModel.wasClicked)
            checkAnswer(true)
        }
        falseBtn.setOnClickListener {
            quizViewModel.wasClicked = true
            wasClicked(quizViewModel.wasClicked)
            checkAnswer(false)
        }
        nextBtn.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatBtn.setOnClickListener { view ->
            ++quizViewModel.count
            quantityTv.text =
                (3 - quizViewModel.count).toString() + " " + resources.getString(R.string.attempt)
            when {
                quizViewModel.count >= 3 -> cheatBtn.isEnabled = false
            }

            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this, answerIsTrue)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                TODO("VERSION.SDK_INT < M")
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        updateQuestion()
        wasClicked(quizViewModel.wasClicked)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG_, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG_, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG_, "onPause called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG_, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putBoolean(KEY_CHEATER, quizViewModel.isCheater)
        savedInstanceState.putInt(KEY_COUNT, quizViewModel.count)
        savedInstanceState.putInt(KEY_TRUE_ANSWER, quizViewModel.trueAnswer)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG_, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG_, "onDestroy called")
    }

    private fun wasClicked(btnClicked: Boolean) {
        if (btnClicked) {
            trueBtn.isEnabled = false
            falseBtn.isEnabled = false
            quizViewModel.wasClicked = true
        } else {
            quizViewModel.wasClicked = false
        }
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTv.setText(questionTextResId)

        Log.i(TAG_, "trueAnswer " + quizViewModel.trueAnswer.toString())

        if (quizViewModel.currentIndex == quizViewModel.sizeQuestionBank - 1) {
            resultTv.text =
                " ${quizViewModel.trueAnswer} correct answers \n ${quizViewModel.markPer}% correct result"

            quizViewModel.count = 0
            cheatBtn.isEnabled = true

            trueBtn.visibility = View.GONE
            falseBtn.visibility = View.GONE
            cheatBtn.visibility = View.GONE
            nextBtn.visibility = View.GONE
            quantityTv.visibility = View.GONE
            againBtn.visibility = View.VISIBLE
            resultTv.visibility = View.VISIBLE

        } else {
            trueBtn.visibility = View.VISIBLE
            falseBtn.visibility = View.VISIBLE
            cheatBtn.visibility = View.VISIBLE
            nextBtn.visibility = View.VISIBLE
            quantityTv.visibility = View.VISIBLE
            againBtn.visibility = View.GONE
            resultTv.visibility = View.GONE

            quantityTv.text =
                (3 - quizViewModel.count).toString() + " " + resources.getString(R.string.attempt)

            when {
                quizViewModel.count >= 3 -> cheatBtn.isEnabled = false
            }

            trueBtn.isEnabled = true
            falseBtn.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> {
                ++quizViewModel.trueAnswer
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


}