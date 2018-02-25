package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PPacker;
import de.xavaro.android.yihome.P2PSession;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ResolutionData
{
    private P2PSession session;

    public ResolutionData(P2PSession session)
    {
        this.session = session;
    }

    public ResolutionData(P2PSession session, byte[] data)
    {
        this.session = session;

        parse(data);
    }

    public final static int RESOLUTION_720P = 1;
    public final static int RESOLUTION_1080P = 2;
    public final static int RESOLUTION_SUPER1080P = 3;

    public int resolution;
    public int reserved;

    public void parse(byte[] data)
    {
        resolution = P2PPacker.byteArrayToInt(data, 0, session.isBigEndian);
        reserved = P2PPacker.byteArrayToInt(data, 4, session.isBigEndian);
    }
}
