package de.xavaro.android.base;

import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.content.Context;
import android.view.View;

import de.xavaro.android.simple.Defs;
import de.xavaro.android.simple.Simple;
import de.xavaro.android.skills.CanRoundedCorners;

public class BaseRainbowLayout extends BaseRelativeLayout
{
    private int orient;
    private boolean isActive;

    private int rainBowRadiusDip = Defs.ROUNDED_NORMAL;

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

    public BaseRainbowLayout(Context context)
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

            if (child instanceof CanRoundedCorners)
            {
                CanRoundedCorners canRC = (CanRoundedCorners) child;

                canRC.saveBackground();

                rainBowRadiusDip = Math.round(canRC.getRadiusDip() * 0.75f);

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

            if (child instanceof CanRoundedCorners)
            {
                ((CanRoundedCorners) child).restoreBackground();
            }

            restoreBackground();
        }
    }
}
