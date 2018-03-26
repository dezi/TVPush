package de.xavaro.android.systems;

import android.app.Application;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iam.base.IAM;
import zz.top.sny.base.SNY;
import zz.top.tpl.base.TPL;

public class Systems
{
    public static SystemsIOT iot;
    public static SystemsGUI gui;

    public static SystemsP2P p2p;

    public static void initialize(Application application)
    {
        iot = new SystemsIOT(application);
        gui = new SystemsGUI(application);

        SystemsIAM iam = new SystemsIAM(application);

        if (GUI.instance.subSystems.isSubsystemActivated("iam"))
        {
            IAM.instance = iam;
            IAM.instance.startSubsystem();
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

        p2p = new SystemsP2P(application);

        p2p.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
