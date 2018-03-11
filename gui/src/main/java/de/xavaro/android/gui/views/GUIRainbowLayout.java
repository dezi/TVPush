package de.xavaro.android.gui.views;

import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.content.Context;
import android.view.View;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.skills.GUICanRoundedCorners;

public class GUIRainbowLayout extends GUIRelativeLayout
{
    private boolean isActive;
    private int orient;

    private int rainBowRadiusDip = GUIDefs.ROUNDED_NORMAL;

    private int[] rainBowColors = new int[]
            {
                    Color.RED, Color.MAGENTA, Color.BLUE,
                    Color.CYAN, Color.GREEN, Color.YELLOW
            };

    private GradientDrawable.Orientation[] rainBowOrients = new GradientDrawable.Orientation[]
            {
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    GradientDrawable.Orientation.TR_BL,
                    GradientDrawable.Orientation.RIGHT_LEFT ,
                    GradientDrawable.Orientation.BR_TL,
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    GradientDrawable.Orientation.BL_TR,
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    GradientDrawable.Orientation.TL_BR
            };

    public GUIRainbowLayout(Context context)
    {
        super(context);
    }

    private final Runnable rainbowRotate = new Runnable()
    {
        @Override
        public void run()
        {
            int next = orient++ % rainBowOrients.length;

            GradientDrawable rainbow = new GradientDrawable(rainBowOrients[ next ], rainBowColors);
            rainbow.setCornerRadius(Simple.dipToPx(rainBowRadiusDip));

            setBackground(rainbow);

            getHandler().postDelayed(this, 100);
        }
    };

    public void start()
    {
        if (! isActive)
        {
            isActive = true;

            saveBackground();

            View child = getChildAt(0);

            if (child instanceof GUICanRoundedCorners)
            {
                GUICanRoundedCorners canRC = (GUICanRoundedCorners) child;

                canRC.saveBackground();

                rainBowRadiusDip = Math.round(canRC.getRadiusDip() * 0.5f);

                int innerColor = canRC.getInnerColor();
                int newColor = Simple.setRGBAlpha(innerColor, 0xff);

                canRC.setRoundedCornersDip(rainBowRadiusDip, newColor);
            }

            rainbowRotate.run();
        }
    }

    public void stop()
    {
        if (isActive)
        {
            isActive = false;

            getHandler().removeCallbacks(rainbowRotate);

            View child = getChildAt(0);

            if (child instanceof GUICanRoundedCorners)
            {
                ((GUICanRoundedCorners) child).restoreBackground();
            }

            restoreBackground();
        }
    }
}
