package de.xavaro.android.gui.views;

import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.skills.GUICanDip;
import de.xavaro.android.gui.skills.GUICanFocus;
import de.xavaro.android.gui.skills.GUICanToast;
import de.xavaro.android.gui.skills.GUICanFocusDelegate;
import de.xavaro.android.gui.skills.GUICanRestoreBackground;
import de.xavaro.android.gui.skills.GUICanRestoreBackgroundDelegate;
import de.xavaro.android.gui.skills.GUICanRoundedCorners;
import de.xavaro.android.gui.skills.GUICanRoundedCornersDelegate;

public class GUIScrollView extends ScrollView implements
        GUICanDip,
        GUICanFocus,
        GUICanToast,
        GUICanRoundedCorners,
        GUICanRestoreBackground
{
    public GUIScrollView(Context context)
    {
        super(context);

        setFocusable(false);

        initSkills();
    }

    //region CanDip implementation.

    @Override
    public void setSizeDip(int width, int height)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        getLayoutParams().width = width > 0 ? Simple.dipToPx(width) : width;
        getLayoutParams().height = height > 0 ? Simple.dipToPx(height) : height;
    }

    @Override
    public void setSizeDip(int width, int height, float weight)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        getLayoutParams().width = width > 0 ? Simple.dipToPx(width) : width;
        getLayoutParams().height = height > 0 ? Simple.dipToPx(height) : height;

        ((LinearLayout.LayoutParams) getLayoutParams()).weight = weight;
    }

    @Override
    public void setPaddingDip(int pad)
    {
        setPadding(Simple.dipToPx(pad), Simple.dipToPx(pad), Simple.dipToPx(pad), Simple.dipToPx(pad));
    }

    @Override
    public void setPaddingDip(int left, int top, int right, int bottom)
    {
        setPadding(Simple.dipToPx(left), Simple.dipToPx(top), Simple.dipToPx(right), Simple.dipToPx(bottom));
    }

    @Override
    public void setMarginLeftDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).leftMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginTopDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginRightDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((MarginLayoutParams) getLayoutParams()).rightMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginBottomDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).bottomMargin = Simple.dipToPx(margin);
    }

    //endregion CanDip implementation.

    //region Skills implementation.

    private GUICanRoundedCornersDelegate canRC;
    private GUICanRestoreBackgroundDelegate canRB;

    private void initSkills()
    {
        canRB = new GUICanRestoreBackgroundDelegate(this);
        canRC = new GUICanRoundedCornersDelegate(this);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);
        canRB.setBackgroundColor(color);
        canRC.setBackgroundColor(color);
    }

    @Override
    public int getBackgroundColor()
    {
        return canRB.getBackgroundColor();
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        super.setBackground(drawable);
        canRB.setBackground(drawable);
    }

    @Override
    public Drawable getBackground()
    {
        return canRB.getBackground();
    }

    @Override
    public void setRoundedCorners(int radius, int color)
    {
        canRC.setRoundedCorners(radius, color);
    }

    @Override
    public void setRoundedCorners(int radius, int innerColor, int strokeColor)
    {
        canRC.setRoundedCorners(radius, innerColor, strokeColor);
    }

    @Override
    public void setRoundedCornersDip(int radiusdip, int color)
    {
        canRC.setRoundedCornersDip(radiusdip, color);
    }

    @Override
    public void setRoundedCornersDip(int radiusdip, int innerColor, int strokeColor)
    {
        canRC.setRoundedCornersDip(radiusdip, innerColor, strokeColor);
    }

    @Override
    public int getRadius()
    {
        return canRC.getRadius();
    }

    @Override
    public int getRadiusDip()
    {
        return canRC.getRadiusDip();
    }

    @Override
    public int getInnerColor()
    {
        return canRC.getInnerColor();
    }

    @Override
    public int getStrokeColor()
    {
        return canRC.getStrokeColor();
    }

    @Override
    public void saveBackground()
    {
        canRB.saveBackground();
        canRC.saveBackground();
    }

    @Override
    public void restoreBackground()
    {
        canRB.restoreBackground();
        canRC.restoreBackground();
    }

    //endregion Skills implementation.

    //region CanFocus implementation.

    private boolean focus;
    private boolean focusable;
    private boolean highlight;
    private boolean highlightable;

    @Override
    public void setFocusable(boolean focusable)
    {
        this.focusable = focusable;

        super.setFocusable(focusable);

        GUICanFocusDelegate.setupOnFocusChangeListener(this, focusable);
    }

    @Override
    public boolean getIsFocusable()
    {
        return focusable;
    }

    @Override
    public void setHighlight(boolean highlight)
    {
        this.highlight = highlight;

        GUICanFocusDelegate.adjustHighlightState(this);
    }

    @Override
    public boolean getHighlight()
    {
        return this.highlight;
    }

    @Override
    public void setHighlightable(boolean highlightable)
    {
        this.highlightable = highlightable;
    }

    @Override
    public boolean getHighlightable()
    {
        return this.highlightable;
    }

    @Override
    public void setHasFocus(boolean hasfocus)
    {
        this.focus = hasfocus;
    }

    @Override
    public boolean getHasFocus()
    {
        return this.focus;
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        super.setOnClickListener(onClickListener);
    }

    public void onHighlightChanged(View view, boolean highlight)
    {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return GUICanFocusDelegate.onKeyDown(this, keyCode, event) || super.onKeyDown(keyCode, event);
    }

    //endregion CanFocus implementation.

    //region CanToast implementation.

    private String toastFocus;
    private String toastHighlight;

    @Override
    public void setToastFocus(String toast)
    {
        this.toastFocus = toast;
    }

    @Override
    public String getToastFocus()
    {
        return toastFocus;
    }

    @Override
    public void setToastHighlight(String toast)
    {
        this.toastHighlight = toast;
    }

    @Override
    public String getToastHighlight()
    {
        return toastHighlight;
    }

    //endregion CanToast implementation
}
