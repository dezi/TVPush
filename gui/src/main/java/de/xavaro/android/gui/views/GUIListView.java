package de.xavaro.android.gui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

public class GUIListView extends  GUILinearLayout
{
    public GUIListView(Context context)
    {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
    }

    private int focusedIndex;
    private boolean notfirst;

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        notfirst = false;
    }

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

    public void markAllViewsUnused()
    {
        for (int inx = 0; inx < getChildCount(); inx++)
        {
            View child = getChildAt(inx);

            if (child instanceof GUIListEntry)
            {
                ((GUIListEntry) child).isinuse = false;
            }
        }
    }

    public void removeAllUnusedViews()
    {
        for (int inx = 0; inx < getChildCount(); inx++)
        {
            View child = getChildAt(inx);

            if (child instanceof GUIListEntry)
            {
                if (! ((GUIListEntry) child).isinuse)
                {
                    removeView(child);
                    inx--;
                }
            }
        }
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

    public GUIListEntry findGUIListEntryOrCreate(String idtag)
    {
        for (int inx = 0; inx < getChildCount(); inx++)
        {
            View child = getChildAt(inx);

            if ((child instanceof GUIListEntry)
                    && (((GUIListEntry) child).idtag != null)
                    && (((GUIListEntry) child).idtag.equals(idtag)))
            {
                return (GUIListEntry) child;
            }
        }

        GUIListEntry entry = new GUIListEntry(getContext());
        addView(entry);

        return entry;
    }
}