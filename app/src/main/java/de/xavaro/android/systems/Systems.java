package de.xavaro.android.systems;

import android.app.Application;

import de.xavaro.android.adb.base.ADB;
import de.xavaro.android.bcn.base.BCN;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.sny.base.SNY;
import de.xavaro.android.spr.base.SPR;
import de.xavaro.android.tpl.base.TPL;

import zz.top.p2p.base.P2P;

public class Systems
{
    public static void initialize(Application application)
    {
        GUI.instance = new SystemsGUI(application);
        GUI.instance.startSubsystem();
        GUI.instance.subSystems.registerSubsystem(GUI.instance.getSubsystemInfo());

        IOT.instance = new SystemsIOT(application);
        IOT.instance.startSubsystem();
        GUI.instance.subSystems.registerSubsystem(IOT.instance.getSubsystemInfo());

        SystemsSPR spr = new SystemsSPR(application);
        GUI.instance.subSystems.registerSubsystem(spr.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("spr"))
        {
            SPR.instance = spr;
            SPR.instance.startSubsystem();
        }

        SystemsBCN bcn = new SystemsBCN(application);
        GUI.instance.subSystems.registerSubsystem(bcn.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("bcn"))
        {
            BCN.instance = bcn;
            BCN.instance.startSubsystem();
        }

        SystemsIAM iam = new SystemsIAM(application);
        GUI.instance.subSystems.registerSubsystem(iam.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("iam"))
        {
            IAM.instance = iam;
            IAM.instance.startSubsystem();
        }

        SystemsADB adb = new SystemsADB(application);
        GUI.instance.subSystems.registerSubsystem(adb.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("adb"))
        {
            ADB.instance = adb;
            ADB.instance.startSubsystem();
        }

        SystemsSNY sny = new SystemsSNY(application);
        GUI.instance.subSystems.registerSubsystem(sny.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("sny"))
        {
            SNY.instance = sny;
            SNY.instance.startSubsystem();
        }

        SystemsTPL tpl = new SystemsTPL(application);
        GUI.instance.subSystems.registerSubsystem(tpl.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("tpl"))
        {
            TPL.instance = tpl;
            TPL.instance.startSubsystem();
        }

        SystemsP2P p2p = new SystemsP2P(application);
        GUI.instance.subSystems.registerSubsystem(p2p.getSubsystemInfo());

        if (GUI.instance.subSystems.isSubsystemActivated("p2p"))
        {
            P2P.instance = p2p;
            P2P.instance.startSubsystem();
        }
    }
}
