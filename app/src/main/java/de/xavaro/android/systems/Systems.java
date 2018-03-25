package de.xavaro.android.systems;

import android.app.Application;

public class Systems
{
    public static SystemsIOT iot;
    public static SystemsGUI gui;
    public static SystemsIAM iam;

    public static SystemsSNY sny;
    public static SystemsTPL tpl;
    public static SystemsP2P p2p;

    public static void initialize(Application application)
    {
        iot = new SystemsIOT(application);
        gui = new SystemsGUI(application);

        iam = new SystemsIAM(application);
        tpl = new SystemsTPL(application);
        sny = new SystemsSNY(application);
        p2p = new SystemsP2P(application);

        p2p.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
