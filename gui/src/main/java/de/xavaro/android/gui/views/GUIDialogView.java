package de.xavaro.android.gui.views;

import android.widget.LinearLayout;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Color;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.View;
import android.os.Build;
import android.util.Log;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.base.GUIApplication;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;

public class GUIDialogView extends GUIRelativeLayout
{
    private static final String LOGTAG = GUIDialogView.class.getSimpleName();

    protected final static int padSize = Simple.isTablet() ? GUIDefs.PADDING_XLARGE : GUIDefs.PADDING_NORMAL;

    public static void errorAlert(final ViewGroup rootView, int titleres, int msgres)
    {
        errorAlert(rootView, titleres, msgres, null);
    }

    public static void errorAlert(final ViewGroup rootView, int titleres, int msgres, View.OnClickListener onokclick)
    {
        errorAlert(rootView, titleres, rootView.getContext().getResources().getString(msgres), onokclick);
    }

    public static void errorAlert(final ViewGroup rootView, int titleres, String msgstr)
    {
        errorAlert(rootView, titleres, msgstr, null);
    }

    public static void errorAlert(final ViewGroup rootView, int titleres, String msgstr, View.OnClickListener onokclick)
    {
        final GUIDialogView dialogView = new GUIDialogView(rootView.getContext());

        dialogView.marginView.setRoundedCorners(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT, Color.RED);

        dialogView.setTitleText(titleres);
        dialogView.setInfoText(msgstr);

        dialogView.setPositiveButton(R.string.basic_ok, onokclick);

        if (Simple.isUIThread())
        {
            rootView.addView(dialogView);
        }
        else
        {
            Simple.getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    rootView.addView(dialogView);
                }
            });
        }
    }

    public static void yesnoAlert(final ViewGroup rootView, int titleres, String msgstr,
                                  View.OnClickListener onyesclick,
                                  View.OnClickListener onnoclick)
    {
        final GUIDialogView dialogView = new GUIDialogView(rootView.getContext());

        dialogView.setCloseButton(true, onnoclick);

        dialogView.marginView.setRoundedCorners(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_TRANSPARENT, Color.BLACK);

        dialogView.setTitleText(titleres);
        dialogView.setInfoText(msgstr);

        dialogView.setPositiveButton(R.string.basic_ok, onyesclick);
        dialogView.setNegativeButton(R.string.basic_cancel, onnoclick);

        if (Simple.isUIThread())
        {
            rootView.addView(dialogView);
        }
        else
        {
            Simple.getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    rootView.addView(dialogView);
                }
            });
        }
    }

    public GUIDialogButtonView positiveButton;
    public GUIDialogButtonView negativeButton;

    protected GUIRelativeLayout marginView;
    protected GUIImageView closeButton;
    protected GUILinearLayout padView;
    protected GUITextView titleView;
    protected GUITextView infoView;
    protected GUIRelativeLayout customView;
    protected GUILinearLayout buttonFrame;

    protected OnClickListener positiveButtonOnClick;
    protected OnClickListener negativeButtonOnClick;
    protected OnClickListener closeButtonOnClick;

    public GUIDialogView(Context context)
    {
        super(context);

        Activity activity = GUIApplication.getCurrentActivity(getContext());

        if (activity instanceof GUIActivity)
        {
            Log.d(LOGTAG, "Constructor: saveFocusState.");

            ((GUIActivity) activity).saveFocusState();
        }

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ((negativeButton.getVisibility() == GONE) &&
                        (positiveButton.getVisibility() == GONE) &&
                        (closeButton.getVisibility() != GONE))
                {
                    dismissDialog();
                }
            }
        });

        setFocusable(false);
        setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
        setBackgroundColor(GUIDefs.COLOR_BACKGROUND_DIM);
        setSizeDip(Simple.MP, Simple.MP);

        marginView = new GUIRelativeLayout(context);
        marginView.setSizeDip(Simple.WC, Simple.WC);
        marginView.setPaddingDip(GUIDefs.PADDING_TINY);
        marginView.setRoundedCorners(GUIDefs.ROUNDED_MEDIUM, GUIDefs.COLOR_LIGHT_GRAY, Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            marginView.setElevation(Simple.dipToPx(20));
        }

        addView(marginView);

        GUILinearLayout boxView = new GUILinearLayout(context);
        boxView.setOrientation(LinearLayout.VERTICAL);
        boxView.setSizeDip(Simple.WC, Simple.WC);

        marginView.addView(boxView);

        GUIRelativeLayout closeButtonBox = new GUIRelativeLayout(context);
        closeButtonBox.setGravity(Gravity.END);
        closeButtonBox.setSizeDip(Simple.MP, Simple.WC);

        boxView.addView(closeButtonBox);

        closeButton = new GUIImageView(context);
        closeButton.setVisibility(GONE);
        closeButton.setScaleType(ImageView.ScaleType.FIT_XY);
        closeButton.setSizeDip(30, 30);
        closeButton.setPaddingDip(GUIDefs.PADDING_SMALL);
        closeButton.setImageResource(R.drawable.dialog_close_28);

        closeButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismissDialog();

                if (closeButtonOnClick != null)
                {
                    closeButtonOnClick.onClick(GUIDialogView.this);
                }
            }
        });

        closeButtonBox.addView(closeButton);

        padView = new GUILinearLayout(context);
        padView.setOrientation(LinearLayout.VERTICAL);
        padView.setSizeDip(Simple.WC, Simple.WC);
        padView.setPaddingDip(padSize);

        boxView.addView(padView);

        titleView = new GUITextView(context);
        titleView.setSingleLine(true);
        titleView.setVisibility(GONE);
        titleView.setSizeDip(Simple.WC, Simple.WC);
        titleView.setTextSizeDip(GUIDefs.FONTSIZE_TITLE);
        titleView.setGravity(Gravity.START);

        padView.addView(titleView);

        infoView = new GUITextView(context);
        infoView.setVisibility(GONE);
        infoView.setMinLines(2);
        infoView.setMinEms(GUIDefs.MIN_EMS_DIALOGS);
        infoView.setMaxEms(GUIDefs.MAX_EMS_DIALOGS);
        infoView.setSizeDip(Simple.WC, Simple.WC);
        infoView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);
        infoView.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        infoView.setMarginTopDip(GUIDefs.PADDING_SMALL);

        padView.addView(infoView);

        customView = new GUIRelativeLayout(context);
        customView.setVisibility(GONE);
        customView.setSizeDip(Simple.WC, Simple.WC, 1.0f);

        padView.addView(customView);

        buttonFrame = new GUILinearLayout(context);
        buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
        buttonFrame.setSizeDip(Simple.MP, Simple.WC);
        buttonFrame.setMarginTopDip(GUIDefs.PADDING_MEDIUM);

        padView.addView(buttonFrame);

        negativeButton = new GUIDialogButtonView(context);
        negativeButton.setVisibility(GONE);

        negativeButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismissDialog();

                if (negativeButtonOnClick != null)
                {
                    negativeButtonOnClick.onClick(GUIDialogView.this);
                }
            }
        });

        buttonFrame.addView(negativeButton);

        positiveButton = new GUIDialogButtonView(context);
        positiveButton.setDefaultButton(true);
        positiveButton.setVisibility(GONE);

        positiveButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismissDialog();

                if (positiveButtonOnClick != null)
                {
                    positiveButtonOnClick.onClick(GUIDialogView.this);
                }
            }
        });

        buttonFrame.addView(positiveButton);

        setButtonsSpacing();
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        Activity activity = GUIApplication.getCurrentActivity(getContext());

        if (activity instanceof GUIActivity)
        {
            Log.d(LOGTAG, "onDetachedFromWindow: restoreFocusState.");

            ((GUIActivity) activity).restoreFocusState();
        }
    }

    public boolean onBackPressed()
    {
        dismissDialog();

        return true;
    }

    public void setButtonsSpacing()
    {
        if ((positiveButton.getVisibility() == VISIBLE) && (negativeButton.getVisibility() == VISIBLE))
        {
            if (Simple.isPhone())
            {
                buttonFrame.setOrientation(LinearLayout.VERTICAL);

                negativeButton.setMarginRightDip(GUIDefs.PADDING_ZERO);
                negativeButton.setMarginBottomDip(GUIDefs.PADDING_MEDIUM);

                positiveButton.setMarginLeftDip(GUIDefs.PADDING_ZERO);
                positiveButton.setMarginTopDip(GUIDefs.PADDING_MEDIUM);
            }
            else
            {
                buttonFrame.setOrientation(LinearLayout.HORIZONTAL);

                negativeButton.setMarginRightDip(GUIDefs.PADDING_MEDIUM);
                negativeButton.setMarginBottomDip(GUIDefs.PADDING_ZERO);

                positiveButton.setMarginLeftDip(GUIDefs.PADDING_MEDIUM);
                positiveButton.setMarginTopDip(GUIDefs.PADDING_ZERO);

            }
        }
        else
        {
            negativeButton.setMarginRightDip(GUIDefs.PADDING_ZERO);
            negativeButton.setMarginBottomDip(GUIDefs.PADDING_ZERO);

            positiveButton.setMarginLeftDip(GUIDefs.PADDING_ZERO);
            positiveButton.setMarginTopDip(GUIDefs.PADDING_ZERO);
        }
    }

    public void setTitleText(int resid)
    {
        if (resid > 0)
        {
            titleView.setText(resid);
            titleView.setVisibility(VISIBLE);
        }
    }

    public void setTitleText(String title)
    {
        titleView.setText(title);
        titleView.setVisibility(VISIBLE);
    }

    public void setInfoText(int resid)
    {
        if (resid > 0)
        {
            infoView.setText(resid);
            infoView.setVisibility(VISIBLE);
        }
    }

    public void setInfoText(String info)
    {
        info = info.replaceAll("\n *", "\n");

        infoView.setText(info);
        infoView.setVisibility(VISIBLE);
    }

    public void setCustomView(View view)
    {
        customView.addView(view);
        customView.setVisibility(VISIBLE);
    }

    public void setPositiveButton(int resid)
    {
        setPositiveButton(resid, null);
    }

    public void setPositiveButton(int resid, OnClickListener onClickListener)
    {
        positiveButton.setText(resid);
        positiveButton.setFocusable(true);
        positiveButton.setVisibility(VISIBLE);
        positiveButtonOnClick = onClickListener;

        setButtonsSpacing();
    }

    public void setNegativeButton(int resid)
    {
        setNegativeButton(resid, null);
    }

    public void setNegativeButton(int resid, OnClickListener onClickListener)
    {
        negativeButton.setText(resid);
        negativeButton.setFocusable(true);
        negativeButton.setVisibility(VISIBLE);
        negativeButtonOnClick = onClickListener;

        setButtonsSpacing();
    }

    public void setCloseButton(boolean enable)
    {
        setCloseButton(enable, null);
    }

    public void setCloseButton(boolean enable, OnClickListener onClickListener)
    {
        if (enable)
        {
            padView.setPaddingDip(padSize, 0, padSize, padSize);
        }
        else
        {
            padView.setPaddingDip(padSize);
        }

        closeButton.setVisibility(enable ? VISIBLE : GONE);
        closeButtonOnClick = onClickListener;
    }

    public ViewGroup dismissDialog()
    {
        ViewGroup parent = (ViewGroup) getParent();

        if (parent != null)
        {
            parent.removeView(this);
        }

        return parent;
    }
}
