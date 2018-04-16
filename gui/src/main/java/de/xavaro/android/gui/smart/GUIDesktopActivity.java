package de.xavaro.android.gui.smart;

import android.hardware.Camera;
import android.support.annotation.Nullable;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import de.xavaro.android.cam.gls.SurfaceView;
import de.xavaro.android.cam.rtsp.RtspServer;
import de.xavaro.android.cam.session.SessionBuilder;
import de.xavaro.android.cam.streams.VideoQuality;
import de.xavaro.android.cam.util.CAMGetVideoModes;
import de.xavaro.android.gui.plugin.GUIPluginTitle;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.skills.GUICanFocusDelegate;
import de.xavaro.android.gui.views.GUIEditText;
import de.xavaro.android.gui.wizzards.GUIDomainsWizzard;
import de.xavaro.android.gui.wizzards.GUIFixedWizzard;
import de.xavaro.android.gui.wizzards.GUIGeoposWizzard;
import de.xavaro.android.gui.wizzards.GUICamerasWizzard;
import de.xavaro.android.gui.wizzards.GUIChannelWizzard;
import de.xavaro.android.gui.wizzards.GUIGeomapWizzard;
import de.xavaro.android.gui.wizzards.GUICameraWizzard;
import de.xavaro.android.gui.wizzards.GUILocationsWizzard;
import de.xavaro.android.gui.wizzards.GUISettingsWizzard;
import de.xavaro.android.gui.wizzards.GUISetupWizzard;
import de.xavaro.android.gui.wizzards.GUIMenuWizzard;
import de.xavaro.android.gui.wizzards.GUIPingWizzard;
import de.xavaro.android.gui.wizzards.GUIStreetviewWizzard;
import de.xavaro.android.gui.wizzards.GUITodoWizzard;

import de.xavaro.android.gui.plugin.GUIPluginTitleIOT;
import de.xavaro.android.gui.plugin.GUIPlugin;
import de.xavaro.android.gui.plugin.GUIToastBar;

import de.xavaro.android.gui.views.GUIDialogView;

import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.pub.interfaces.ext.OnSpeechHandler;

public class GUIDesktopActivity extends GUIActivity implements OnSpeechHandler
{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    private GUIToastBar toastBar;
    private SurfaceView surfaceView;

    private Map<String,GUIPlugin> wizzards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GUI.instance.desktopActivity = this;

        toastBar = new GUIToastBar(this);
        topframe.addView(toastBar, toastBar.getPreferredLayout());

        wizzards = new LinkedHashMap<>();

        // @formatter:off

        wizzards.put(GUIMenuWizzard.      class.getSimpleName(), new GUIMenuWizzard      (this));
        wizzards.put(GUIPingWizzard.      class.getSimpleName(), new GUIPingWizzard      (this));
        wizzards.put(GUITodoWizzard.      class.getSimpleName(), new GUITodoWizzard      (this));
        wizzards.put(GUIFixedWizzard.     class.getSimpleName(), new GUIFixedWizzard     (this));
        wizzards.put(GUISetupWizzard.     class.getSimpleName(), new GUISetupWizzard     (this));
        wizzards.put(GUIDomainsWizzard.   class.getSimpleName(), new GUIDomainsWizzard   (this));
        wizzards.put(GUILocationsWizzard. class.getSimpleName(), new GUILocationsWizzard (this));
        wizzards.put(GUISettingsWizzard.  class.getSimpleName(), new GUISettingsWizzard  (this));
        wizzards.put(GUICameraWizzard.    class.getSimpleName(), new GUICameraWizzard    (this));
        wizzards.put(GUICamerasWizzard.   class.getSimpleName(), new GUICamerasWizzard   (this));
        wizzards.put(GUIChannelWizzard.   class.getSimpleName(), new GUIChannelWizzard   (this));
        wizzards.put(GUIGeoposWizzard.    class.getSimpleName(), new GUIGeoposWizzard    (this));
        wizzards.put(GUIGeomapWizzard.    class.getSimpleName(), new GUIGeomapWizzard    (this));
        wizzards.put(GUIStreetviewWizzard.class.getSimpleName(), new GUIStreetviewWizzard(this));

        // @formatter:on

        if (!Simple.isPhone())
        {
            displayWizzard(GUISetupWizzard.class.getSimpleName());
            //displayWizzard(GUIPingWizzard.class.getSimpleName());
            //displayWizzard(GUIDomainsWizzard.class.getSimpleName());
        }

