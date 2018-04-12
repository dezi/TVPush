package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTThing;

public class GUIDomainsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIDomainsWizzard.class.getSimpleName();

    private String lastHelper;

    public GUIDomainsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.domains_540);
        setTitleText("Domänen");

        setActionIconVisible(R.drawable.add_540, true);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (lastHelper != null)
        {
            GUI.instance.desktopActivity.displayWizzard(false, lastHelper);
            lastHelper = null;
        }
    }

    @Override
    public void onActionIconClicked()
    {
        Log.d(LOGTAG, "onActionIconClicked:");

        IOTDomain domain = new IOTDomain();

        domain.fixedwifi = Simple.getConnectedWifiName();
        domain.name = domain.fixedwifi;

        IOTDomain.list.addEntry(domain, true, true);

        updateContent();
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTDomain.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDomain domain = IOTDomain.list.getEntry(uuid);

            if (domain == null) continue;

            boolean isnice = (domain.fixedLatFine != null)
                    && (domain.fixedLonFine != null)
                    && (domain.fixedAltFine != null);

            if (todo && isnice) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid);

            entry.setOnUpdateContentListener(onUpdateContentListener);
            entry.setOnClickListener(onClickListener);
        }
    }

    private final GUIListEntryIOT.OnUpdateContentListener onUpdateContentListener =
            new GUIListEntryIOT.OnUpdateContentListener()
    {
        @Override
        public void onUpdateContent(GUIListEntryIOT entry)
        {
            String info = "Keine Geo-Position";

            boolean isnice = false;

            IOTDomain domain = IOTDomain.list.getEntry(entry.uuid);

            if (domain != null)
            {
                isnice = (domain.fixedLatFine != null)
                        && (domain.fixedLonFine != null)
                        && (domain.fixedAltFine != null);

                if (isnice)
                {
                    info = ""
                            + Simple.getRounded3(domain.fixedLatFine)
                            + " "
                            + Simple.getRounded3(domain.fixedLonFine)
                            + " "
                            + Simple.getRounded3(domain.fixedAltFine)
                            + " m";
                }
            }

            entry.infoView.setText(info);

            entry.infoView.setTextColor(isnice
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);
        }
    };

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String uuid = ((GUIListEntryIOT) view).uuid;

            if (lastHelper == null)
            {
                lastHelper = GUIGeomapWizzard.class.getSimpleName();
                GUI.instance.desktopActivity.displayWizzard(lastHelper, uuid);

                return;
            }

            if (lastHelper.equals(GUIGeomapWizzard.class.getSimpleName()))
            {
                GUI.instance.desktopActivity.displayWizzard(false, lastHelper);
                lastHelper = GUILocationsWizzard.class.getSimpleName();
                GUI.instance.desktopActivity.displayWizzard(lastHelper, uuid);

                return;
            }

            GUI.instance.desktopActivity.displayWizzard(false, lastHelper);
            lastHelper = null;
        }
    };
}
