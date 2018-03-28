package de.xavaro.android.gui.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTThing;
import de.xavaro.android.iot.things.IOTThings;

import de.xavaro.android.iot.base.IOTAlive;

import pub.android.interfaces.pub.PUBCamera;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;

public class GUIListEntryIOT extends GUIListEntry
{
    private final static String LOGTAG = GUIListEntryIOT.class.getSimpleName();

    public String uuid;

    public GUIRelativeLayout bulletView;

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    private OnUpdateContentListener onUpdateContentListener;

    public GUIListEntryIOT(Context context)
    {
        super(context);
    }

    public GUIListEntryIOT(Context context, String uuid)
    {
        super(context);

        this.uuid = uuid;

        iconView.setIOTThing(uuid);

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

        IOTDevice.list.subscribe(uuid, onDeviceUpdated);
        IOTStatus.list.subscribe(uuid, onStatusUpdated);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTDevice.list.unsubscribe(uuid, onDeviceUpdated);
        IOTStatus.list.unsubscribe(uuid, onStatusUpdated);
    }

    private void setStatusColor(int color)
    {
        bulletView.setRoundedCornersDip(GUIDefs.PADDING_MEDIUM / 2, color);
    }

    public void updateContent()
    {
        IOTThing iotThing = IOTThings.getEntry(uuid);

        if (iotThing instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotThing;

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
        }

        if (iotThing != null)
        {
            headerViev.setText(iotThing.name);

            Long lastPing = IOTAlive.getAliveNetwork(uuid);

            if (lastPing != null)
            {
                boolean pingt = (System.currentTimeMillis() - lastPing) < (60 * 1000);
                setStatusColor(pingt ? GUIDefs.STATUS_COLOR_GREEN : GUIDefs.STATUS_COLOR_RED);
            }

            if (onLongClickListener == null)
            {
                setOnLongClickListener(onThingLongClickListener);
            }

            if (onUpdateContentListener != null)
            {
                onUpdateContentListener.onUpdateContent(this);
            }
        }
    }

    private final Runnable onDeviceUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            updateContent();
        }
    };

    private final Runnable onStatusUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            updateContent();
        }
    };

    private final OnClickListener onSmartPlugClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = IOTDevice.list.getEntryInternal(uuid);
            IOTStatus status = IOTStatus.list.getEntryInternal(uuid);
            IOTCredential credential = IOTCredential.list.getEntryInternal(uuid);

            PUBSmartPlug handler = GUI.instance.onSmartPlugHandlerRequest(
                    device.toJson(),
                    status.toJson(),
                    credential.toJson());

            if (handler == null) return;

            boolean off = (status.plugstate == null) || (status.plugstate == 0);
            handler.setPlugState(off ? 1 : 0);
        }
    };

    private final OnClickListener onSmartBulbClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = IOTDevice.list.getEntryInternal(uuid);
            IOTStatus status = IOTStatus.list.getEntryInternal(uuid);
            IOTCredential credential = IOTCredential.list.getEntryInternal(uuid);

            PUBSmartBulb handler = GUI.instance.onSmartBulbHandlerRequest(
                    device.toJson(),
                    status.toJson(),
                    credential.toJson());

            if (handler == null) return;

            boolean off = (status.bulbstate == null) || (status.bulbstate == 0);
            handler.setBulbState(off ? 1 : 0);
        }
    };

    private final OnClickListener onCameraClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = IOTDevice.list.getEntryInternal(uuid);
            IOTStatus status = IOTStatus.list.getEntryInternal(uuid);
            IOTCredential credential = IOTCredential.list.getEntryInternal(uuid);

            PUBCamera handler = GUI.instance.onCameraHandlerRequest(
                    device.toJson(),
                    status.toJson(),
                    credential.toJson());

            if (handler == null) return;

            boolean off = (status.ledstate == null) || (status.ledstate == 0);

            handler.connectCamera();
            handler.setLEDOnOff(off);
            handler.disconnectCamera();
        }
    };

    private final OnLongClickListener onThingLongClickListener = new OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            Log.d(LOGTAG,"onThingLongClickListener:");

            IOTThings.deleteThing(uuid);

            return true;
        }
    };

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
        super.setOnClickListener(onClickListener);
    }

    @Override
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListener = onLongClickListener;
        super.setOnLongClickListener(onLongClickListener);
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
