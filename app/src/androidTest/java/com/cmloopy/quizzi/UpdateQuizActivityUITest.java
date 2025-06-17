package com.cmloopy.quizzi;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.views.QuizzDetails;
import com.cmloopy.quizzi.views.UpdateQuizActivity;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static org.hamcrest.Matchers.allOf;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
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
public class UpdateQuizActivityUITest {

    private static final long TEST_DELAY_MS = 800; //  between actions
    private static final long LOADING_DELAY_MS = 1000; //  loading operations (increased)
    private static final long SHORT_DELAY_MS = 500; // seconds for quick actions
    private static final long EXTRA_LOADING_DELAY_MS = 2000; // for collections loading

    @Rule
    public ActivityTestRule<UpdateQuizActivity> activityTestRule =
            new ActivityTestRule<UpdateQuizActivity>(UpdateQuizActivity.class) {
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
        waitFor(LOADING_DELAY_MS);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testValidateForm_EmptyTitle_ShowsError() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.et_title))
                .perform(replaceText(""), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.spinner_collection))
                .perform(click());
        waitFor(SHORT_DELAY_MS);
        onData(anything())
                .atPosition(1) // Select first non-default collection
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.btn_save_quiz))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.et_title))
                .check(matches(hasErrorText("Please enter a title")));
    }

    @Test
    public void testValidateForm_NoCollectionSelected_ShowsToast() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.et_title))
                .perform(replaceText("Test Quiz Title"), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.spinner_collection))
                .perform(click());
        waitFor(SHORT_DELAY_MS);
        onData(anything())
                .atPosition(0)
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.btn_save_quiz))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.et_title))
                .check(matches(isDisplayed()));

        onView(withText("Please select a collection"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

//    @Test
//    public void testValidateForm_ValidForm_AllowsSubmission() {
//        waitFor(EXTRA_LOADING_DELAY_MS);
//
//        onView(withId(R.id.et_title))
//                .perform(replaceText("Test Quiz Title"), closeSoftKeyboard());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_desc_title))
//                .perform(replaceText("Test quiz description"), closeSoftKeyboard());
//        waitFor(TEST_DELAY_MS);
//
//        waitFor(EXTRA_LOADING_DELAY_MS);
//
//        boolean collectionSelected = false;
//        int attempts = 0;
//        int maxAttempts = 3;
//
//        while (!collectionSelected && attempts < maxAttempts) {
//            try {
//                onView(withId(R.id.spinner_collection))
//                        .perform(click());
//                waitFor(SHORT_DELAY_MS);
//
//                onData(anything())
//                        .atPosition(1)
//                        .perform(click());
//
//                collectionSelected = true;
//                waitFor(TEST_DELAY_MS);
//            } catch (Exception e) {
//                attempts++;
//                waitFor(EXTRA_LOADING_DELAY_MS);
//            }
//        }
//
//        onView(withId(R.id.spinner_visibility))
//                .perform(click());
//        waitFor(SHORT_DELAY_MS);
//        onData(anything())
//                .atPosition(0)
//                .perform(click());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.spinner_question_visibility))
//                .perform(click());
//        waitFor(SHORT_DELAY_MS);
//        onData(anything())
//                .atPosition(0)
//                .perform(click());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.btn_save_quiz))
//                .perform(click());
//        waitFor(LOADING_DELAY_MS);
//
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_title))
//                .check(matches(isDisplayed()));
//
//        try {
//            onView(withId(R.id.et_title))
//                    .check(matches(hasNoError()));
//        } catch (Exception e) {
//            onView(withId(R.id.et_title))
//                    .check(matches(isDisplayed()));
//        }
//    }

    public static Matcher<View> hasNoError() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has no error text");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }

                CharSequence error = ((EditText) view).getError();
                return error == null || error.length() == 0;
            }
        };
    }

    @Test
    public void testTitleCharacterLimit_ExceedsMaxLength() {
        waitFor(LOADING_DELAY_MS);

        String longTitle = "This is a very long title that exceeds the maximum character limit of fifty characters for testing purposes";

        onView(withId(R.id.et_title))
                .perform(replaceText(longTitle), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.et_title))
                .check(matches(withText(longTitle.substring(0, 50))));

        onView(withId(R.id.tv_title_counter))
                .check(matches(withText("50/50")));
    }

    @Test
    public void testDescriptionCharacterLimit_ExceedsMaxLength() {
        waitFor(LOADING_DELAY_MS);

        String longDescription = "This is a very long description that definitely exceeds the maximum character limit of one hundred and fifty characters for testing purposes and should be truncated";

        onView(withId(R.id.et_desc_title))
                .perform(replaceText(longDescription), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.et_desc_title))
                .check(matches(withText(longDescription.substring(0, 150))));

        onView(withId(R.id.tv_description_counter))
                .check(matches(withText("150/150")));
    }

