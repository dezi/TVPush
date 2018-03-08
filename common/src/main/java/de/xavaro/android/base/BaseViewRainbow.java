package de.xavaro.android.base;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;
import android.graphics.Color;
import android.content.Context;
import android.view.View;

import de.xavaro.android.simple.Defs;
import de.xavaro.android.simple.Simple;
import de.xavaro.android.skills.CanRestoreBackground;

public class BaseViewRainbow extends RelativeLayout implements CanRestoreBackground
{
    private int orient;
    private boolean isActive;

    private Drawable backgroundDrawable;
    private int backgroundColor = Color.TRANSPARENT;

    private Drawable savedBackgroundDrawable;
    private int savedBackgroundColor;

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

    public BaseViewRainbow(Context context)
    {
        super(context);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);

        backgroundColor = color;
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        super.setBackground(drawable);

        backgroundDrawable = drawable;
    }

    @Override
    public void saveBackground()
    {
        savedBackgroundColor = backgroundColor;
        savedBackgroundDrawable = backgroundDrawable;
    }

    @Override
    public void restoreBackground()
    {
        if (savedBackgroundDrawable != null)
        {
            setBackground(savedBackgroundDrawable);
        }
        else
        {
            setBackgroundColor(savedBackgroundColor);
        }
    }

    private final Runnable rainbowRotate = new Runnable()
    {
        @Override
        public void run()
        {
            int next = orient++ % rainBowOrients.length;

            GradientDrawable rainbow = new GradientDrawable(rainBowOrients[ next ], rainBowColors);
            rainbow.setCornerRadius(Simple.dipToPx(Defs.ROUNDED_NORMAL));

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

            if ((child != null) && (child instanceof CanRestoreBackground))
            {
                ((CanRestoreBackground) child).saveBackground();

                child.setBackgroundColor(Color.BLACK);
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

            if ((child != null) && (child instanceof CanRestoreBackground))
            {
                ((CanRestoreBackground) child).restoreBackground();
            }

            restoreBackground();
        }
    }
}
