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
        gui.subSystems.registerSubsystem(iam.getDriverTag());

        tpl = new SystemsTPL(application);
        gui.subSystems.registerSubsystem(tpl.getDriverTag());

        sny = new SystemsSNY(application);
        gui.subSystems.registerSubsystem(sny.getDriverTag());
        
        p2p = new SystemsP2P(application);
        gui.subSystems.registerSubsystem(p2p.getDriverTag());

        p2p.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
