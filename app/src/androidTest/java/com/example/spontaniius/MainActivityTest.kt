package com.example.spontaniius

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainActivityTest{

    @get:Rule
    val activeityScenario = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun pressButton(){
        onView(withId(R.id.UwU)).perform(click())
    }
}