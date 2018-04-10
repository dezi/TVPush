package de.xavaro.android.gui.smart;

import android.support.annotation.Nullable;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import de.xavaro.android.gui.plugin.GUIPluginTitle;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIDialogViewPincode;
import de.xavaro.android.gui.wizzards.GUILocationsWizzard;
import de.xavaro.android.gui.wizzards.GUICamerasWizzard;
import de.xavaro.android.gui.wizzards.GUIChannelWizzard;
import de.xavaro.android.gui.wizzards.GUIGeomapWizzard;
import de.xavaro.android.gui.wizzards.GUICameraWizzard;
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
import pub.android.interfaces.ext.OnSpeechHandler;

public class GUIDesktopActivity extends GUIActivity implements OnSpeechHandler

{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    private GUIToastBar toastBar;

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
        wizzards.put(GUISetupWizzard.     class.getSimpleName(), new GUISetupWizzard     (this));
        wizzards.put(GUISettingsWizzard.  class.getSimpleName(), new GUISettingsWizzard  (this));
        wizzards.put(GUICameraWizzard.    class.getSimpleName(), new GUICameraWizzard    (this));
        wizzards.put(GUICamerasWizzard.   class.getSimpleName(), new GUICamerasWizzard   (this));
        wizzards.put(GUIChannelWizzard.   class.getSimpleName(), new GUIChannelWizzard   (this));
        wizzards.put(GUILocationsWizzard. class.getSimpleName(), new GUILocationsWizzard (this));
        wizzards.put(GUIGeomapWizzard.    class.getSimpleName(), new GUIGeomapWizzard    (this));
        wizzards.put(GUIStreetviewWizzard.class.getSimpleName(), new GUIStreetviewWizzard(this));

        // @formatter:on

        if (!Simple.isPhone())
        {
            displayWizzard(GUISetupWizzard.class.getSimpleName());
            //displayWizzard(GUIPingWizzard.class.getSimpleName());
        }

        //GUIDialogViewPincode pincode = new GUIDialogViewPincode(this);
        //topframe.addView(pincode);

        checkWindowSize();
   }

   public Map<String,GUIPlugin> getWizzards()
   {
        return wizzards;
   }

   @Nullable
   private GUIPlugin getWizzard(String wizzard)
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

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
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

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
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

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
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

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
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
        if (show)
        {
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
        toastBar.onSpeechResults(speech);
    }
}