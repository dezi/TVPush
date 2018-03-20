package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Todo-Liste");

        Simple.getHandler().post(makeTodoList);
    }

    private final Runnable makeTodoList = new Runnable()
    {
        @Override
        public void run()
        {
            listView.removeAllViews();

            GUIPermissionWizzard.collectEntriesTodo(listView);

            Simple.getHandler().postDelayed(makeTodoList, 10 * 1000);
        }
    };

    private OnClickListener onServiceStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String service = (String) view.getTag();
            GUISetup.startIntentForService(view.getContext(), service);
        }
    };

    private OnClickListener onAreaPermissionClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String area = (String) view.getTag();
            GUISetup.requestPermission((Activity) view.getContext(), area, 4711);
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

                Simple.getHandler().removeCallbacks(makeTodoList);
                Simple.getHandler().post(makeTodoList);
            }
            else
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: boo=" + permissions[ 0 ]);
            }
        }
    }
}