        //GUIDialogViewPincode pincode = new GUIDialogViewPincode(this);
        //topframe.addView(pincode);

        surfaceView = new SurfaceView(this, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                160,
                240,
                Gravity.TOP);

        lp.leftMargin = 100;
        lp.topMargin = 200;

        surfaceView.setLayoutParams(lp);

        topframe.addView(surfaceView);

        //CAMGetVideoModes.getVideoModes();

        Simple.getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                /*
                surfaceView.startGLThread();

                try
                {
                    Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    camera.setPreviewTexture(surfaceView.getSurfaceTexture());
                    camera.setDisplayOrientation(90);
                    camera.startPreview();

                    Log.d(LOGTAG, "################# camera open...");
                }
                catch(Exception ex)
                {
                    Log.d(LOGTAG, "############# knallt");

                    ex.printStackTrace();
                }
                */

                SessionBuilder.getInstance()
                        .setSurfaceView(surfaceView)
                        .setPreviewOrientation(90)
                        .setContext(getApplicationContext())
                        .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                        .setVideoEncoder(SessionBuilder.VIDEO_H264)
                        .setCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
                        .setVideoQuality(new VideoQuality(320, 240, 20, 500000));

                Log.d(LOGTAG, "onCreate: start RTSP.");

                GUIDesktopActivity.this.startService(new Intent(GUIDesktopActivity.this, RtspServer.class));

            }
        }, 1000);

        checkWindowSize();
   }

   public Map<String,GUIPlugin> getWizzards()
   {
        return wizzards;
   }

   @Nullable
   public GUIPlugin getWizzard(String wizzard)
   {
       if (wizzard == null) return null;

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
       {
           return wizzards.getOrDefault(wizzard, null);
       }
       else
       {
           try
           {
               return wizzards.get(wizzard);
           }
           catch (Exception ignore)
           {
               return null;
           }
       }
   }

    public void displayWizzard(String name)
    {
        GUIPlugin wizzard = getWizzard(name);
        if (wizzard == null) return;

        hideAllWizzards();
        showPlugin(wizzard);
    }

    public void displayWizzard(String name, String tag)
    {
        GUIPlugin wizzard = getWizzard(name);
        if (wizzard == null) return;

        if (wizzard instanceof GUIPluginTitleIOT)
        {
            ((GUIPluginTitleIOT) wizzard).setIOTObject(tag);
        }
        else
        {
            if (wizzard instanceof GUIPluginTitle)
            {
                ((GUIPluginTitle) wizzard).setObjectTag(tag);
            }
        }

        showPlugin(wizzard);
    }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        //
        // Check for dialogs first.
        //

        int max = topframe.getChildCount() - 1;

        for (int inx = max; inx >= 0; inx--)
        {
            View plugin = topframe.getChildAt(inx);
            if (plugin == toastBar) continue;

            if (topframe.getChildAt(inx) instanceof GUIDialogView)
            {
                Log.d(LOGTAG, "onBackPressed: dialog dismiss=" + plugin.getClass().getSimpleName());

                ((GUIDialogView) plugin).dismissDialog();

                return;
            }
        }

        //
        // Check for plugins which might handle it.
        //

        for (int inx = max; inx >= 0; inx--)
        {
            View plugin = topframe.getChildAt(inx);
            if (plugin == toastBar) continue;

            if (plugin instanceof GUIPlugin)
            {
                if (((GUIPlugin) plugin).onBackPressed())
                {
                    return;
                }
            }
        }

        //
        // Check for wizzard helpers.
        //

        for (int inx = max; inx >= 0; inx--)
        {
            View plugin = topframe.getChildAt(inx);
            if (plugin == toastBar) continue;

            if ((plugin instanceof GUIPlugin) && ((GUIPlugin) plugin).isHelper())
            {
                Log.d(LOGTAG, "onBackPressed: helper hide=" + plugin.getClass().getSimpleName());

                hidePlugin((GUIPlugin) plugin);

                return;
            }
        }

        //
        // Check for wizzards.
        //

        for (int inx = max; inx >= 0; inx--)
        {
            View plugin = topframe.getChildAt(inx);
            if (plugin == toastBar) continue;

            if ((plugin instanceof GUIPlugin) && ((GUIPlugin) plugin).isWizzard())
            {
                Log.d(LOGTAG, "onBackPressed: wizzard hide=" + plugin.getClass().getSimpleName());

                hidePlugin((GUIPlugin) plugin);

                return;
            }
        }

        super.onBackPressed();
    }

    private void showPlugin(GUIPlugin plugin)
    {
        if ((plugin != null) && (plugin.getParent() == null))
        {
            topframe.addView(plugin);
        }

        checkWindowSize();
    }

    private void hidePlugin(GUIPlugin plugin)
    {
        if ((plugin != null) && (plugin.getParent() != null))
        {
            topframe.removeView(plugin);
        }
    }

    private void hideAllWizzards()
    {
        for (Map.Entry<String, GUIPlugin> entry : wizzards.entrySet())
        {
            hidePlugin(entry.getValue());
        }
    }

    public void displayWizzard(boolean show, String name)
    {
        if (name != null)
        {
            GUIPlugin wizzard = getWizzard(name);
            if (wizzard == null) return;

            if (show)
            {
                showPlugin(wizzard);
            }
            else
            {
                hidePlugin(wizzard);
            }
        }
    }

    public void displayMenu(boolean show)
    {
        if (show)
        {
            displayWizzard(GUIMenuWizzard.class.getSimpleName());
        }
    }

    public void displayCamera(boolean show, String uuid)
    {
        if (show)
        {
            displayWizzard(GUICameraWizzard.class.getSimpleName(), uuid);
        }
    }

    public void displayStreetView(boolean show, String address)
    {
        if (show)
        {
            bringToFront();

            String wizzardname = GUIStreetviewWizzard.class.getSimpleName();

            GUIPlugin wizzard = getWizzard(wizzardname);

            if (wizzard instanceof GUIStreetviewWizzard)
            {
                ((GUIStreetviewWizzard) wizzard).setCoordinatesFromAddress(address);

                displayWizzard(wizzardname);
            }
        }

        checkWindowSize();
    }

    public void displaySpeechRecognition(boolean show)
    {
        if (show)
        {
            bringToFront();

            if (toastBar.getParent() == null)
            {
                topframe.addView(toastBar);
            }
        }
        else
        {
            if (toastBar.getParent() != null)
            {
                topframe.removeView(toastBar);
            }
        }

        checkWindowSize();
    }

    public void displayPinCodeMessage(int timeout)
    {
        toastBar.displayPinCodeMessage(timeout);
    }

    public void displayToastMessage(String message)
    {
        toastBar.displayToastMessage(message, 10, false);
    }

    public void displayToastMessage(String message, int seconds, boolean emphasis)
    {
        toastBar.displayToastMessage(message, seconds, emphasis);
    }

    private void checkWindowSize()
    {
        /*
        if ((topframe.getChildCount() == 0)
                || (toastBar.getParent() != null) && (topframe.getChildCount() == 1))
        {
            setWindowHeightDip(Simple.WC);
        }
        else
        {
            setWindowHeightDip(Simple.MP);
        }
        */

        if (! isActive())
        {
            startActivity(new Intent(this, getClass()));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        String name = null;

        if (keyCode == KeyEvent.KEYCODE_PROG_RED) name = GUICamerasWizzard.class.getSimpleName();
        if (keyCode == KeyEvent.KEYCODE_PROG_GREEN) name = GUIPingWizzard.class.getSimpleName();
        if (keyCode == KeyEvent.KEYCODE_PROG_YELLOW) name = GUISetupWizzard.class.getSimpleName();
        if (keyCode == KeyEvent.KEYCODE_PROG_BLUE) name = GUIMenuWizzard.class.getSimpleName();

        GUIPlugin wizzard = getWizzard(name);

        if (wizzard != null)
        {
            if (wizzard.isAttached())
            {
                hideAllWizzards();
            }
            else
            {
                hideAllWizzards();
                showPlugin(wizzard);
            }
        }

        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivateRemote()
    {
        toastBar.onActivateRemote();
    }

    @Override
    public void onSpeechReady()
    {
        toastBar.onSpeechReady();
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        View focused = GUICanFocusDelegate.getFocusedView();

        if ((focused != null) && (focused instanceof GUIEditText))
        {
            boolean partial = Json.getBoolean(speech, "partial");
            JSONArray results = Json.getArray(speech, "results");

            if ((results != null) && (results.length() > 0) && ! partial)
            {
                JSONObject result = Json.getObject(results, 0);
                String text = Json.getString(result, "text");

                if (text != null)
                {
                    ((GUIEditText) focused).setText(text);
                    ((GUIEditText) focused).onHighlightChanged(focused, false);

                    return;
                }
            }
        }

        toastBar.onSpeechResults(speech);
    }
}