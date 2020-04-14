package prisonerprice.example.popularmoviesapp;

import android.example.popularmoviesapp.R;

import prisonerprice.example.popularmoviesapp.View.MainActivity;
import android.os.SystemClock;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickOnAnyMovieTest() {
        // 1. find the view
        // 2. perform an action on the view
        // 3. check if the action results in the expected output
        // this test is a dummy test

        onView(ViewMatchers.withId(R.id.rv_main_screen)).perform(click());
        onView(withId(R.id.detail_iv_poster)).check(matches(isDisplayed()));
    }

    @Test
    public void mainScreenAdapterTest() {
        // Not pass, waited to be improved
        // Waiting for recyclerView loading
        SystemClock.sleep(15000);
        onData(anything()).inAdapterView(withId(R.id.rv_main_screen)).atPosition(1).perform(click());
        onView(withId(R.id.detail_tv_synopsis)).check(matches(isDisplayed()));
    }
}
