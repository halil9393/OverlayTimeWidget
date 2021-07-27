package com.example.OverlayTimeWidget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WidgetService extends Service {

    int LAYOUT_FLAG;
    View mFloatingView;
    WindowManager windowManager;
    ImageView imageClose;
    TextView text_widget;

    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();
    int oncekiKonumX, oncekiKonumY, sonKonumX, sonKonumY;

    float dX = 0, dY = 0;
    long startClickTime = 0;
    int MAX_CLICK_DURATION = 20;
    int widthOfScreen, heightOfScreen;
    int DX, DY, DX2, DY2;
    float frenKatsayisi;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor", "InflateParams"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        heightOfScreen = displayMetrics.heightPixels;

        // inflate float widget
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget, null);
        mFloatingView.setVisibility(View.VISIBLE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //initial position
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = 0;


        //layout prams for close button
        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.TOP | Gravity.START;
        imageParams.x = (widthOfScreen - imageParams.width) / 2;
        imageParams.y = (heightOfScreen - 3 * imageParams.height);


        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_baseline_close_24);
        imageClose.setVisibility(View.INVISIBLE);


        windowManager.addView(imageClose, imageParams);
        windowManager.addView(mFloatingView, layoutParams);


        text_widget = mFloatingView.findViewById(R.id.text_widget);

        //zaman güncelleme işlemi
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                text_widget.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                handler.postDelayed(this, 1000);
            }
        }, 10);


        // drag movement for widget
        text_widget.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    imageClose.setVisibility(View.VISIBLE);

                    oncekiKonumX = (int) event.getRawX();
                    oncekiKonumY = (int) event.getRawY();

                    dX = layoutParams.x - event.getRawX();
                    dY = layoutParams.y - event.getRawY();

                    Log.i("tag_dxdy", "layoutparamsx = " + layoutParams.x + " layaoutparamsy: " + layoutParams.y);

                    return true;

                case MotionEvent.ACTION_MOVE:

                    // view bırakılmadan önceki en yakın konumunu bulmak ve eğim hesaplamak için...
                    handler2.removeCallbacksAndMessages(null);
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            oncekiKonumX = (int) event.getRawX();
                            oncekiKonumY = (int) event.getRawY();
                            Log.i("tag_slope", "onceki eventX: " + oncekiKonumX + " eventY: " + oncekiKonumY);
                            handler2.postDelayed(this, 100);
                        }
                    }, 10);

                    //calculate x y coordinates
                    layoutParams.x = (int) (event.getRawX() + dX);
                    layoutParams.y = (int) (event.getRawY() + dY);

                    // update widget coordinates
                    windowManager.updateViewLayout(mFloatingView, layoutParams);

                    // kapatma iconuna yakınlaştığında yapılacak işlemler, animasyonlar...
                    if (kontrolet(layoutParams, imageParams)) {
                        imageClose.setBackgroundColor(R.color.purple_200);
                    } else {
                        imageClose.setBackgroundColor(R.color.black);
                    }

                    return true;

                case MotionEvent.ACTION_UP:
                    handler2.removeCallbacksAndMessages(null);
                    sonKonumX = (int) event.getRawX();
                    sonKonumY = (int) event.getRawY();
                    Log.i("tag_slope", "sonKonumX: " + sonKonumX + " sonKonumY: " + sonKonumY);

                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    imageClose.setVisibility(View.INVISIBLE);

                    if (clickDuration < MAX_CLICK_DURATION) { // click eventi olarak kullanılabilir...
                        Toast.makeText(this, "time:" + text_widget.getText().toString(), Toast.LENGTH_SHORT).show();
                    } else {

                        if (kontrolet(layoutParams, imageParams)) stopSelf();   //remove widget
                        else {
                            firlatmaBaslat(layoutParams);
                        }
                    }
                    return true;
            }

            return true;
        });

        return START_STICKY;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean kontrolet(WindowManager.@NotNull LayoutParams layoutParams, WindowManager.@NotNull LayoutParams imageParams) {
        //kapatma iconu ile yakınlık yada çakışma kontrolü
        boolean sonuc = false;
        if (Math.abs((layoutParams.x + mFloatingView.getWidth() / 2) - (imageParams.x + imageClose.getWidth() / 2)) <= (mFloatingView.getWidth() / 2 + imageClose.getWidth() / 2)
                && Math.abs((layoutParams.y + mFloatingView.getHeight() / 2) - (imageParams.y + imageClose.getHeight() / 2)) <= (mFloatingView.getHeight() / 2 + imageClose.getHeight() / 2)) {
            sonuc = true;
        }
        return sonuc;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public void firlatmaBaslat(WindowManager.LayoutParams layoutParams) {
        // Touch eventinden gelen hareket ivmesi yavaşça durdurulması için, aniden durma olmayacak
        DX = sonKonumX - oncekiKonumX;
        DY = sonKonumY - oncekiKonumY;
        DX2 = DX;
        DY2 = DY;
        frenKatsayisi = 1;
        int maxPixel = 150;
        if(DX2<-maxPixel) DX2 = -maxPixel;     // max hızı belirlemek için hareket pixel cinsinden sınırlandırıldı
        else if(DX2>maxPixel) DX2 = maxPixel;
        if(DY2<-maxPixel) DY2 = -maxPixel;
        else if(DY2>maxPixel) DY2 = maxPixel;

        Log.i("tag_slope", "DX: " + DX + " DY: " + DY);

        handler3.removeCallbacksAndMessages(null);
        handler3.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (DX <= 0 && DX2 <= 0) {
                    layoutParams.x += DX2;
                    DX2 += frenKatsayisi * 10;
                } else if (DX > 0 && DX2 > 0) {
                    layoutParams.x += DX2;
                    DX2 -= frenKatsayisi * 10;
                }

                if (DY <= 0 && DY2 <= 0) {
                    layoutParams.y += DY2;
                    DY2 += frenKatsayisi * 10;
                } else if (DY > 0 && DY2 > 0) {
                    layoutParams.y += DY2;
                    DY2 -= frenKatsayisi * 10;
                }

                windowManager.updateViewLayout(mFloatingView, layoutParams);
                Log.i("tag_slopefirlatma", "DX2: " + DX2 + " DY2: " + DY2);
                frenKatsayisi += 0.4;

                if ((DX < 0 && DX2 > 0 || DX > 0 && DX2 < 0) && (DY < 0 && DY2 > 0 || DY > 0 && DY2 < 0)) {
                    handler3.removeCallbacksAndMessages(null);
                    Log.i("tag_slope", "egim durdu,en yakın kenar hesaplanacak..");
                    enYakinKenar(layoutParams);
                } else {
                    handler3.postDelayed(this, 16);
                }


            }
        }, 5);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public void enYakinKenar(WindowManager.LayoutParams layoutParams) {
        // hareket sıfırlandığında, en yakın kenara hareket etmesini sağlar
        int[] mesafeler = kenarMesafeleriHesapla(layoutParams);
        if (Math.abs(mesafeler[0]) <= Math.abs(mesafeler[1])) {
            animasyon(layoutParams, "X", mesafeler[0]);
        } else {
            animasyon(layoutParams, "Y", mesafeler[1]);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public int[] kenarMesafeleriHesapla(WindowManager.@NotNull LayoutParams layoutParams) {
        // kenarlara olan mesafeler kıyaslanıp, en yakın kenara olan uzaklık hesaplamak için
        int[] liste = new int[2];
        int xMesafesi, yMesafesi;

        if (layoutParams.x < 0) {
            layoutParams.x = 0;
        } else if (layoutParams.x + mFloatingView.getWidth() > widthOfScreen) {
            layoutParams.x = widthOfScreen - mFloatingView.getWidth();
        }

        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        } else if (layoutParams.y + mFloatingView.getHeight() > heightOfScreen) {
            layoutParams.y = heightOfScreen - mFloatingView.getHeight();
        }

        int xMerkez = mFloatingView.getWidth() / 2 + layoutParams.x;
        int yMerkez = mFloatingView.getHeight() / 2 + layoutParams.y;

        if (xMerkez <= widthOfScreen / 2) {
            xMesafesi = -layoutParams.x;
        } else {
            xMesafesi = widthOfScreen - layoutParams.x - mFloatingView.getWidth();
        }

        if (yMerkez <= heightOfScreen/2) {
            yMesafesi = -layoutParams.y;
        } else {
            yMesafesi = heightOfScreen - layoutParams.y - mFloatingView.getHeight();
        }

        liste[0] = xMesafesi;
        liste[1] = yMesafesi;

        Log.i("tag_mesafeler", "xMesafesi: " + xMesafesi + " yMesafesi: " + yMesafesi);

        return liste;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public void animasyon(WindowManager.LayoutParams layoutParams, @NotNull String yon, int mesafe) {
        // en yakın kenara hareket ettirmek için
        if (yon.equals("X") && mesafe != 0) {
            handler1.removeCallbacksAndMessages(null);
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mesafe > 0) layoutParams.x = layoutParams.x + 10;
                    else layoutParams.x = layoutParams.x - 10;

                    windowManager.updateViewLayout(mFloatingView, layoutParams);

                    if (layoutParams.x <= 0 || layoutParams.x >= widthOfScreen - mFloatingView.getWidth()) {
                        handler1.removeCallbacksAndMessages(null);
                        kenarSifirlama(layoutParams);
                    } else {
                        handler1.postDelayed(this, 16);
                    }

                }
            }, 10);

        } else if (yon.equals("Y") && mesafe != 0) {
            handler1.removeCallbacksAndMessages(null);
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mesafe > 0) layoutParams.y = layoutParams.y + 10;
                    else layoutParams.y = layoutParams.y - 10;

                    windowManager.updateViewLayout(mFloatingView, layoutParams);

                    if (layoutParams.y <= 0 || layoutParams.y >= heightOfScreen - mFloatingView.getHeight()) {
                        handler1.removeCallbacksAndMessages(null);
                        kenarSifirlama(layoutParams);
                    } else {
                        handler1.postDelayed(this, 16);
                    }

                }
            }, 10);

        }else kenarSifirlama(layoutParams);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public void kenarSifirlama(@org.jetbrains.annotations.NotNull WindowManager.LayoutParams layoutParams) {
        // kenarlardan taşma var mı kontrol edilecek, kenarlar ekrana sıfırlanacak
        if (layoutParams.x < 0) {
            layoutParams.x = 0;
        } else if (layoutParams.x + mFloatingView.getWidth() > widthOfScreen) {
            layoutParams.x = widthOfScreen - mFloatingView.getWidth();
        }

        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        } else if (layoutParams.y + mFloatingView.getHeight() > heightOfScreen) {
            layoutParams.y = heightOfScreen - mFloatingView.getHeight();
        }
        windowManager.updateViewLayout(mFloatingView, layoutParams);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            windowManager.removeView(mFloatingView);
        }

        if (imageClose != null) {
            windowManager.removeView(imageClose);
        }
    }
}
