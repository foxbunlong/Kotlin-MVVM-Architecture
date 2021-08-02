package com.example.mvvmarchitecture

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.example.mvvmarchitecture.activities.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class AppTest {

    private val BASIC_SAMPLE_PACKAGE = "com.example.mvvmarchitecture"
    private val LAUNCH_TIMEOUT = 5000
    private var mDevice: UiDevice? = null

    @ExperimentalTime
    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        mDevice!!.pressHome()

        // Wait for launcher
        val launcherPackage: String = getLauncherPackageName()
        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue())
        mDevice!!.wait<Boolean>(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT.toLong()
        )

        // Launch the blueprint app
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager
            .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        mDevice!!.wait<Boolean>(
            Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT.toLong()
        )
    }

    private fun getLauncherPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = ApplicationProvider.getApplicationContext<Context>().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo!!.activityInfo.packageName
    }

    @ExperimentalTime
    @Test
    fun testShowSearch_MainActivity() {
        mDevice!!.findObject(
            By.res(
                BASIC_SAMPLE_PACKAGE,
                "imgSearch"
            )
        ).click()

        // Verify if the search view is showed
        Espresso.onView(ViewMatchers.withId(R.id.lnSearch))
            .inRoot(
                RootMatchers.withDecorView(
                    Matchers.`is`(
                        activityRule.activity.window.decorView
                    )
                )
            )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @ExperimentalTime
    @Test
    fun testExecuteSearch_MainActivity() {
        mDevice!!.findObject(
            By.res(
                BASIC_SAMPLE_PACKAGE,
                "imgSearch"
            )
        ).click()

        runBlocking {
            mDevice!!.findObject(
                By.res(
                    BASIC_SAMPLE_PACKAGE,
                    "etSearch"
                )
            ).text = "neo"

            delay(1000)
            assert(activityRule.activity.mAdapter!!.coins.size == 1) // Should only has 1 record as Neo coin is unique
        }
    }

    @Test
    fun checkPreconditions() {
        Assert.assertThat(mDevice, CoreMatchers.notNullValue())
    }
}