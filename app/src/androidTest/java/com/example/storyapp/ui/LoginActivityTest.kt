package com.example.storyapp.ui

import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.storyapp.R
import com.example.storyapp.utils.EspressoIdlingResource
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginFunction() {
        val emailCredential = "testing101@gmail.com"
        val passwordCredential = "test12345"

        onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(withId(R.id.ed_login_email)),
                ViewMatchers.withClassName(equalTo(EditText::class.java.name)),
            )
        ).perform(typeText(emailCredential))
        onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(withId(R.id.ed_login_email)),
                ViewMatchers.withClassName(equalTo(EditText::class.java.name)),
            )
        ).perform(typeText(passwordCredential))
        onView(withId(R.id.btn_login)).perform(click())

        EspressoIdlingResource.increment()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.loading_indicator)).check(matches(not(isDisplayed())))

        EspressoIdlingResource.decrement()

        intended(hasComponent(HomeActivity::class.java.name))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

}