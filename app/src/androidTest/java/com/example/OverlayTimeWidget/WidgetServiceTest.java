package com.example.OverlayTimeWidget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WidgetServiceTest {

    @Test
    public void ikiView_ustUsteGelmesiDurumu_true() {

        //Given
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
        layoutParams2.x = 180;  // 100-200 arası değerler girilebilir
        layoutParams2.y = 120;  // 100-200 arası değerler girilebilir
        view2.setLayoutParams(layoutParams2);

        //When
        Boolean sonuc = WidgetService.kontrolet2(view1, view2);
        Boolean sonuc2 = WidgetService.kontrolet2(view2, view1);

        //Then
        assertEquals("İki view objesi ust uste gelmiyor. Look--->WidgetService.kontrolet2()", true, sonuc);
        assertEquals("İki view objesi ust uste gelmiyor. Look--->WidgetService.kontrolet2()", true, sonuc2);

    }


    @Test
    public void ikiView_ustUsteGelmemesiDurumu_false() {
        //Given
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
        layoutParams2.x = 300;
        layoutParams2.y = 300;
        view2.setLayoutParams(layoutParams2);

        //When
        Boolean sonuc = WidgetService.kontrolet2(view1, view2);
        Boolean sonuc2 = WidgetService.kontrolet2(view2, view1);

        //Then
        assertEquals("İki view objesi ust uste geliyor. Look--->WidgetService.kontrolet2()", false, sonuc);
        assertEquals("İki view objesi ust uste geliyor. Look--->WidgetService.kontrolet2()", false, sonuc2);

    }


    @Test
    public void view_birakildiginda_kenarlaraOlanUzaklikKontrolu() {
        //Given
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
        layoutParams2.x = 300;
        layoutParams2.y = 300;
        view2.setLayoutParams(layoutParams2);

        View view3 = new View(appContext);
        WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams();
        layoutParams3.width = 100;
        layoutParams3.height = 100;
        layoutParams3.gravity = Gravity.TOP | Gravity.START;
        layoutParams3.x = 400;
        layoutParams3.y = 400;
        view3.setLayoutParams(layoutParams3);

        View view4 = new View(appContext);
        WindowManager.LayoutParams layoutParams4 = new WindowManager.LayoutParams();
        layoutParams4.width = 100;
        layoutParams4.height = 100;
        layoutParams4.gravity = Gravity.TOP | Gravity.START;
        layoutParams4.x = 500;
        layoutParams4.y = 800;
        view4.setLayoutParams(layoutParams4);

        View view5 = new View(appContext);
        WindowManager.LayoutParams layoutParams5 = new WindowManager.LayoutParams();
        layoutParams5.width = 100;
        layoutParams5.height = 100;
        layoutParams5.gravity = Gravity.TOP | Gravity.START;
        layoutParams5.x = -200;
        layoutParams5.y = 800;
        view5.setLayoutParams(layoutParams5);

        View view6 = new View(appContext);
        WindowManager.LayoutParams layoutParams6 = new WindowManager.LayoutParams();
        layoutParams6.width = 100;
        layoutParams6.height = 100;
        layoutParams6.gravity = Gravity.TOP | Gravity.START;
        layoutParams6.x = 500;
        layoutParams6.y = -100;
        view6.setLayoutParams(layoutParams6);

        // When
        int[] list1 = WidgetService.kenarMesafeleriHesapla2(view1, 700, 1200);
        int[] list2 = WidgetService.kenarMesafeleriHesapla2(view2, 700, 1200);
        int[] list3 = WidgetService.kenarMesafeleriHesapla2(view3, 700, 1200);
        int[] list4 = WidgetService.kenarMesafeleriHesapla2(view4, 700, 1200);
        int[] list5 = WidgetService.kenarMesafeleriHesapla2(view5, 700, 1200);
        int[] list6 = WidgetService.kenarMesafeleriHesapla2(view6, 700, 1200);

        //Then
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{-100, -100}, list1);
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{-300, -300}, list2);
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{200, -400}, list3);
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{100, 300}, list4);
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{0, 300}, list5);
        assertArrayEquals("Kenarlara olan uzaklık yanlış hesaplaniyor. Look--->WidgetService.kenarMesafeleriHesapla2()", new int[]{100, 0}, list6);

    }


}
