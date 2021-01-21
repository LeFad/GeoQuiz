package com.valeria.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "CheatActivity"
const val KEY_SHOWN = "shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.valeria.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.valeria.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var showAnswerBtn: Button
    private lateinit var textAnswer: TextView
    private lateinit var textVersion: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val answerIsShown = savedInstanceState?.getBoolean(KEY_SHOWN, false) ?: false
        quizViewModel.answerIsShown = answerIsShown

        quizViewModel.answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        showAnswerBtn = findViewById(R.id.show_answer_btn)
        textAnswer = findViewById(R.id.answer_text_view)
        textVersion = findViewById(R.id.version_text_view)

        textVersion.text = " API Level " + android.os.Build.VERSION.SDK_INT

        showAnswerBtn.setOnClickListener {
            quizViewModel.count -= 1
            quizViewModel.answerIsShown = true
            quizViewModel.textTV = when {
                quizViewModel.answerIsTrue -> R.string.true_btn.toString()
                else -> R.string.false_btn.toString()
            }
            textAnswer.setText(Integer.parseInt(quizViewModel.textTV))
            setAnswerShownResult(quizViewModel.answerIsShown)
        }
        setAnswerShownResult(quizViewModel.answerIsShown)
        when {
            quizViewModel.textTV == " " -> textAnswer.text = quizViewModel.textTV
            else -> textAnswer.setText(Integer.parseInt(quizViewModel.textTV))
        }
    }

    companion object {
        fun newIntent(context: Context, answerIsTrue: Boolean): Intent {
            return Intent(context, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        Log.d(TAG, "setAnswerShownResult - $isAnswerShown")

        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            Log.d(TAG, " putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown)")
        }
        setResult(Activity.RESULT_OK, data)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putBoolean(KEY_SHOWN, quizViewModel.answerIsShown)

    }

}