package com.example.OverlayTimeWidget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.OverlayTimeWidget", appContext.getPackageName());

    }


    @Test
    public void kontrolEtTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        View view1 = new View(appContext);
        WindowManager.LayoutParams layoutParams1 = new WindowManager.LayoutParams();
        layoutParams1.width = 100;
        layoutParams1.height = 100;
        layoutParams1.gravity = Gravity.TOP | Gravity.START;
        layoutParams1.x = 100;
        layoutParams1.y = 100;
        view1.setLayoutParams(layoutParams1);

        View view2 = new View(appContext);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.width = 100;
        layoutParams2.height = 100;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;
        layoutParams2.x = 100;
        layoutParams2.y = 100;
        view2.setLayoutParams(layoutParams2);

        Boolean sonuc = WidgetService.kontrolet2(view1,view2);

        assertEquals("İki view objesi çakışıyor",false,sonuc);


    }
}