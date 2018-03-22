package de.xavaro.android.gui.views;

import android.content.Context;
import android.util.Log;
import android.view.View;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import pub.android.interfaces.drv.Camera;
import pub.android.interfaces.drv.SmartBulb;
import pub.android.interfaces.drv.SmartPlug;

public class GUIListEntryIOT extends GUIListEntry
{
    private final static String LOGTAG = GUIListEntryIOT.class.getSimpleName();

    public String uuid;
    public IOTDevice device;
    public IOTStatus status;
    public IOTCredential credential;

    public GUIListEntryIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        IOTStatusses.instance.subscribe(device.uuid, onStatusUpdated);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTStatusses.instance.unsubscribe(device.uuid, onStatusUpdated);
    }

    public void updateContent()
    {
        int residplain = GUIIcons.getImageResid(device, false);
        int residcolor = GUIIcons.getImageResid(device, true);

        iconView.setImageResource(residplain);

        if (device.type.equals("smartbulb"))
        {
            int color = GUIDefs.STATUS_COLOR_INACT;

            if ((status.hue != null)
                    && (status.saturation != null)
                    && (status.brightness != null)
                    && (status.bulbstate != null))
            {
                if (status.bulbstate != 0)
                {
                    color = Simple.colorRGB(status.hue, status.saturation, 100);
                    color = Simple.setRGBAlpha(color, status.brightness + 155);
                }
            }

            iconView.setImageResource(residcolor, color);

            setOnClickListener(onSmartBulbClickListener);
        }

        if (device.type.equals("smartplug"))
        {
            int color = ((status.plugstate == null) || (status.plugstate == 0))
                    ? GUIDefs.STATUS_COLOR_INACT
                    : GUIDefs.STATUS_COLOR_GREEN;

            iconView.setImageResource(residcolor, color);

            setOnClickListener(onSmartPlugClickListener);
        }

        if (device.type.equals("camera"))
        {
            int color = ((status.ledstate == null) || (status.ledstate == 0))
                    ? GUIDefs.STATUS_COLOR_INACT
                    : GUIDefs.STATUS_COLOR_BLUE;

            iconView.setImageResource(residcolor, color);

            setOnClickListener(onCameraClickListener);
        }

        String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;

        headerViev.setText(device.name);
        infoView.setText(connect);

        Long lastPing = IOTAlive.getAliveNetwork(uuid);

        if (lastPing != null)
        {
            boolean pingt = (System.currentTimeMillis() - lastPing) < (60 * 1000);
            setStatusColor(pingt ? GUIDefs.STATUS_COLOR_GREEN : GUIDefs.STATUS_COLOR_RED);
        }
    }

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

            SmartPlug handler = GUI.instance.onSmartPlugHandlerRequest(
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

            SmartBulb handler = GUI.instance.onSmartBulbHandlerRequest(
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

            Camera handler = GUI.instance.onCameraHandlerRequest(
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

}
