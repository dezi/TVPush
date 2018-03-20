package de.xavaro.android.gui.views;

import android.content.Context;
import android.widget.ImageView;

import de.xavaro.android.gui.base.GUIDefs;

public class GUIIconView extends GUIImageView
{
    public GUIIconView(Context context)
    {
        super(context);

        setScaleType(ImageView.ScaleType.FIT_XY);
        setSizeDip(GUIDefs.ICON_SIZE, GUIDefs.ICON_SIZE);
        setPaddingDip(GUIDefs.ICON_PADD);
    }
}
