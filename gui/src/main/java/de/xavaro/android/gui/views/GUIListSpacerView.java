package de.xavaro.android.gui.views;

import android.content.Context;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;

public class GUIListSpacerView extends GUIRelativeLayout
{
    public GUIListSpacerView(Context context)
    {
        super(context);

        setSizeDip(Simple.MP, GUIDefs.PADDING_SMALL);
    }
}
