package de.xavaro.android.gui.views;

import android.content.Context;

import de.xavaro.android.gui.simple.Simple;

public class GUIDialogButtonView extends GUIButtonView
{
    public GUIDialogButtonView(Context context)
    {
        super(context);

        //
        // We assume two buttons in dialog.
        // If there is only one, the weight
        // is meaningsless.
        //

        setSizeDip(Simple.MP, Simple.WC, 0.5f);
    }
}
