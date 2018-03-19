package de.xavaro.android.systems;

import android.app.Application;

import de.xavaro.android.iot.base.IOTBackup;

public class Systems
{
    public static SystemsGUI gui;
    public static SystemsIOT iot;
    public static SystemsSNY sny;
    public static SystemsTPL tpl;
    public static SystemsIAM iam;
    public static SystemsP2P p2p;

    public static void initialize(Application application)
    {
        iot = new SystemsIOT(application);
        gui = new SystemsGUI(application);
        iam = new SystemsIAM(application);
        tpl = new SystemsTPL(application);
        p2p = new SystemsP2P(application);

        p2p.login("dezi@kappa-mm.de", "blabla1234!");

        sny = new SystemsSNY(application);

        //IOTBackup.saveBackup(application);
    }
}
