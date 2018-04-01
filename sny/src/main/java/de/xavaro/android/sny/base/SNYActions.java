package de.xavaro.android.sny.base;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SNYActions
{
    public static Map<String, String > actions;

    static
    {
        actions = new HashMap<>();

        actions.put("PowerOn", "AAAAAQAAAAEAAAAuAw==");
        actions.put("Num1",    "AAAAAQAAAAEAAAAAAw==");
        actions.put("Num2", "AAAAAQAAAAEAAAABAw==");
        actions.put("Num3", "AAAAAQAAAAEAAAACAw==");
        actions.put("Num4", "AAAAAQAAAAEAAAADAw==");
        actions.put("Num5", "AAAAAQAAAAEAAAAEAw==");
        actions.put("Num6", "AAAAAQAAAAEAAAAFAw==");
        actions.put("Num7", "AAAAAQAAAAEAAAAGAw==");
        actions.put("Num8", "AAAAAQAAAAEAAAAHAw==");
        actions.put("Num9", "AAAAAQAAAAEAAAAIAw==");
        actions.put("Num0", "AAAAAQAAAAEAAAAJAw==");
        actions.put("Num11", "AAAAAQAAAAEAAAAKAw==");
        actions.put("Num12", "AAAAAQAAAAEAAAALAw==");
        actions.put("Enter", "AAAAAQAAAAEAAAALAw==");
        actions.put("GGuide", "AAAAAQAAAAEAAAAOAw==");
        actions.put("ChannelUp", "AAAAAQAAAAEAAAAQAw==");
        actions.put("ChannelDown", "AAAAAQAAAAEAAAARAw==");
        actions.put("VolumeUp", "AAAAAQAAAAEAAAASAw==");
        actions.put("VolumeDown", "AAAAAQAAAAEAAAATAw==");
        actions.put("Mute", "AAAAAQAAAAEAAAAUAw==");
        actions.put("TvPower", "AAAAAQAAAAEAAAAVAw==");
        actions.put("Audio", "AAAAAQAAAAEAAAAXAw==");
        actions.put("MediaAudioTrack", "AAAAAQAAAAEAAAAXAw==");
        actions.put("Tv", "AAAAAQAAAAEAAAAkAw==");
        actions.put("Input", "AAAAAQAAAAEAAAAlAw==");
        actions.put("TvInput", "AAAAAQAAAAEAAAAlAw==");
        actions.put("TvAntennaCable", "AAAAAQAAAAEAAAAqAw==");
        actions.put("WakeUp", "AAAAAQAAAAEAAAAuAw==");
        actions.put("PowerOff", "AAAAAQAAAAEAAAAvAw==");
        actions.put("Sleep", "AAAAAQAAAAEAAAAvAw==");
        actions.put("Right", "AAAAAQAAAAEAAAAzAw==");
        actions.put("Left", "AAAAAQAAAAEAAAA0Aw==");
        actions.put("SleepTimer", "AAAAAQAAAAEAAAA2Aw==");
        actions.put("Analog2", "AAAAAQAAAAEAAAA4Aw==");
        actions.put("TvAnalog", "AAAAAQAAAAEAAAA4Aw==");
        actions.put("Display", "AAAAAQAAAAEAAAA6Aw==");
        actions.put("Jump", "AAAAAQAAAAEAAAA7Aw==");
        actions.put("PicOff", "AAAAAQAAAAEAAAA+Aw==");
        actions.put("PictureOff", "AAAAAQAAAAEAAAA+Aw==");
        actions.put("Teletext", "AAAAAQAAAAEAAAA/Aw==");
        actions.put("Video1", "AAAAAQAAAAEAAABAAw==");
        actions.put("Video2", "AAAAAQAAAAEAAABBAw==");
        actions.put("AnalogRgb1", "AAAAAQAAAAEAAABDAw==");
        actions.put("Home", "AAAAAQAAAAEAAABgAw==");
        actions.put("Exit", "AAAAAQAAAAEAAABjAw==");
        actions.put("PictureMode", "AAAAAQAAAAEAAABkAw==");
        actions.put("Confirm", "AAAAAQAAAAEAAABlAw==");
        actions.put("Up", "AAAAAQAAAAEAAAB0Aw==");
        actions.put("Down", "AAAAAQAAAAEAAAB1Aw==");
        actions.put("ClosedCaption", "AAAAAgAAAKQAAAAQAw==");
        actions.put("Component1", "AAAAAgAAAKQAAAA2Aw==");
        actions.put("Component2", "AAAAAgAAAKQAAAA3Aw==");
        actions.put("Wide", "AAAAAgAAAKQAAAA9Aw==");
        actions.put("EPG", "AAAAAgAAAKQAAABbAw==");
        actions.put("PAP", "AAAAAgAAAKQAAAB3Aw==");
        actions.put("TenKey", "AAAAAgAAAJcAAAAMAw==");
        actions.put("BSCS", "AAAAAgAAAJcAAAAQAw==");
        actions.put("Ddata", "AAAAAgAAAJcAAAAVAw==");
        actions.put("Stop", "AAAAAgAAAJcAAAAYAw==");
        actions.put("Pause", "AAAAAgAAAJcAAAAZAw==");
        actions.put("Play", "AAAAAgAAAJcAAAAaAw==");
        actions.put("Rewind", "AAAAAgAAAJcAAAAbAw==");
        actions.put("Forward", "AAAAAgAAAJcAAAAcAw==");
        actions.put("DOT", "AAAAAgAAAJcAAAAdAw==");
        actions.put("Rec", "AAAAAgAAAJcAAAAgAw==");
        actions.put("Return", "AAAAAgAAAJcAAAAjAw==");
        actions.put("Blue", "AAAAAgAAAJcAAAAkAw==");
        actions.put("Red", "AAAAAgAAAJcAAAAlAw==");
        actions.put("Green", "AAAAAgAAAJcAAAAmAw==");
        actions.put("Yellow", "AAAAAgAAAJcAAAAnAw==");
        actions.put("SubTitle", "AAAAAgAAAJcAAAAoAw==");
        actions.put("CS", "AAAAAgAAAJcAAAArAw==");
        actions.put("BS", "AAAAAgAAAJcAAAAsAw==");
        actions.put("Digital", "AAAAAgAAAJcAAAAyAw==");
        actions.put("Options", "AAAAAgAAAJcAAAA2Aw==");
        actions.put("Media", "AAAAAgAAAJcAAAA4Aw==");
        actions.put("Prev", "AAAAAgAAAJcAAAA8Aw==");
        actions.put("Next", "AAAAAgAAAJcAAAA9Aw==");
        actions.put("DpadCenter", "AAAAAgAAAJcAAABKAw==");
        actions.put("CursorUp", "AAAAAgAAAJcAAABPAw==");
        actions.put("CursorDown", "AAAAAgAAAJcAAABQAw==");
        actions.put("CursorLeft", "AAAAAgAAAJcAAABNAw==");
        actions.put("CursorRight", "AAAAAgAAAJcAAABOAw==");
        actions.put("ShopRemoteControlForcedDynamic", "AAAAAgAAAJcAAABqAw==");
        actions.put("FlashPlus", "AAAAAgAAAJcAAAB4Aw==");
        actions.put("FlashMinus", "AAAAAgAAAJcAAAB5Aw==");
        actions.put("AudioQualityMode", "AAAAAgAAAJcAAAB7Aw==");
        actions.put("DemoMode", "AAAAAgAAAJcAAAB8Aw==");
        actions.put("Analog", "AAAAAgAAAHcAAAANAw==");
        actions.put("Mode3D", "AAAAAgAAAHcAAABNAw==");
        actions.put("DigitalToggle", "AAAAAgAAAHcAAABSAw==");
        actions.put("DemoSurround", "AAAAAgAAAHcAAAB7Aw==");
        actions.put("*AD", " AAAAAgAAABoAAAA7Aw == ");
        actions.put("AudioMixUp", "AAAAAgAAABoAAAA8Aw==");
        actions.put("AudioMixDown", "AAAAAgAAABoAAAA9Aw==");
        actions.put("PhotoFrame", "AAAAAgAAABoAAABVAw==");
        actions.put("Tv_Radio", "AAAAAgAAABoAAABXAw==");
        actions.put("SyncMenu", "AAAAAgAAABoAAABYAw==");
        actions.put("Hdmi1", "AAAAAgAAABoAAABaAw==");
        actions.put("Hdmi2", "AAAAAgAAABoAAABbAw==");
        actions.put("Hdmi3", "AAAAAgAAABoAAABcAw==");
        actions.put("Hdmi4", "AAAAAgAAABoAAABdAw==");
        actions.put("TopMenu", "AAAAAgAAABoAAABgAw==");
        actions.put("PopUpMenu", "AAAAAgAAABoAAABhAw==");
        actions.put("OneTouchTimeRec", "AAAAAgAAABoAAABkAw==");
        actions.put("OneTouchView", "AAAAAgAAABoAAABlAw==");
        actions.put("DUX", "AAAAAgAAABoAAABzAw==");
        actions.put("FootballMode", "AAAAAgAAABoAAAB2Aw==");
        actions.put("iManual", "AAAAAgAAABoAAAB7Aw==");
        actions.put("Netflix", "AAAAAgAAABoAAAB8Aw==");
        actions.put("Assists", "AAAAAgAAAMQAAAA7Aw==");
        actions.put("ActionMenu", "AAAAAgAAAMQAAABLAw==");
        actions.put("Help", "AAAAAgAAAMQAAABNAw==");
        actions.put("TvSatellite", "AAAAAgAAAMQAAABOAw==");
        actions.put("WirelessSubwoofer", "AAAAAgAAAMQAAAB+Aw==");
    }

    @Nullable
    public static String getAction(String action)
    {
        try
        {
            return actions.get(action);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }
}
