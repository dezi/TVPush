package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PSession;
import de.xavaro.android.yihome.P2PPacker;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PTZInfo
{
    private P2PSession session;

    public PTZInfo(P2PSession session)
    {
        this.session = session;
    }

    public PTZInfo(P2PSession session, byte[] data)
    {
        this.session = session;

        parse(data);
    }

    public byte motionTrackState;
    public byte cruiseMode;
    public byte curiseState;

    public int panoramicCruiseStayTime;
    public int presetCruiseStayTime;

    public int startTime;
    public int endTime;

    public void parse(byte[] data)
    {
        motionTrackState = data[0];
        curiseState = data[1];
        cruiseMode = data[2];
        presetCruiseStayTime = P2PPacker.byteArrayToInt(data, 4, session.isBigEndian);
        panoramicCruiseStayTime = P2PPacker.byteArrayToInt(data, 8, session.isBigEndian);
        startTime = P2PPacker.byteArrayToInt(data, 12, session.isBigEndian);
        endTime = P2PPacker.byteArrayToInt(data, 16, session.isBigEndian);
    }
}
