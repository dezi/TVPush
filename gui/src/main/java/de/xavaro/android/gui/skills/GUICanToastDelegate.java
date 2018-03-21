package de.xavaro.android.gui.skills;

import de.xavaro.android.gui.base.GUI;

public class GUICanToastDelegate
{
    public static void displayToast(String toast)
    {
        if (toast != null)
        {
            GUI.instance.desktopActivity.displayToastMessage(toast, 10, false);
        }
    }
}
