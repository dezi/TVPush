package de.xavaro.android.yihome.p2pcommands;

import java.util.ArrayList;

import de.xavaro.android.yihome.P2PSession;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PTZPresets
{
    private P2PSession session;

    public PTZPresets(P2PSession session)
    {
        this.session = session;
    }

    public PTZPresets(P2PSession session, byte[] data)
    {
        this.session = session;

        parse(data);
    }

    public int point_count;
    public ArrayList<Integer> presets = new ArrayList<>();

    public void parse(byte[] data)
    {
        point_count = data[0];
        presets.clear();

        int count = 0;

        for (int inx = 4; inx < data.length; inx++)
        {
            if ((data[inx] != (byte) 0) && (count < point_count))
            {
                presets.add((int) data[inx]);
            }
        }
    }
}
