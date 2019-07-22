package com.example.android.guesstheword.screens.game

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.guesstheword.awaitNextValue
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

import org.junit.Rule

class GameViewModelTest {

    // Executes each task synchronously using Architecture Components.
    // Required for LiveData to work
    @get:Rule
    val rule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var gameViewModel: GameViewModel


    @Before
    fun setupViewModel() {
        // Create class under test
        gameViewModel = GameViewModel()
    }

    @Test
    fun newViewModel_properInitialization() {
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(0))
        assertThat(gameViewModel.eventBuzz.awaitNextValue(), `is`(GameViewModel.BuzzType.NO_BUZZ))
    }


    @Test
    fun skipOnce_scoreNegativeOne() {
        // arrange


        // act
        gameViewModel.onSkip()

        // assert
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(-1))

    }

    @Test
    fun skipHundred_scoreNegativeHundred() {
        // arrange


        // act
        for (x in 0 until 100) gameViewModel.onSkip()

        // assert
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(-100))

    }


    @Test
    fun correctOnce_scorePositiveOne() {
        // act
        gameViewModel.onCorrect()

        // assert
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(1))
        assertThat(gameViewModel.eventBuzz.awaitNextValue(), `is`(GameViewModel.BuzzType.CORRECT))
    }


    @Test
    fun correctHundred_scoreHundred() {
        // arrange


        // act
        for (x in 0 until 100) gameViewModel.onCorrect()

        // assert
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(100))

    }

    @Test
    fun correctAndSkip_scoreUpdated() {
        // arrange


        // act
        for (x in 0 until 2) gameViewModel.onSkip()
        for (x in 0 until 1) gameViewModel.onCorrect()
        for (x in 0 until 3) gameViewModel.onSkip()

        // assert
        assertThat(gameViewModel.word.awaitNextValue(), `is`(not(nullValue())))
        assertThat(gameViewModel.score.awaitNextValue(), `is`(-4))

    }


    @Test
    fun timerTickPlentyTime_noBuzz() {
        // act
        gameViewModel.tick(GameViewModel.COUNTDOWN_TIME)

        // assert
//        assertThat(gameViewModel.currentTime.awaitNextValue(), `is`(60L))
//        // Question about this here: https://stackoverflow.com/questions/51810330/testing-livedata-transformations
//        assertThat(gameViewModel.currentTimeString.awaitNextValue(), `is`("60:00"))
        assertThat(gameViewModel.eventBuzz.awaitNextValue(), `is`(GameViewModel.BuzzType.NO_BUZZ))

    }


    @Test
    fun timerTickPanic_panicBuzz() {
        // act
        gameViewModel.tick(GameViewModel.COUNTDOWN_PANIC_SECONDS)

        // assert
//        assertThat(gameViewModel.currentTime.awaitNextValue(), `is`(60L))
//        // Question about this here: https://stackoverflow.com/questions/51810330/testing-livedata-transformations
//        Thread.sleep(1000)
//        assertThat(gameViewModel.currentTimeString.awaitNextValue(), `is`("60:00"))
        assertThat(gameViewModel.eventBuzz.awaitNextValue(), `is`(GameViewModel.BuzzType.COUNTDOWN_PANIC))

    }


    @Test
    fun timerFinish_finishEventTriggered() {
        // act
        gameViewModel.finish()

        // assert
        assertThat(gameViewModel.eventBuzz.awaitNextValue(), `is`(GameViewModel.BuzzType.GAME_OVER))
        assertThat(gameViewModel.eventGameFinish.awaitNextValue(), `is`(true))

    }
}