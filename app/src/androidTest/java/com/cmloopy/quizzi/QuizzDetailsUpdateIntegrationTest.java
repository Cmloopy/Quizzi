package com.cmloopy.quizzi;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.views.QuizzDetails;
import com.cmloopy.quizzi.views.UpdateQuizActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class QuizzDetailsUpdateIntegrationTest {

    @Rule
    public ActivityTestRule<QuizzDetails> quizzDetailsRule =
            new ActivityTestRule<QuizzDetails>(QuizzDetails.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("quizId", 94L);
                    intent.putExtra("userId", 194);
                    return intent;
                }
            };

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEditButton_OpensUpdateActivity() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btnEdit))
                .perform(click());

        onView(withText("Edit Quiz"))
                .perform(click());

        Intents.intended(IntentMatchers.hasComponent(
                UpdateQuizActivity.class.getName()));
    }

    @Test
    public void testUpdateResult_QuizUpdated_RefreshesData() {
        Intent resultData = new Intent();
        resultData.putExtra("quiz_updated", true);

        onView(withId(R.id.quizTitle))
                .check(matches(isDisplayed()));

        onView(withId(R.id.quizDescriptionDetail))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteQuiz_ConfirmationDialog() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btnEdit))
                .perform(click());

        onView(withText("Delete Quiz"))
                .perform(click());

        onView(withText("Delete Quiz"))
                .check(matches(isDisplayed()));

        onView(withText("Are you sure you want to delete this quiz?"))
                .check(matches(isDisplayed()));

        onView(withText("Cancel"))
                .perform(click());

        onView(withId(R.id.quizTitle))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testShareQuiz_OpensShareIntent() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btnEdit))
                .perform(click());

        onView(withText("Share Quiz"))
                .perform(click());

        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_SEND));
    }

    @Test
    public void testGenerateQRCode_NavigatesToQRActivity() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btnEdit))
                .perform(click());

        onView(withText("Generate QR Code"))
                .perform(click());

        Intents.intended(IntentMatchers.hasComponent(
                "com.cmloopy.quizzi.views.UI_40_generate_qr"));
    }
}