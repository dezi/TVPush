package de.xavaro.android.gui.skills;

import android.view.View;

public interface GUICanFocus
{
    void setFocusable(boolean focusable);
    //boolean getFocusable();

    View.OnFocusChangeListener getOnFocusChangeListener();
    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
