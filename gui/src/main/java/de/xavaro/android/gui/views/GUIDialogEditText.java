package de.xavaro.android.gui.views;

import android.content.Context;

import de.xavaro.android.gui.simple.Simple;

public class GUIDialogEditText extends GUIEditText
{
    public GUIDialogEditText(Context context)
    {
        super(context);

        setMinEms(Simple.isTablet() ? 12 : 9);
    }
}
