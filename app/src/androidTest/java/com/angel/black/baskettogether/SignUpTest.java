package com.blackangel.baskettogether;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.blackangel.baskettogether.signup.SignUpActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by KimJeongHun on 2016-05-24.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpTest extends ActivityTestCase {
    @Rule
    public ActivityTestRule<SignUpActivity> mActivityRule = new ActivityTestRule<>(SignUpActivity.class);

    @Before
    public void setUp() {

    }

    @Test
    public void testSignUp() {
        // 아이디 미입력 확인
        onView(withId(R.id.btn_sign_up)).perform(click());
        onView(withText(R.string.error_not_input_id)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        // 비밀번호 미입력 확인
        onView(withId(R.id.user_id)).perform(typeText("jointest"));
        onView(withId(R.id.btn_sign_up)).perform(click());
        onView(withText(R.string.error_not_input_pwd)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        // 비밀번호 미입력 확인
        onView(withId(R.id.password)).perform(typeText("qqqqqqqq"));
        onView(withId(R.id.btn_sign_up)).perform(click());
        onView(withText(R.string.error_not_input_pwd_re)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        // 닉네임 미입력 확인
        onView(withId(R.id.password_re)).perform(typeText("qqqqqqqq"));
        onView(withId(R.id.btn_sign_up)).perform(click());
        onView(withText(R.string.error_not_input_nickname)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.user_nickname)).perform(typeText("joinTestNickName"));
        onView(withId(R.id.btn_sign_up)).perform(click());

    }
}
