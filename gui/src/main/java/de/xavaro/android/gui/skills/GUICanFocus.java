package de.xavaro.android.gui.skills;

import android.view.View;

public interface GUICanFocus extends GUICanRoundedCorners
{
    void setFocusable(boolean focusable);

    void setHighlight(boolean highlight);
    boolean getHighlight();

    void setHasFocus(boolean hasfocus);
    boolean getHasFocus();

    int getBackgroundColor();

    View.OnFocusChangeListener getOnFocusChangeListener();
    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
