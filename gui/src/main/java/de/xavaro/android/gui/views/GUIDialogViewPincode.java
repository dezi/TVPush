package de.xavaro.android.gui.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;

public class GUIDialogViewPincode extends GUIDialogView
{
    private static final String LOGTAG = GUIDialogViewPincode.class.getSimpleName();

    private GUIEditText pin1;
    private GUIEditText pin2;
    private GUIEditText pin3;
    private GUIEditText pin4;

    public GUIDialogViewPincode(Context context)
    {
        super(context);

        setTitleText("Pin-Code");

        setPositiveButton(R.string.basic_ok, new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        setNegativeButton(R.string.basic_cancel);

        GUILinearLayout numberBox = new GUILinearLayout(context);
        numberBox.setOrientation(LinearLayout.HORIZONTAL);
        numberBox.setSizeDip(Simple.WC, Simple.WC);
        numberBox.setMarginTopDip(GUIDefs.PADDING_MEDIUM);
        numberBox.setMarginBottomDip(GUIDefs.PADDING_SMALL);

        pin1 = createPin();
        numberBox.addView(pin1);

        pin2 = createPin();
        numberBox.addView(pin2);

        pin3 = createPin();
        numberBox.addView(pin3);

        pin4 = createPin();
        numberBox.addView(pin4);

        setCustomView(numberBox);

        pin1.requestFocus();

        Simple.getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Simple.showSoftKeyBoard(pin1);
            }
        }, 250);
    }

    private GUIEditText createPin()
    {
        final GUIEditText pin = new GUIEditText(getContext());

        pin.setMinWidth(Simple.dipToPx(GUIDefs.PADDING_LARGE));
        pin.setMarginLeftDip(GUIDefs.PADDING_MEDIUM);
        pin.setMarginRightDip(GUIDefs.PADDING_MEDIUM);
        pin.setGravity(Gravity.CENTER_HORIZONTAL);
        pin.setPaddingDip(GUIDefs.PADDING_SMALL);
        pin.setBackgroundColor(Color.WHITE);
        pin.setTypeNumber();

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);
        pin.setFilters(filterArray);

        pin.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable str)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count)
            {
                Log.d(LOGTAG, "onTextChanged: str=" + str);

                if (str.length() == 1)
                {
                    if (pin == pin1) { pin2.selectAll(); pin2.requestFocus(); }
                    if (pin == pin2) { pin3.selectAll(); pin3.requestFocus(); }
                    if (pin == pin3) { pin4.selectAll(); pin4.requestFocus(); }

                    if (pin == pin4)
                    {
                        pin1.selectAll();
                        pin4.clearFocus();
                        Simple.hideSoftKeyBoard(pin4);
                    }
                }
            }
        });

        return pin;
    }
}