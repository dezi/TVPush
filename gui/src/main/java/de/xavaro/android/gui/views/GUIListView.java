package de.xavaro.android.gui.views;

import android.content.Context;
import android.view.View;

public class GUIListView extends  GUILinearLayout
{
    public GUIListView(Context context)
    {
        super(context);
    }

    private int focusedIndex = 0;

    @Override
    public void removeAllViews()
    {
        //
        // Inspect childs and search for focused.
        //

        focusedIndex = -1;

        for (int inx = 0; inx < getChildCount(); inx++)
        {
            if (getChildAt(inx).hasFocus())
            {
                focusedIndex = inx;
                break;
            }
        }

        super.removeAllViews();
    }

    @Override
    public void addView(View view)
    {
        if (getChildCount() > 0)
        {
            super.addView(new GUIListSpacerView(getContext()));
        }

        super.addView(view);

        if (focusedIndex == (getChildCount() - 1))
        {
            view.requestFocus();
        }
    }
}