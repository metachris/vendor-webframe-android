package at.metalab.android.vendor.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.os.Handler;
import android.widget.EditText;

/**
 * Created by Chris Hager <chris@linuxuser.at> on 02/04/14.
 */
public class MainActivity extends Activity {
    private static final String TAG = "VendorMainApp";
    private static final String URL = "http://www.hackerspaceshop.com/automat/";

    // Whether to enable WakeLock (to keep screen from dimming)
    private static final boolean keepScreenOn = false;
    private static final boolean useWakeLock = false;
    private static final boolean hideSystemUI = true;

    Handler mHandler = new Handler();

    private long lastTouchMs = 0;
    private int touchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);

        if (hideSystemUI) {
            hideSystemUi();
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility == 0) {
                        mHandler.postDelayed(mHideRunnable, 2000);
                    }
                }
            });
        }

        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (useWakeLock) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            wakeLock.acquire();
        }

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.loadUrl(URL);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.v(TAG, "touch");
                if (v.getId() == R.id.webView && event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (lastTouchMs > 0) {
                        long msDiff = System.currentTimeMillis() - lastTouchMs;
                        if (msDiff > 1000)
                            touchCount = 0;
                        else
                            touchCount++;

                        Log.v(TAG, "" + touchCount);
                        if (touchCount == 8) {
                            showExitDialog();
                        }
                    }
                    lastTouchMs = System.currentTimeMillis();
                }
                return false;
            }
        });
    }

    private void showExitDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Hosen runter du nackter Affe")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String s = input.getText().toString();
                        if (s.equals("ente")) {
                            finish();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideSystemUi();
        }
    };
}
