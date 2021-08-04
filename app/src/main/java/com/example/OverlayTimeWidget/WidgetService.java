package com.example.OverlayTimeWidget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
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
import android.view.ViewGroup;
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
    static WindowManager windowManager;
    ImageView imageClose;
    TextView text_widget;

    static Handler handler1 = new Handler();
    static Handler handler2 = new Handler();
    static Handler handler3 = new Handler();



    int oncekiKonumX, oncekiKonumY, sonKonumX, sonKonumY;

    float dX = 0, dY = 0;
    long startClickTime = 0;
    int MAX_CLICK_DURATION = 20;
    int widthOfScreen, heightOfScreen;
    int DX, DY, DX2, DY2;
    float frenKatsayisi;

    public static boolean kontrolet2(View view1, View view2) {
        //kapatma iconu ile yakınlık yada çakışma kontrolü

        WindowManager.LayoutParams layoutParams1 = (WindowManager.LayoutParams) view1.getLayoutParams();
        WindowManager.LayoutParams layoutParams2 = (WindowManager.LayoutParams) view2.getLayoutParams();

        Log.i("tag_get", "view1 x :" + layoutParams1.x + " view1 Y:" + layoutParams1.y);
        Log.i("tag_get", "view2 X :" + layoutParams2.y + " view2 Y:" + layoutParams2.y);

        boolean sonuc = false;

        if (Math.abs((layoutParams1.x + layoutParams1.width / 2) - (layoutParams2.x + layoutParams2.width / 2)) <= (layoutParams1.width / 2 + layoutParams2.width / 2)
                && Math.abs((layoutParams1.y + layoutParams1.height/ 2) - (layoutParams2.y + layoutParams2.height / 2)) <= (layoutParams1.height / 2 + layoutParams2.height / 2)) {
            sonuc = true;
        }

        Log.i("tag_kontrolet2","layoutx:"+layoutParams1.x+" "+layoutParams1.width+" "+layoutParams1.height);
        Log.i("tag_kontrolet2","getx:"+view1.getX()+" "+view1.getWidth()+" "+view1.getHeight());

        return sonuc;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor", "InflateParams"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler1 = new Handler();
        handler2 = new Handler();
        handler3 = new Handler();

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
        Log.i("tag_params","layoutprams.width:"+layoutParams.width);

        Handler handler8 = new Handler();// layoutparams başta wrap content verildiğinde -2 değeri kalıyor. onu değiştirmek gerekiyor metotların doğru çalışması için
        handler8.postDelayed(() -> {
            layoutParams.width =mFloatingView.getWidth();
            layoutParams.height = mFloatingView.getHeight();
            Log.i("tag_tamam","getwidth:"+mFloatingView.getWidth()+" params.width:"+layoutParams.width);
        },1000);


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

                    handler1.removeCallbacksAndMessages(null);
                    handler2.removeCallbacksAndMessages(null);
                    handler3.removeCallbacksAndMessages(null);

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
                    if (kontrolet2(mFloatingView, imageClose)) {
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

                        if (kontrolet2(mFloatingView, imageClose)) {
                            handler1.removeCallbacksAndMessages(null);
                            handler2.removeCallbacksAndMessages(null);
                            handler3.removeCallbacksAndMessages(null);
                            stopSelf();   //remove widget
                        } else {
                            firlatmaBaslat(layoutParams);
                        }

                    }
                    return true;
            }

            return true;
        });

        return START_STICKY;
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
        if (DX2 < -maxPixel) DX2 = -maxPixel;     // max hızı belirlemek için hareket pixel cinsinden sınırlandırıldı
        else if (DX2 > maxPixel) DX2 = maxPixel;
        if (DY2 < -maxPixel) DY2 = -maxPixel;
        else if (DY2 > maxPixel) DY2 = maxPixel;

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
                    Log.i("tag_slope", "firlatma egimi durdu,en yakın kenar hesaplanacak..");
                    enYakinKenar(mFloatingView,widthOfScreen,heightOfScreen);
                } else {
                    handler3.postDelayed(this, 16);
                }


            }
        }, 5);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static  void enYakinKenar(@NotNull View view, int widthOfScreen, int heightOfScreen) {
        // hareket sıfırlandığında, en yakın kenara hareket etmesini sağlar
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        int[] mesafeler = kenarMesafeleriHesapla2(view,widthOfScreen,heightOfScreen);
        if (Math.abs(mesafeler[0]) <= Math.abs(mesafeler[1])) {
            animasyon2(view, "X", mesafeler[0],widthOfScreen,heightOfScreen);
        } else {
            animasyon2(view, "Y", mesafeler[1],widthOfScreen,heightOfScreen);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static int[] kenarMesafeleriHesapla2(@NotNull View view, int widthOfScreen, int heightOfScreen) {
        // kenarlara olan mesafeler kıyaslanıp, en yakın kenara olan uzaklık hesaplamak için

        int xMesafesi, yMesafesi;
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();

        // Kenarlardan dışarı çıktığı takdirde kenara sıfır hizalmak için...
        if (layoutParams.x < 0) {
            layoutParams.x = 0;
        } else if (layoutParams.x + layoutParams.width > widthOfScreen) {
            layoutParams.x = widthOfScreen - layoutParams.width;
        }

        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        } else if (layoutParams.y + layoutParams.height > heightOfScreen) {
            layoutParams.y = heightOfScreen - layoutParams.height;
        }

        // Calculate paddings from edges...
        int xMerkez = layoutParams.width / 2 + layoutParams.x;
        int yMerkez = layoutParams.height / 2 + layoutParams.y;

        if (xMerkez <= widthOfScreen / 2) {
            xMesafesi = -layoutParams.x;
        } else {
            xMesafesi = widthOfScreen - layoutParams.x - layoutParams.width;
            Log.i("tag_view","viewgetwidth:"+layoutParams.width);
        }

        if (yMerkez <= heightOfScreen / 2) {
            yMesafesi = -layoutParams.y;
        } else {
            yMesafesi = heightOfScreen - layoutParams.y - layoutParams.height;
        }

        Log.i("tag_mesafeler", "xMesafesi: " + xMesafesi + " yMesafesi: " + yMesafesi);

        return new int[] {xMesafesi,yMesafesi};
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static void animasyon2(@NotNull View view, @NotNull String yon, int mesafe, int widthOfScreen, int heightOfScreen) {
        // en yakın kenara hareket ettirmek, yanastirmak için
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        if (yon.equals("X") && mesafe != 0) {
            handler1.removeCallbacksAndMessages(null);
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.i("tag_handler","handler çalıştı layoutparamsx :"+layoutParams.x);
                    if (mesafe > 0) layoutParams.x = layoutParams.x + 10;
                    else layoutParams.x = layoutParams.x - 10;

                    windowManager.updateViewLayout(view, layoutParams);

                    if (layoutParams.x <= 0 || layoutParams.x >= widthOfScreen - layoutParams.width) {
                        handler1.removeCallbacksAndMessages(null);
                        kenarSifirlama(view,widthOfScreen,heightOfScreen);
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

                    windowManager.updateViewLayout(view, layoutParams);

                    if (layoutParams.y <= 0 || layoutParams.y >= heightOfScreen - layoutParams.height) {
                        handler1.removeCallbacksAndMessages(null);
                        kenarSifirlama(view,widthOfScreen,heightOfScreen);
                    } else {
                        handler1.postDelayed(this, 16);
                    }

                }
            }, 10);

        } else kenarSifirlama(view,widthOfScreen,heightOfScreen);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public static void kenarSifirlama(@NotNull View view, int widthOfScreen, int heightOfScreen){
        // kenarlardan taşma var mı kontrol edilecek, kenarlar ekrana sıfırlanacak
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();

        if (layoutParams.x < 0) {
            layoutParams.x = 0;
        } else if (layoutParams.x + layoutParams.width > widthOfScreen) {
            layoutParams.x = widthOfScreen - layoutParams.width;
        }

        if (layoutParams.y < 0) {
            layoutParams.y = 0;
        } else if (layoutParams.y + layoutParams.height > heightOfScreen) {
            layoutParams.y = heightOfScreen - layoutParams.height;
        }

        windowManager.updateViewLayout(view, layoutParams);
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
