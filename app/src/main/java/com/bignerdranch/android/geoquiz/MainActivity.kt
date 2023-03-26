package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result
        if(result.resultCode == Activity.RESULT_OK) {
            quizViewModel.isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        binding.nextButton.setOnClickListener {
            nextQuestion()
        }
        binding.questionTextView.setOnClickListener{
            nextQuestion()
        }
        binding.cheatButton.setOnClickListener {
            // Start CheatActivity
            if(showToasts()) {
            }
            else{
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                cheatLauncher.launch(intent)
            }
        }
        updateQuestion()
    }

    private fun updateQuestion(){
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }
    private fun checkAnswer(userAnswer: Boolean){
        if(showToasts()){
            return
        }
        else{
            val correctAnswer = quizViewModel.currentQuestionAnswer
            quizViewModel.answerQuestion(userAnswer == correctAnswer)
            val messageResId = when {
                quizViewModel.isCheater -> R.string.judgement_toast
                userAnswer == correctAnswer -> R.string.correct_toast
                else -> R.string.incorrect_toast
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
            if(quizViewModel.answeredAllQuestions){
                showScore()
            }
        }
    }
    private fun nextQuestion(){
        if(quizViewModel.answeredAllQuestions){
            showScore()
        }
        else{
            quizViewModel.moveToNext()
            updateQuestion()
        }
    }
    private fun showScore() {
        val messageText = if(quizViewModel.isCheater){
                getString(R.string.cheat_score_toast)
            } else {
                getString(R.string.score_toast, quizViewModel.currentScore, quizViewModel.numQuestions)
            }
        Toast.makeText(this, messageText, Toast.LENGTH_LONG).show()
    }
    private fun moveToNextMessage(){
        Toast.makeText(this, R.string.move_to_next_toast, Toast.LENGTH_SHORT).show()
    }
    private fun showToasts(): Boolean {
        if(quizViewModel.answeredAllQuestions){
            showScore()
            return true
        }
        else if(quizViewModel.questionWasAnswered){
            moveToNextMessage()
            return true
        }
        else{
            return false
        }
    }
}