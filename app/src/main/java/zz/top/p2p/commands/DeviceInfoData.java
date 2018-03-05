package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PSession;
import zz.top.p2p.camera.P2PPacker;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DeviceInfoData
{
    public static final int V1_SIZE = 56;
    public static final int V2_EXTEND_SIZE = 256;

    private P2PSession session;

    public DeviceInfoData(P2PSession session)
    {
        this.session = session;

        pizInfo = new PTZInfoData(session);
        presets = new PTZPresetsData(session);
    }

    public DeviceInfoData(P2PSession session, byte[] data)
    {
        this(session);

        parse(data);
    }

    public int version;
    public int channel;
    public int free;
    public int total;

    public byte alarm_ring;
    public byte alarm_sensitivity;
    public byte baby_crying_mode;
    public byte check_stat;
    public byte close_camera;
    public byte close_light;
    public byte day_night_mode;
    public byte frame_rate;
    public byte hardware_version;
    public byte interface_version;
    public byte internet_lossrate;
    public byte internet_visit;
    public byte is_utc_time;
    public byte language;
    public byte ldc_mode;
    public byte lossrate;
    public byte mic_mode;
    public byte record_mode;
    public byte reverse_mode;
    public byte talk_mode;
    public byte tfstat;
    public byte update_mode;
    public byte update_progress;
    public byte update_stat;
    public byte update_without_tf;
    public byte version_type;
    public byte video_backup;

    public byte v1_alarm_ring;
    public byte v1_alarm_sensitivity;
    public byte v1_baby_crying_mode;
    public byte v1_day_night_mode;
    public byte v1_frame_rate;
    public byte v1_is_utc_time;
    public byte v1_ldc_mode;
    public byte v1_mic_mode;
    public byte v1_talk_mode;
    public byte v1_version_type;
    public byte v1_video_backup;

    public byte v2_alarm_mode;
    public byte v2_alarm_sensitivity;
    public byte v2_beep_mode;
    public byte v2_day_night_mode;
    public byte v2_extend_abnormal_sound;
    public byte v2_extend_abnormal_sound_sensitivity;
    public byte v2_extend_baby_crying_mode;
    public byte v2_extend_gesture_mode;
    public byte v2_extend_mic_mode;
    public byte v2_extend_micboost_set;
    public byte v2_extend_motion_roi;
    public byte v2_extend_pgc_live;
    public byte v2_extend_safe_remove_sd;
    public byte v2_extend_upload_log;
    public byte v2_extend_version_rollback;
    public byte v2_extend_video_backup;
    public byte v2_extend_video_talkmode;
    public byte v2_extend_wifi_switch;
    public byte v2_hd_resolution;
    public byte v2_is_extend;
    public byte v2_is_utc_time;
    public byte v2_ldc_mode;
    public byte v2_silent_upgrade;
    public byte v2_speaker_volume;
    public byte v2_version_type;

    public PTZInfoData pizInfo;
    public PTZPresetsData presets;

    public void parse(byte[] data)
    {
        presets = new PTZPresetsData(session);
        pizInfo = new PTZInfoData(session);

        interface_version = data[0];
        lossrate = data[1];
        tfstat = data[2];
        internet_lossrate = data[3];
        internet_visit = data[4];
        check_stat = data[5];
        update_without_tf = data[6];
        language = data[7];
        hardware_version = data[8];

        version = P2PPacker.byteArrayToInt(data, 32, session.isBigEndian);
        channel = P2PPacker.byteArrayToInt(data, 36, session.isBigEndian);
        total = P2PPacker.byteArrayToInt(data, 40, session.isBigEndian);
        free = P2PPacker.byteArrayToInt(data, 44, session.isBigEndian);

        close_camera = data[48];
        close_light = data[49];
        update_stat = data[50];
        update_progress = data[51];
        record_mode = data[52];
        update_mode = data[53];
        reverse_mode = data[54];
        
        if (hardware_version != (byte) 1)
        {
            v1_is_utc_time = data[9];
            v1_day_night_mode = data[10];
            v1_alarm_sensitivity = data[11];
            v1_version_type = data[12];
            v1_video_backup = data[13];
            v1_ldc_mode = data[14];
            v1_baby_crying_mode = data[15];
            v1_mic_mode = data[16];
            v1_talk_mode = data[17];
            v1_frame_rate = data[20];
            v1_alarm_ring = data[23];

            day_night_mode = v1_day_night_mode;
            is_utc_time = v1_is_utc_time;
            alarm_sensitivity = v1_alarm_sensitivity;
            version_type = v1_version_type;
            video_backup = v1_video_backup;
            ldc_mode = v1_ldc_mode;
            baby_crying_mode = v1_baby_crying_mode;
            mic_mode = v1_mic_mode;
            talk_mode = v1_talk_mode;
            frame_rate = v1_frame_rate;
            alarm_ring = v1_alarm_ring;

            if ((hardware_version == (byte) 2
                    || hardware_version == (byte) 3
                    || hardware_version == (byte) 20) && interface_version >= (byte) 8)
            {
                if (data.length >= 88)
                {
                    byte[] ptzpresets = new byte[12];
                    System.arraycopy(data, 56, ptzpresets, 0, 12);
                    presets = new PTZPresetsData(session, ptzpresets);

                    byte[]  ptzinfo = new byte[20];
                    System.arraycopy(data, 68, ptzinfo, 0, 20);
                    pizInfo = new PTZInfoData(session, ptzinfo);
                }
            }
        }
        else
        {
            v2_version_type = data[9];
            v2_day_night_mode = data[10];
            v2_hd_resolution = data[11];
            v2_alarm_mode = data[12];
            v2_ldc_mode = data[13];
            v2_is_utc_time = data[14];
            v2_alarm_sensitivity = data[15];
            v2_beep_mode = data[16];
            v2_speaker_volume = data[17];
            v2_is_extend = data[18];
            v2_silent_upgrade = data[19];

            day_night_mode = v2_day_night_mode;
            is_utc_time = v2_is_utc_time;
            alarm_sensitivity = v2_alarm_sensitivity;
            version_type = v2_version_type;
            ldc_mode = v2_ldc_mode;

            if ((v2_is_extend > (byte) 0) && (data.length >= 256))
            {
                v2_extend_mic_mode = data[56];
                v2_extend_baby_crying_mode = data[57];
                v2_extend_gesture_mode = data[58];
                v2_extend_motion_roi = data[59];
                v2_extend_safe_remove_sd = data[60];
                v2_extend_version_rollback = data[61];
                v2_extend_upload_log = data[62];
                v2_extend_wifi_switch = data[63];
                v2_extend_video_backup = data[64];
                v2_extend_video_talkmode = data[65];
                v2_extend_pgc_live = data[68];
                v2_extend_micboost_set = data[69];
                v2_extend_abnormal_sound = data[70];
                v2_extend_abnormal_sound_sensitivity = data[71];

                baby_crying_mode = v2_extend_baby_crying_mode;
                video_backup = v2_extend_video_backup;
                mic_mode = v2_extend_mic_mode;
            }
        }
    }
}
