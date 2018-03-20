package de.xavaro.android.gui.skills;

import android.view.KeyEvent;
import android.view.View;

public interface GUICanFocus extends GUICanRoundedCorners
{
    void setFocusable(boolean focusable);

    void setHighlight(boolean highlight);
    boolean getHighlight();

    void setHighlightable(boolean highlighttable);
    boolean getHighlightable();

    void setHasFocus(boolean hasfocus);
    boolean getHasFocus();

    int getBackgroundColor();

    void setToast(String toast);
    String getToast();

    void onHighlightStarted(View view);
    void onHighlightFinished(View view);

    View.OnFocusChangeListener getOnFocusChangeListener();
    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
