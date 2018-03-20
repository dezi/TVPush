package de.xavaro.android.gui.skills;

import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIEditText;
import de.xavaro.android.gui.wizzards.GUILocationWizzard;

public class GUICanFocusDelegate
{
    private final static String LOGTAG = GUICanFocusDelegate.class.getSimpleName();

    public final static View.OnFocusChangeListener genericOnFocusChangeListener = new View.OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;
            if (gf == null) return;

            if (hasFocus)
            {
                //
                // Dismiss any keyboard.
                //

                Simple.hideSoftKeyBoard(view);

                //
                // Display focus frame around image.
                //

                gf.saveBackground();

                gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS);

                gf.setHasFocus(true);

                if (gf.getToast() != null)
                {
                    GUI.instance.desktopActivity.displayToastMessage(gf.getToast(), 10, false);
                }
            }
            else
            {
                //
                // Make neutral again.
                //

                gf.restoreBackground();

                gf.setHasFocus(false);
                gf.setHighlight(false);

                if (gf.getHighlightable() && (view instanceof GUIEditText))
                {
                    GUIEditText et = (GUIEditText) view;

                    et.setEnabled(false);
                    et.setInputType(InputType.TYPE_NULL);
                }
            }
        }
    };

    public static void adjustHighlightState(View view)
    {
        GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;

        if ((gf != null)
                && gf.getHasFocus()
                && gf.getHighlightable())
        {
            if (gf.getHighlight())
            {
                gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS_HIGHLIGHT);

                gf.onHighlightStarted(view);
            }
            else
            {
                gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS);

                gf.onHighlightFinished(view);
            }
        }
    }

    public static void setupFocusChange(View view, boolean focusable)
    {
        if (Simple.isTV() && (view instanceof GUICanFocus))
        {
            if (focusable)
            {
                int padneed = Simple.dipToPx(2);

                int padleft = view.getPaddingLeft();
                int padtop = view.getPaddingTop();
                int padright = view.getPaddingRight();
                int padbottom = view.getPaddingBottom();

                if (padleft < padneed) padleft = padneed;
                if (padtop < padneed) padtop = padneed;
                if (padright < padneed) padright = padneed;
                if (padbottom < padneed) padbottom = padneed;

                view.setPadding(padleft, padtop, padright, padbottom);

                view.setOnFocusChangeListener(genericOnFocusChangeListener);
            }
            else
            {
                view.setOnFocusChangeListener(null);
            }
        }
    }

    public static boolean onKeyDown(View view, int keyCode, KeyEvent event)
    {
        GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;

        if ((gf != null)
                && gf.getHasFocus()
                && gf.getHighlightable()
                && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
            gf.setHighlight(! gf.getHighlight());

            Log.d(LOGTAG, "onKeyDown: highlight=" + gf.getHighlight());

            if (view instanceof GUIEditText)
            {
                GUIEditText et = (GUIEditText) view;

                if (gf.getHighlight())
                {
                    et.setEnabled(true);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    et.setEnabled(false);
                    et.setInputType(InputType.TYPE_NULL);
                }
            }

            return true;
        }

        return false;
    }
}
