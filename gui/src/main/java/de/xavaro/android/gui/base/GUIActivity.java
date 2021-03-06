package de.xavaro.android.gui.base;

import android.support.annotation.NonNull;
import android.annotation.SuppressLint;

import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.widget.FrameLayout;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.View;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import de.xavaro.android.gui.plugin.GUIPlugin;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.skills.GUICanFocus;
import de.xavaro.android.gui.skills.GUICanFocusDelegate;
import de.xavaro.android.gui.views.GUIDialogView;

@SuppressLint("Registered")
public class GUIActivity extends AppCompatActivity
{
    private final static String LOGTAG = GUIActivity.class.getSimpleName();

    private final static int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0x0817;
    private final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0x0816;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x0815;

    public WindowManager.LayoutParams windowParams;
    public FrameLayout.LayoutParams topFrameParams;

    public FrameLayout topframe;

    private int width = Simple.getDeviceWidth();
    private int height = Simple.getDeviceHeight();

    private int maxWidth = width;
    private int maxHeight = height;

    private int minWidth = width;
    private int minHeight = height;

    private boolean isactive;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //
        // Remove stupid menu bar.
        //

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        //
        // Window manager layout params.
        //

        windowParams = getWindow().getAttributes();

        windowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.dimAmount = 0.0f;
        windowParams.alpha = 1.0f;

        //
        // Setup background and sizes.
        //

        if (Simple.isPhone())
        {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        else
        {
            //minHeight = height = Simple.dipToPx(80);

            windowParams.width = width;
            windowParams.height = height;
            windowParams.gravity = Gravity.BOTTOM;

            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            setFinishOnTouchOutside(true);
        }

        getWindow().setAttributes(windowParams);

        //
        // Create master frame with exact sizes.
        //

        topFrameParams = new FrameLayout.LayoutParams(width, height, Gravity.TOP);

        topframe = new FrameLayout(this);
        topframe.setLayoutParams(topFrameParams);

        //topframe.setBackgroundColor(0x44440000);

        setContentView(topframe);

        setUiFlags();
    }

    public void setWindowHeightDip(int heightDip)
    {
        if (Simple.isPhone())
        {
            //
            // Phones always use full screen.
            //
        }
        else
        {
            int height;

            if (heightDip == Simple.MP)
            {
                height = maxHeight;
            }
            else
            {
                if (heightDip == Simple.WC)
                {
                    height = minHeight;
                }
                else
                {
                    height = Simple.dipToPx(heightDip);
                }
            }

            windowParams.height = height;
            getWindow().setAttributes(windowParams);

            topFrameParams.height = height;
            topframe.setLayoutParams(topFrameParams);
        }
    }

    public boolean isActive()
    {
        return isactive;
    }

    public void pressHomeKey()
    {
        Log.d(LOGTAG, "sendToBack: isactive=" + isactive);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }

    public void sendToBack()
    {
        Log.d(LOGTAG, "sendToBack: isactive=" + isactive);

        onBackPressed();
    }

    public void bringToFront()
    {
        Log.d(LOGTAG, "bringToFront: isactive=" + isactive + " class=" + this.getClass().getSimpleName());

        if (! isactive)
        {
            Intent intent = new Intent(this, this.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            try
            {
                pendingIntent.send();
            }
            catch (PendingIntent.CanceledException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onStart()
    {
        Log.d(LOGTAG, "onStart:");

        super.onStart();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                ((GUIPlugin) topframe.getChildAt(inx)).onStart();
            }
        }
    }

    @Override
    public void onResume()
    {
        Log.d(LOGTAG, "onResume:");

        super.onResume();

        setUiFlags();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                ((GUIPlugin) topframe.getChildAt(inx)).onResume();
            }
        }

        isactive = true;
    }

    @Override
    public void onPause()
    {
        Log.d(LOGTAG, "onPause:");

        super.onPause();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                ((GUIPlugin) topframe.getChildAt(inx)).onPause();
            }
        }

        isactive = false;
    }

    @Override
    public void onStop()
    {
        Log.d(LOGTAG, "onStop:");

        super.onStop();

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                ((GUIPlugin) topframe.getChildAt(inx)).onStop();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackpressed:");

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIDialogView)
            {
                if (((GUIDialogView) topframe.getChildAt(inx)).onBackPressed())
                {
                    return;
                }
            }
        }

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                if (((GUIPlugin) topframe.getChildAt(inx)).onBackPressed())
                {
                    return;
                }
            }
        }

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        Log.d(LOGTAG, "onRequestPermissionsResult:");

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            if (topframe.getChildAt(inx) instanceof GUIPlugin)
            {
                ((GUIPlugin) topframe.getChildAt(inx)).onRequestPermissionsResult(
                        requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        setUiFlags();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_SEARCH)
        {
            GUI.instance.displaySpeechRecognition(true);

            return true;
        }

        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);
    }

    private void setUiFlags()
    {
        @SuppressLint("InlinedApi")
        int uiOptions
                = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    private View savedFocusedView;
    private ArrayList<View> savedFocusableViews;

    public void saveFocusedView()
    {
        this.savedFocusedView = GUICanFocusDelegate.getFocusedView();

        Log.d(LOGTAG, "saveFocusedView: view=" + this.savedFocusedView);
    }

    public void restoreFocusedView()
    {
        if (savedFocusedView != null)
        {
            if (savedFocusedView.getParent() == null)
            {
                Log.d(LOGTAG, "restoreFocusedView: gone=" + savedFocusedView);
            }
            else
            {
                final View focusme = savedFocusedView;

                Simple.getHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        focusme.requestFocus();
                    }
                });

                Log.d(LOGTAG, "restoreFocusedView: view=" + focusme);
            }
        }
    }

    public void saveFocusState()
    {
        savedFocusedView = null;
        savedFocusableViews = new ArrayList<>();

        saveFocusedView();
        saveFocusStateRecurse(topframe);
    }

    public void restoreFocusState()
    {
        if (savedFocusableViews != null)
        {
            for (View view : savedFocusableViews)
            {
                view.setFocusable(true);
            }

            savedFocusableViews = null;
        }

        restoreFocusedView();
    }

    private void saveFocusStateRecurse(View view)
    {
        if (view instanceof ViewGroup)
        {
            ViewGroup vg = (ViewGroup) view;

            for (int inx = 0; inx < vg.getChildCount(); inx++)
            {
                View child = vg.getChildAt(inx);

                if (child instanceof GUICanFocus)
                {
                    if (((GUICanFocus) child).getIsFocusable())
                    {
                        savedFocusableViews.add(child);
                        child.setFocusable(false);
                    }
                }

                saveFocusStateRecurse(child);
            }
        }
    }
}
