package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PSession;
import de.xavaro.android.yihome.P2PPacker;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PTZPresetData
{
    private P2PSession session;

    public PTZPresetData(P2PSession session)
    {
        this.session = session;
    }

    public PTZPresetData(P2PSession session, byte[] data)
    {
        this.session = session;

        parse(data);
    }

    public short opResult;
    public short presetIndex;
    public PTZPresetsData presets = new PTZPresetsData(session);

    public void parse(byte[] data)
    {
        opResult = P2PPacker.byteArrayToShort(data, 0, session.isBigEndian);
        presetIndex = P2PPacker.byteArrayToShort(data, 2, session.isBigEndian);

        byte[] ptzpresets = new byte[data.length - 4];
        System.arraycopy(data, 4, ptzpresets, 0, ptzpresets.length);

        presets = new PTZPresetsData(session, ptzpresets);
    }
}
