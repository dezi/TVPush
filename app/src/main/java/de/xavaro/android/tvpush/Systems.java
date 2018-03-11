package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

public class Systems
{
    private static final String LOGTAG = Systems.class.getSimpleName();

    private static SystemsGUI gui;
    private static SystemsIOT iot;
    private static SystemsTPL tpl;
    private static SystemsIAM iam;
    private static SystemsP2P p2p;

    public static void initialize(Application application)
    {
        iot = new SystemsIOT(application);
        tpl = new SystemsTPL(application);
        gui = new SystemsGUI(application);
        iam = new SystemsIAM(application);

        p2p = new SystemsP2P(application);
        p2p.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
