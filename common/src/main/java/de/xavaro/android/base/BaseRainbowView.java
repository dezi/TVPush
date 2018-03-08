package de.xavaro.android.base;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.content.Context;

public class BaseRainbowView extends RelativeLayout
{
    private int orient;
    private boolean isActive;

    private int backgroundColor;
    private Drawable backgroundDrawable;

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

    public BaseRainbowView(Context context)
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

    private final Runnable rainbowRotate = new Runnable()
    {
        @Override
        public void run()
        {
            int next = orient++ % rainBowOrients.length;
            GradientDrawable rainbow = new GradientDrawable(rainBowOrients[ next ], rainBowColors);
            setBackground(rainbow);

            getHandler().postDelayed(this, 100);
        }
    };

    public void start()
    {
        if (! isActive)
        {
            isActive = true;

            rainbowRotate.run();
        }
    }

    public void stop()
    {
        isActive = false;

        getHandler().removeCallbacks(rainbowRotate);

        setBackground(null);
        setBackgroundColor(backgroundColor);
    }
}
