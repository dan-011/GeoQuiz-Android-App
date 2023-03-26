package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import android.content.Context

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    private var userScore = 0
    private var answeredQuestionsCount = 0

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)

    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val answeredAllQuestions: Boolean
        get() = answeredQuestionsCount == questionBank.size

    val currentScore: Int
        get() = if(isCheater){
            0
        }else{
            userScore
        }

    val questionWasAnswered: Boolean
        get() = questionBank[currentIndex].wasAnswered

    val numQuestions: Int
        get() = questionBank.size

    fun answerQuestion(answeredCorrectly: Boolean){
        if(answeredCorrectly && !questionWasAnswered && !isCheater){
            userScore++
        }
        questionBank[currentIndex].wasAnswered = true
        answeredQuestionsCount++
    }
    fun moveToNext(){
        do{
            currentIndex = (currentIndex + 1) % questionBank.size
        } while(questionWasAnswered && !answeredAllQuestions)
    }
}