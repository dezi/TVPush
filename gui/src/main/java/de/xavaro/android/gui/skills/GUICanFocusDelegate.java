package de.xavaro.android.gui.skills;

import android.view.View;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;

public class GUICanFocusDelegate
{
    public final static View.OnFocusChangeListener genericOnFocusChangeListener = new View.OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;

            if (hasFocus)
            {
                //
                // Dismiss any keyboard.
                //

                Simple.hideSoftKeyBoard(view);

                //
                // Display focus frame around image.
                //

                if (gf != null)
                {
                    gf.saveBackground();

                    gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS);
                }
            }
            else
            {
                //
                // Make neutral again.
                //

                if (gf != null)
                {
                    gf.restoreBackground();
                }
            }
        }
    };

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
}
