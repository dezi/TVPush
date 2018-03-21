package de.xavaro.android.gui.base;

import android.content.Context;
import android.widget.LinearLayout;

import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.views.GUIScrollView;

public class GUIPluginTitleList extends GUIPluginTitle
{
    private final static String LOGTAG = GUIPluginTitleList.class.getSimpleName();

    public GUIScrollView scrollView;
    public GUIListView listView;

    public GUIPluginTitleList(Context context)
    {
        super(context);

        scrollView = new GUIScrollView(context);
        scrollView.setRoundedCorners(GUIDefs.ROUNDED_MEDIUM,GUIDefs.COLOR_LIGHT_TRANSPARENT);
        scrollView.setPaddingDip(GUIDefs.PADDING_SMALL);

        contentFrame.addView(scrollView);

        listView = new GUIListView(context);
        listView.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(listView);
    }
}
