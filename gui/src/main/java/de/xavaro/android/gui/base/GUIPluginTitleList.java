package de.xavaro.android.gui.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.LinearLayout;

import de.xavaro.android.gui.simple.Simple;
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

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        Simple.getHandler().post(makeEntryList);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        Simple.getHandler().removeCallbacks(makeEntryList);

        listView.removeAllViews();
    }

    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        Log.d(LOGTAG, "onCollectEntries: Stub!");
    }

    public void updateContent()
    {
        Simple.getHandler().removeCallbacks(makeEntryList);
        Simple.getHandler().post(makeEntryList);
    }

    private final Runnable makeEntryList = new Runnable()
    {
        @Override
        public void run()
        {
            listView.removeAllViews();

            onCollectEntries(listView, false);

            Simple.getHandler().postDelayed(makeEntryList, 10 * 1000);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if ((requestCode == 4711) && (permissions.length > 0) && (grantResults.length > 0))
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: yep=" + permissions[ 0 ]);

                Simple.getHandler().removeCallbacks(makeEntryList);
                Simple.getHandler().post(makeEntryList);
            }
            else
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: boo=" + permissions[ 0 ]);
            }
        }
    }
}
