package de.xavaro.android.systems;

import android.app.Application;

import de.xavaro.android.bcn.base.BCN;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.iot.base.IOT;

import de.xavaro.android.sny.base.SNY;
import de.xavaro.android.tpl.base.TPL;
import zz.top.p2p.base.P2P;

public class Systems
{
    public static void initialize(Application application)
    {
        GUI.instance = new SystemsGUI(application);
        GUI.instance.startSubsystem();

        IOT.instance = new SystemsIOT(application);
        IOT.instance.startSubsystem();

        SystemsIAM iam = new SystemsIAM(application);

        if (GUI.instance.subSystems.isSubsystemActivated("iam"))
        {
            IAM.instance = iam;
            IAM.instance.startSubsystem();
        }

        SystemsBCN bcn = new SystemsBCN(application);

        if (GUI.instance.subSystems.isSubsystemActivated("bcn"))
        {
            BCN.instance = bcn;
            BCN.instance.startSubsystem();
        }

        SystemsSNY sny = new SystemsSNY(application);

        if (GUI.instance.subSystems.isSubsystemActivated("sny"))
        {
            SNY.instance = sny;
            SNY.instance.startSubsystem();
        }

        SystemsTPL tpl = new SystemsTPL(application);

        if (GUI.instance.subSystems.isSubsystemActivated("tpl"))
        {
            TPL.instance = tpl;
            TPL.instance.startSubsystem();
        }

        SystemsP2P p2p = new SystemsP2P(application);

        if (GUI.instance.subSystems.isSubsystemActivated("p2p"))
        {
            P2P.instance = p2p;
            P2P.instance.startSubsystem();
        }
    }
}
