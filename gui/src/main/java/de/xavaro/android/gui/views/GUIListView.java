package de.xavaro.android.gui.views;

import android.widget.LinearLayout;
import android.content.Context;
import android.view.View;
import android.util.Log;

public class GUIListView extends  GUILinearLayout
{
    private final static String LOGTAG = GUIListView.class.getSimpleName();

    public GUIListView(Context context)
    {
        super(context);

        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
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
                ((GUIListEntry) child).isinuse = true;

                return (GUIListEntry) child;
            }
        }

        GUIListEntry entry = new GUIListEntry(getContext());
        entry.isinuse = true;
        entry.idtag = idtag;

        addView(entry);

        return entry;
    }

    public GUIListEntryIOT findGUIListEntryIOTOrCreate(String uuid)
    {
        GUIListEntryIOT entry;

        for (int inx = 0; inx < getChildCount(); inx++)
        {
            View child = getChildAt(inx);

            if (child instanceof GUIListEntryIOT)
            {
                entry = (GUIListEntryIOT) child;

                if ((entry.uuid != null) && entry.uuid.equals(uuid))
                {
                    entry.updateContent();
                    entry.isinuse = true;

                    return entry;
                }
            }
        }

        entry = new GUIListEntryIOT(getContext(), uuid);
        entry.setOnFocusChangeListenerCustom(onFocusChangeListener);
        entry.updateContent();

        addView(entry);

        return entry;
    }

    private final OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            if (view instanceof GUIListEntryIOT)
            {
                onSelectionChanged((GUIListEntryIOT) view, hasFocus);
            }
            else
            {
                if (view instanceof GUIListEntry)
                {
                    onSelectionChanged((GUIListEntry) view, hasFocus);
                }
            }
        }
    };

    public void onSelectionChanged(GUIListEntry entry, boolean selected)
    {
        Log.d(LOGTAG, "onSelectionChanged: entry=" + entry.idtag + " selected=" + selected);
    }

    public void onSelectionChanged(GUIListEntryIOT entry, boolean selected)
    {
        Log.d(LOGTAG, "onSelectionChanged: entry=" + entry.uuid + " selected=" + selected);
    }
}