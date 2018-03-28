package de.xavaro.android.gui.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.util.Log;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.iot.base.IOTAlive;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import de.xavaro.android.gui.simple.Simple;

import pub.android.interfaces.pub.PUBCamera;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;

public class GUIListEntryIOT extends GUIListEntry
{
    private final static String LOGTAG = GUIListEntryIOT.class.getSimpleName();

    public String uuid;

    public GUIRelativeLayout bulletView;

    public IOTDevice device;
    public IOTStatus status;
    public IOTCredential credential;

    private View.OnClickListener onClickListener;
    private OnUpdateContentListener onUpdateContentListener;

    public GUIListEntryIOT(Context context)
    {
        super(context);

        GUIRelativeLayout statusBox = new GUIRelativeLayout(context);
        statusBox.setGravity(Gravity.CENTER);
        statusBox.setSizeDip(Simple.WC, Simple.MP);
        statusBox.setPaddingDip(GUIDefs.PADDING_TINY);

        addView(statusBox);

        bulletView = new GUIRelativeLayout(context);
        bulletView.setSizeDip(GUIDefs.PADDING_MEDIUM,GUIDefs.PADDING_MEDIUM);

        statusBox.addView(bulletView);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        IOTDevices.instance.subscribe(device.uuid, onDeviceUpdated);
        IOTStatusses.instance.subscribe(device.uuid, onStatusUpdated);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTDevices.instance.unsubscribe(device.uuid, onDeviceUpdated);
        IOTStatusses.instance.unsubscribe(device.uuid, onStatusUpdated);
    }

    public void setStatusColor(int color)
    {
        bulletView.setRoundedCornersDip(GUIDefs.PADDING_MEDIUM / 2, color);
    }

    public void updateContent()
    {
        iconView.setIOTObject(device);

        if (device.type.equals("smartbulb"))
        {
            if (onClickListener == null)
            {
                setOnClickListener(onSmartBulbClickListener);
            }
        }

        if (device.type.equals("smartplug"))
        {
            if (onClickListener == null)
            {
                setOnClickListener(onSmartPlugClickListener);
            }
        }

        if (device.type.equals("camera"))
        {
            if (onClickListener == null)
            {
                setOnClickListener(onCameraClickListener);
            }
        }

        headerViev.setText(device.name);

        Long lastPing = IOTAlive.getAliveNetwork(uuid);

        if (lastPing != null)
        {
            boolean pingt = (System.currentTimeMillis() - lastPing) < (60 * 1000);
            setStatusColor(pingt ? GUIDefs.STATUS_COLOR_GREEN : GUIDefs.STATUS_COLOR_RED);
        }

        if (onUpdateContentListener != null)
        {
            onUpdateContentListener.onUpdateContent(this);
        }
    }

    private final Runnable onDeviceUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "onDeviceUpdated: name=" + device.name);

            device = IOTDevices.getEntry(uuid);

            updateContent();
        }
    };

    private final Runnable onStatusUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "onStatusUpdated: name=" + device.name);

            status = IOTStatusses.getEntry(uuid);

            updateContent();
        }
    };

    private static final OnClickListener onSmartPlugClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            GUIListEntryIOT entry = (GUIListEntryIOT) view;

            entry.credential = new IOTCredential(entry.uuid);

            PUBSmartPlug handler = GUI.instance.onSmartPlugHandlerRequest(
                    entry.device.toJson(),
                    entry.status.toJson(),
                    entry.credential.toJson());

            if (handler == null) return;

            boolean off = (entry.status.plugstate == null) || (entry.status.plugstate == 0);
            handler.setPlugState(off ? 1 : 0);
        }
    };

    private static final OnClickListener onSmartBulbClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            GUIListEntryIOT entry = (GUIListEntryIOT) view;

            entry.credential = new IOTCredential(entry.uuid);

            PUBSmartBulb handler = GUI.instance.onSmartBulbHandlerRequest(
                    entry.device.toJson(),
                    entry.status.toJson(),
                    entry.credential.toJson());

            if (handler == null) return;

            boolean off = (entry.status.bulbstate == null) || (entry.status.bulbstate == 0);
            handler.setBulbState(off ? 1 : 0);
        }
    };

    private static final OnClickListener onCameraClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            GUIListEntryIOT entry = (GUIListEntryIOT) view;

            entry.credential = new IOTCredential(entry.uuid);

            PUBCamera handler = GUI.instance.onCameraHandlerRequest(
                    entry.device.toJson(),
                    entry.status.toJson(),
                    entry.credential.toJson());

            if (handler == null) return;

            boolean off = (entry.status.ledstate == null) || (entry.status.ledstate == 0);

            handler.connectCamera();
            handler.setLEDOnOff(off);
            handler.disconnectCamera();
        }
    };

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
        super.setOnClickListener(onClickListener);
    }

    public void setOnUpdateContentListener(OnUpdateContentListener onUpdateContentListener)
    {
        this.onUpdateContentListener = onUpdateContentListener;

        updateContent();
    }

    public interface OnUpdateContentListener
    {
        void onUpdateContent(GUIListEntryIOT entry);
    }
}
