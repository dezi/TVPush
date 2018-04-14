package de.xavaro.android.gui.wizzards;

import android.content.Context;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.plugin.GUIPluginTitleList;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setWizzard(true, false);

        setTitleIcon(R.drawable.todo_list_512);
        setNameText("Todo-Liste");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        (new GUISetupWizzard(listView.getContext())).collectEntries(listView, true);
        (new GUIGeoposWizzard(listView.getContext())).collectEntries(listView, true);
    }
}
