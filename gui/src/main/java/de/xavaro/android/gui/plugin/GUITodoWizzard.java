package de.xavaro.android.gui.plugin;

import android.content.Context;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIPluginTitle;

public class GUITodoWizzard extends GUIPluginTitle
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Todo-Liste");
    }
}
