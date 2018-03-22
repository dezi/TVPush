package de.xavaro.android.gui.views;

import android.content.Context;
import android.view.View;

public class GUIListView extends  GUILinearLayout
{
    public GUIListView(Context context)
    {
        super(context);
    }

    private int focusedIndex;
    private boolean notfirst;

    @Override
    public void removeAllViews()
    {
        //
        // Inspect childs and search for focused.
        //

        if (notfirst)
        {
            focusedIndex = -1;
        }
        else
        {
            focusedIndex = 0;
            notfirst = true;
        }

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