//    @Test
//    public void testKeywordAddition_ValidKeyword() {
//        waitFor(LOADING_DELAY_MS);
//
//        onView(withId(R.id.et_keyword))
//                .perform(replaceText(""), closeSoftKeyboard());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_keyword))
//                .perform(typeText("testKeyword"));
//        waitFor(TEST_DELAY_MS);
//
//        try {
//            onView(withId(R.id.et_keyword))
//                    .perform(pressImeActionButton());
//        } catch (Exception e1) {
//            try {
//                onView(withId(R.id.et_keyword))
//                        .perform(pressKey(KeyEvent.KEYCODE_ENTER));
//            } catch (Exception e2) {
//                onView(withId(R.id.et_keyword))
//                        .perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
//            }
//        }
//
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_keyword))
//                .check(matches(withText("")));
//
//        onView(withId(R.id.et_keyword))
//                .perform(typeText("secondKeyword"));
//        waitFor(TEST_DELAY_MS);
//
//        try {
//            onView(withId(R.id.et_keyword))
//                    .perform(pressImeActionButton());
//        } catch (Exception e) {
//            onView(withId(R.id.et_keyword))
//                    .perform(pressKey(KeyEvent.KEYCODE_ENTER));
//        }
//
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_keyword))
//                .check(matches(withText("")));
//    }

    @Test
    public void testSpinnerSelections_VisibilitySpinner() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.spinner_visibility))
                .perform(click());
        waitFor(SHORT_DELAY_MS);
        onData(allOf(is(instanceOf(String.class)), is("Public")))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.spinner_visibility))
                .perform(click());
        waitFor(SHORT_DELAY_MS);
        onData(allOf(is(instanceOf(String.class)), is("Private")))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.spinner_visibility))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCloseButton_WithoutChanges_FinishesActivity() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.btn_close))
                .perform(click());
        waitFor(TEST_DELAY_MS);

    }

    @Test
    public void testCloseButton_WithChanges_ShowsConfirmationDialog() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.et_title))
                .perform(replaceText("Modified Title"), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.btn_close))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withText("Discard changes?"))
                .check(matches(isDisplayed()));

        onView(withText("Cancel"))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.et_title))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDiscardChanges_ConfirmsAndFinishes() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.et_title))
                .perform(replaceText("Modified Title"), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.btn_close))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withText("Discard changes?"))
                .check(matches(isDisplayed()));

        onView(withText("Discard"))
                .perform(click());
        waitFor(TEST_DELAY_MS);

    }

    @Test
    public void testAddQuestionButton_NavigatesToQuestionCreate() {
        waitFor(LOADING_DELAY_MS);

        onView(withId(R.id.et_title))
                .perform(replaceText("Test Quiz"), closeSoftKeyboard());
        waitFor(TEST_DELAY_MS);

        waitFor(EXTRA_LOADING_DELAY_MS);
        onView(withId(R.id.spinner_collection))
                .perform(click());
        waitFor(SHORT_DELAY_MS);
        onData(anything())
                .atPosition(1)
                .perform(click());
        waitFor(TEST_DELAY_MS);

        onView(withId(R.id.btn_add_question))
                .perform(click());
        waitFor(TEST_DELAY_MS);

        Intents.intended(IntentMatchers.hasComponent(
                "com.cmloopy.quizzi.views.QuestionCreate.QuestionCreateActivity"));
    }

//    @Test
//    public void testUpdateQuiz_ValidForm_ReturnsSuccessResult() {
//        waitFor(LOADING_DELAY_MS);
//
//        onView(withId(R.id.et_title))
//                .perform(replaceText("Updated Quiz Title"), closeSoftKeyboard());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.et_desc_title))
//                .perform(replaceText("Updated description"), closeSoftKeyboard());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.spinner_collection)).perform(click());
//        waitFor(SHORT_DELAY_MS);
//        onData(anything()).atPosition(1).perform(click());
//        waitFor(TEST_DELAY_MS);
//
//        onView(withId(R.id.btn_save_quiz)).perform(click());
//        waitFor(TEST_DELAY_MS);
//
//
//        onView(withText("Quiz updated successfully!"))
//                .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
//                .check(matches(isDisplayed()));
//    }
}