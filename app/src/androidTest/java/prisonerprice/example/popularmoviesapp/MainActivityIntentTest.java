package prisonerprice.example.popularmoviesapp;

import android.app.Instrumentation;
import android.example.popularmoviesapp.R;

import prisonerprice.example.popularmoviesapp.View.MainActivity;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    @Rule
    public IntentsTestRule<MainActivity> mainActivityIntentsTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // All external Intents will be blocked in this way.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(RESULT_OK, null));
    }

    @Test
    public void clickOnMoviePosterTest() {
        onView(ViewMatchers.withId(R.id.rv_main_screen)).perform(click());
        intended(isInternal());
    }

}
