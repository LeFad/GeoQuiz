package com.valeria.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    private val questionBank = listOf(
            Question(R.string.question_australia, true),
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true),
            Question(R.string.finish, true),
            )

    var currentIndex = 0
    var isCheater=false
    var count = 0
    var trueAnswer=0
    var wasClicked = false

    var answerIsTrue = false
    var answerIsShown = false
    var textTV = " "

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val sizeQuestionBank: Int
        get() = questionBank.size
    val markPer: Int
        get() = (trueAnswer*100)/6

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
}