package de.xavaro.android.gui.skills;

import android.view.View;

public interface GUICanFocus extends GUICanRoundedCorners
{
    void setFocusable(boolean focusable);

    View.OnFocusChangeListener getOnFocusChangeListener();
    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
