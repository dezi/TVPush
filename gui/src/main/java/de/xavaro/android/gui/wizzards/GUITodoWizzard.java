package de.xavaro.android.gui.wizzards;

import android.content.Context;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUIPluginTitleList;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Todo-Liste");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        GUIPermissionWizzard.collectEntries(listView, true);
        GUILocationsWizzard.collectEntries(listView, true);
    }
}
