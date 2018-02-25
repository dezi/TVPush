package de.xavaro.android.yihome;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class AVIOCTRLDEFs
{
    private final static String LOGTAG = AVIOCTRLDEFs.class.getSimpleName();

    public static final int AVIOCTRL_RECORD_PLAY_START = 16;
    public static final int AVIOCTRL_RECORD_PLAY_STOP = 1;
    public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 768;
    public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 769;
    public static final int IOTYPE_USER_IPCAM_CANCEL_UPDATE_PHONE_REQ = 4872;
    public static final int IOTYPE_USER_IPCAM_CANCEL_UPDATE_PHONE_RESP = 4878;
    public static final int IOTYPE_USER_IPCAM_CHECK_STAT_REQ = 4889;
    public static final int IOTYPE_USER_IPCAM_CHECK_STAT_REQ_RESP = 4890;
    public static final int IOTYPE_USER_IPCAM_CLOSE_CAMERA_REQ = 4868;
    public static final int IOTYPE_USER_IPCAM_CLOSE_CAMERA_RESP = 4876;
    public static final int IOTYPE_USER_IPCAM_CLOSE_LIGHT_REQ = 4869;
    public static final int IOTYPE_USER_IPCAM_CLOSE_LIGHT_RESP = 4877;
    public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 816;
    public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 817;
    public static final int IOTYPE_USER_IPCAM_GET_IPC_INFO = 4939;
    public static final int IOTYPE_USER_IPCAM_GET_IPC_INFO_RESP = 4940;
    public static final int IOTYPE_USER_IPCAM_GET_MOTION_DETECT = 4903;
    public static final int IOTYPE_USER_IPCAM_GET_MOTION_DETECT_RESP = 4904;
    public static final int IOTYPE_USER_IPCAM_GET_PRE_VERSION = 4931;
    public static final int IOTYPE_USER_IPCAM_GET_PRE_VERSION_RESP = 4932;
    public static final int IOTYPE_USER_IPCAM_GET_RECORD_SPEED = 4921;
    public static final int IOTYPE_USER_IPCAM_GET_RECORD_SPEED_RESP = 4922;
    public static final int IOTYPE_USER_IPCAM_GET_RESOLUTION = 4883;
    public static final int IOTYPE_USER_IPCAM_GET_RESOLUTION_RESP = 4884;
    public static final int IOTYPE_USER_IPCAM_GET_VIDEO_BACKUP = 4949;
    public static final int IOTYPE_USER_IPCAM_GET_VIDEO_BACKUP_RESP = 4950;
    public static final int IOTYPE_USER_IPCAM_HEART = 110;
    public static final int IOTYPE_USER_IPCAM_IFRAME_PIECES_REQ = 4633;
    public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 792;
    public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 793;
    public static final int IOTYPE_USER_IPCAM_QUERY_RTMP_STAT_REQ = 948;
    public static final int IOTYPE_USER_IPCAM_QUERY_RTMP_STAT_RESP = 949;
    public static final int IOTYPE_USER_IPCAM_REBOOT_PHONE_REQ = 4873;
    public static final int IOTYPE_USER_IPCAM_REBOOT_PHONE_RESP = 4880;
    public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 794;
    public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL2 = 12718;
    public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 795;
    public static final int IOTYPE_USER_IPCAM_RECVICE_ALARMINFO = 8191;
    public static final int IOTYPE_USER_IPCAM_RESP = 65535;
    public static final int IOTYPE_USER_IPCAM_SET_ABS_SENSITIVITY = 4957;
    public static final int IOTYPE_USER_IPCAM_SET_ABS_SENSITIVITY_RESP = 4958;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_MODE = 4909;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_MODE_RESP = 4910;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_RING = 4955;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_RING_RES = 4956;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_SENSITIVITY = 4917;
    public static final int IOTYPE_USER_IPCAM_SET_ALARM_SENSITIVITY_RESP = 4918;
    public static final int IOTYPE_USER_IPCAM_SET_BABY_CRYING_MODE = 4927;
    public static final int IOTYPE_USER_IPCAM_SET_BABY_CRYING_MODE_RESP = 4928;
    public static final int IOTYPE_USER_IPCAM_SET_BEEP = 4913;
    public static final int IOTYPE_USER_IPCAM_SET_BEEP_RESP = 4914;
    public static final int IOTYPE_USER_IPCAM_SET_DAYNIGHT_MODE = 4897;
    public static final int IOTYPE_USER_IPCAM_SET_DAYNIGHT_MODE_RESP = 4898;
    public static final int IOTYPE_USER_IPCAM_SET_HD_RESOLUTION = 4905;
    public static final int IOTYPE_USER_IPCAM_SET_HD_RESOLUTION_RESP = 4906;
    public static final int IOTYPE_USER_IPCAM_SET_LDC = 4911;
    public static final int IOTYPE_USER_IPCAM_SET_LDC_RESP = 4912;
    public static final int IOTYPE_USER_IPCAM_SET_MIC_VOLUME = 4923;
    public static final int IOTYPE_USER_IPCAM_SET_MIC_VOLUME_RESP = 4924;
    public static final int IOTYPE_USER_IPCAM_SET_MIRROR_FLIP = 4895;
    public static final int IOTYPE_USER_IPCAM_SET_MIRROR_FLIP_PESP = 4896;
    public static final int IOTYPE_USER_IPCAM_SET_MOTION_DETECT = 4901;
    public static final int IOTYPE_USER_IPCAM_SET_MOTION_DETECT_RESP = 4902;
    public static final int IOTYPE_USER_IPCAM_SET_MOTION_RECT_ROI_MODE = 4935;
    public static final int IOTYPE_USER_IPCAM_SET_MOTION_RECT_ROI_MODE_RESP = 4936;
    public static final int IOTYPE_USER_IPCAM_SET_PGC_LIVE = 4953;
    public static final int IOTYPE_USER_IPCAM_SET_PGC_LIVE_RESP = 4954;
    public static final int IOTYPE_USER_IPCAM_SET_RECORD_MODE = 4874;
    public static final int IOTYPE_USER_IPCAM_SET_RECORD_MODE_RESP = 4875;
    public static final int IOTYPE_USER_IPCAM_SET_RECORD_SPEED = 4919;
    public static final int IOTYPE_USER_IPCAM_SET_RECORD_SPEED_RESP = 4920;
    public static final int IOTYPE_USER_IPCAM_SET_RESOLUTION = 4881;
    public static final int IOTYPE_USER_IPCAM_SET_RESOLUTION_RESP = 4882;
    public static final int IOTYPE_USER_IPCAM_SET_ROI = 4907;
    public static final int IOTYPE_USER_IPCAM_SET_ROI_RESP = 4908;
    public static final int IOTYPE_USER_IPCAM_SET_RTMP_ADDR_REQ = 950;
    public static final int IOTYPE_USER_IPCAM_SET_RTMP_ADDR_RESP = 951;
    public static final int IOTYPE_USER_IPCAM_SET_SILENT_UPGRADE = 4943;
    public static final int IOTYPE_USER_IPCAM_SET_SILENT_UPGRADE_RESP = 4944;
    public static final int IOTYPE_USER_IPCAM_SET_SMART_IA_MODE = 4933;
    public static final int IOTYPE_USER_IPCAM_SET_SMART_IA_MODE_RESP = 4934;
    public static final int IOTYPE_USER_IPCAM_SET_SPEAKER_VOLUME = 4915;
    public static final int IOTYPE_USER_IPCAM_SET_SPEAKER_VOLUME_RESP = 4916;
    public static final int IOTYPE_USER_IPCAM_SET_TF_FORMAT = 4885;
    public static final int IOTYPE_USER_IPCAM_SET_TF_FORMAT_RESP = 4886;
    public static final int IOTYPE_USER_IPCAM_SET_TF_UMOUNT = 4925;
    public static final int IOTYPE_USER_IPCAM_SET_TF_UMOUNT_RESP = 4926;
    public static final int IOTYPE_USER_IPCAM_SET_UPLOAD_LOG = 4937;
    public static final int IOTYPE_USER_IPCAM_SET_UPLOAD_LOG_RESP = 4938;
    public static final int IOTYPE_USER_IPCAM_SET_VER_RECOVER = 4929;
    public static final int IOTYPE_USER_IPCAM_SET_VER_RECOVER_RESP = 4930;
    public static final int IOTYPE_USER_IPCAM_SET_WIFI_SWITCH = 4945;
    public static final int IOTYPE_USER_IPCAM_SET_WIFI_SWITCH_RESP = 4946;
    public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 848;
    public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 849;
    public static final int IOTYPE_USER_IPCAM_START = 511;
    public static final int IOTYPE_USER_IPCAM_START2 = 8190;
    public static final int IOTYPE_USER_IPCAM_START_CHECK = 4887;
    public static final int IOTYPE_USER_IPCAM_START_CHECK_RESP = 4888;
    public static final int IOTYPE_USER_IPCAM_START_RESP = 512;
    public static final int IOTYPE_USER_IPCAM_START_RTMP_REQ = 944;
    public static final int IOTYPE_USER_IPCAM_START_RTMP_RESP = 945;
    public static final int IOTYPE_USER_IPCAM_STOP = 767;
    public static final int IOTYPE_USER_IPCAM_STOP_CLOUD_STORAGE_REQ = 962;
    public static final int IOTYPE_USER_IPCAM_STOP_CLOUD_STORAGE_REQ_RESP = 963;
    public static final int IOTYPE_USER_IPCAM_STOP_RTMP_REQ = 946;
    public static final int IOTYPE_USER_IPCAM_STOP_RTMP_REQ_RESP = 947;
    public static final int IOTYPE_USER_IPCAM_TNP_EVENT_LIST_REQ = 9031;
    public static final int IOTYPE_USER_IPCAM_TNP_EVENT_LIST_RESP = 9032;
    public static final int IOTYPE_USER_IPCAM_TNP_NETWORK_CHECK = 60929;
    public static final int IOTYPE_USER_IPCAM_TNP_NETWORK_CHECK_RESP = 60930;
    public static final int IOTYPE_USER_IPCAM_TNP_ONLINE_STATUS = 60931;
    public static final int IOTYPE_USER_IPCAM_TNP_ONLINE_STATUS_RESP = 60932;
    public static final int IOTYPE_USER_IPCAM_TNP_START_REALTIME = 9029;
    public static final int IOTYPE_USER_IPCAM_TNP_START_RECORD = 9030;
    public static final int IOTYPE_USER_IPCAM_TRIGGER_SYNC_INFO_FROM_SERVER_REQ = 960;
    public static final int IOTYPE_USER_IPCAM_TRIGGER_SYNC_INFO_FROM_SERVER_RESP = 961;
    public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_PHONE_REQ = 4864;
    public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_PHONE_RSP = 4865;
    public static final int IOTYPE_USER_IPCAM_UPDATE_PHONE_REQ = 4866;
    public static final int IOTYPE_USER_IPCAM_UPDATE_PHONE_RSP = 4879;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_ABROT = 16418;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_ABROT_RSP = 16419;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_SCHEDULE_POLLING = 16420;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_SCHEDULE_POLLING_RSP = 16421;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_START = 16416;
    public static final int IOTYPE_USER_PANORAMA_CAPTURE_START_RSP = 16417;
    public static final int IOTYPE_USER_PTZ_CTRL_STOP = 16403;
    public static final int IOTYPE_USER_PTZ_DIRECTION_CTRL = 16402;
    public static final int IOTYPE_USER_PTZ_HOME = 16404;
    public static final int IOTYPE_USER_PTZ_JUMP_TO_POINT = 16405;
    public static final int IOTYPE_USER_PTZ_PRESET_ADD_REQ = 16384;
    public static final int IOTYPE_USER_PTZ_PRESET_ADD_RESP = 16385;
    public static final int IOTYPE_USER_PTZ_PRESET_CALL = 16390;
    public static final int IOTYPE_USER_PTZ_PRESET_DEL_REQ = 16386;
    public static final int IOTYPE_USER_PTZ_PRESET_DEL_RESP = 16387;
    public static final int IOTYPE_USER_PTZ_SET_CRUISE_PERIOD_REQ = 16393;
    public static final int IOTYPE_USER_PTZ_SET_CRUISE_PERIOD_RESP = 16394;
    public static final int IOTYPE_USER_PTZ_SET_CURISE_STAY_TIME_REQ = 16391;
    public static final int IOTYPE_USER_PTZ_SET_CURISE_STAY_TIME_RESP = 16392;
    public static final int IOTYPE_USER_PTZ_SET_MOTION_TRACK_REQ = 16395;
    public static final int IOTYPE_USER_PTZ_SET_MOTION_TRACK_RESP = 16396;
    public static final int IOTYPE_USER_SET_PTZ_CRUISE_REQ = 16400;
    public static final int IOTYPE_USER_SET_PTZ_CRUISE_RESP = 16401;
    public static final int IOTYPE_USER_TNP_IPCAM_KICK = 9033;
    public static final int IOTYPE_USER_TRIGER_TIME_ZONE_REQ = 8192;

    public static class SMsgAVIoctrlPTZDireCTRL
    {
        public static byte[] parseContent(int i, int i2, boolean z)
        {
            byte[] obj = new byte[8];
            System.arraycopy(Packet.intToByteArray(i, z), 0, obj, 0, 4);
            System.arraycopy(Packet.intToByteArray(i2, z), 0, obj, 4, 4);
            return obj;
        }
    }

    @SuppressWarnings("unused")
    public static class SMsgAVIoctrlOnlineStatusResp
    {
        public int lastOnlineTime;
        public int online;

        public static SMsgAVIoctrlOnlineStatusResp parse(byte[] bArr, boolean z)
        {
            SMsgAVIoctrlOnlineStatusResp sMsgAVIoctrlOnlineStatusResp = new SMsgAVIoctrlOnlineStatusResp();
            sMsgAVIoctrlOnlineStatusResp.online = Packet.byteArrayToInt(bArr, 0, z);
            sMsgAVIoctrlOnlineStatusResp.lastOnlineTime = Packet.byteArrayToInt(bArr, 4, z);

            return sMsgAVIoctrlOnlineStatusResp;
        }

        public static byte[] parseContent(int i, int i2, boolean z)
        {
            byte[] obj = new byte[8];

            System.arraycopy(Packet.intToByteArray(i, z), 0, obj, 0, 4);
            System.arraycopy(Packet.intToByteArray(i2, z), 0, obj, 4, 4);

            return obj;
        }
    }

    public static class SMsgAVIoctrlDeviceInfoReq
    {
        static byte[] reserved = new byte[4];

        public static byte[] parseContent()
        {
            return reserved;
        }
    }

    @SuppressWarnings("unused")
    public static class SMsgAVIoctrlDeviceInfoResp
    {
        public static final int V1_SIZE = 56;
        public static final int V2_EXTEND_SIZE = 256;
        public byte alarm_ring;
        public byte alarm_sensitivity;
        public byte baby_crying_mode;
        public int channel;
        public byte check_stat;
        public byte close_camera;
        public byte close_light;
        public byte day_night_mode;
        public byte frame_rate;
        public int free;
        public byte hardware_version;
        public byte interface_version;
        public byte internet_lossrate;
        public byte internet_visit;
        public byte is_utc_time;
        public byte language;
        public byte ldc_mode;
        public byte lossrate;
        public byte mic_mode;
        public SMsgAVIoctrlPTZInfoResp pizInfo;
        public List<Integer> presets;
        public byte record_mode;
        public byte reverse_mode;
        public byte talk_mode;
        public byte tfstat;
        public int total;
        public byte update_mode;
        public byte update_progress;
        public byte update_stat;
        public byte update_without_tf;
        private byte v1_alarm_ring;
        private byte v1_alarm_sensitivity;
        private byte v1_baby_crying_mode;
        private byte v1_day_night_mode;
        private byte v1_frame_rate;
        private byte v1_is_utc_time;
        private byte v1_ldc_mode;
        private byte v1_mic_mode;
        public byte v1_talk_mode;
        private byte v1_version_type;
        private byte v1_video_backup;
        public byte v2_alarm_mode;
        private byte v2_alarm_sensitivity;
        public byte v2_beep_mode;
        private byte v2_day_night_mode;
        public byte v2_extend_abnormal_sound = (byte) 0;
        public byte v2_extend_abnormal_sound_sensitivity = (byte) 0;
        private byte v2_extend_baby_crying_mode = (byte) 0;
        public byte v2_extend_gesture_mode = (byte) 0;
        private byte v2_extend_mic_mode = (byte) 0;
        public byte v2_extend_micboost_set = (byte) 0;
        public byte v2_extend_motion_roi = (byte) 0;
        public byte v2_extend_pgc_live = (byte) 0;
        public byte v2_extend_safe_remove_sd = (byte) 0;
        public byte v2_extend_upload_log = (byte) 0;
        public byte v2_extend_version_rollback = (byte) 0;
        private byte v2_extend_video_backup = (byte) 0;
        public byte v2_extend_video_talkmode = (byte) 0;
        public byte v2_extend_wifi_switch = (byte) 0;
        public byte v2_hd_resolution;
        private byte v2_is_extend;
        private byte v2_is_utc_time;
        private byte v2_ldc_mode;
        public byte v2_silent_upgrade;
        public byte v2_speaker_volume;
        private byte v2_version_type;
        public int version;
        public byte version_type;
        public byte video_backup;

        public static SMsgAVIoctrlDeviceInfoResp parse(byte[] bArr, boolean z)
        {
            SMsgAVIoctrlDeviceInfoResp sMsgAVIoctrlDeviceInfoResp = new SMsgAVIoctrlDeviceInfoResp();
            sMsgAVIoctrlDeviceInfoResp.interface_version = bArr[0];
            sMsgAVIoctrlDeviceInfoResp.lossrate = bArr[1];
            sMsgAVIoctrlDeviceInfoResp.tfstat = bArr[2];
            sMsgAVIoctrlDeviceInfoResp.internet_lossrate = bArr[3];
            sMsgAVIoctrlDeviceInfoResp.internet_visit = bArr[4];
            sMsgAVIoctrlDeviceInfoResp.check_stat = bArr[5];
            sMsgAVIoctrlDeviceInfoResp.update_without_tf = bArr[6];
            sMsgAVIoctrlDeviceInfoResp.language = bArr[7];
            sMsgAVIoctrlDeviceInfoResp.hardware_version = bArr[8];
            sMsgAVIoctrlDeviceInfoResp.version = Packet.byteArrayToInt(bArr, 32, z);
            sMsgAVIoctrlDeviceInfoResp.channel = Packet.byteArrayToInt(bArr, 36, z);
            sMsgAVIoctrlDeviceInfoResp.total = Packet.byteArrayToInt(bArr, 40, z);
            sMsgAVIoctrlDeviceInfoResp.free = Packet.byteArrayToInt(bArr, 44, z);
            sMsgAVIoctrlDeviceInfoResp.close_camera = bArr[48];
            sMsgAVIoctrlDeviceInfoResp.close_light = bArr[49];
            sMsgAVIoctrlDeviceInfoResp.update_stat = bArr[50];
            sMsgAVIoctrlDeviceInfoResp.update_progress = bArr[51];
            sMsgAVIoctrlDeviceInfoResp.record_mode = bArr[52];
            sMsgAVIoctrlDeviceInfoResp.update_mode = bArr[53];
            sMsgAVIoctrlDeviceInfoResp.reverse_mode = bArr[54];
            if (sMsgAVIoctrlDeviceInfoResp.hardware_version != (byte) 1)
            {
                sMsgAVIoctrlDeviceInfoResp.v1_is_utc_time = bArr[9];
                sMsgAVIoctrlDeviceInfoResp.v1_day_night_mode = bArr[10];
                sMsgAVIoctrlDeviceInfoResp.v1_alarm_sensitivity = bArr[11];
                sMsgAVIoctrlDeviceInfoResp.v1_version_type = bArr[12];
                sMsgAVIoctrlDeviceInfoResp.v1_video_backup = bArr[13];
                sMsgAVIoctrlDeviceInfoResp.v1_ldc_mode = bArr[14];
                sMsgAVIoctrlDeviceInfoResp.v1_baby_crying_mode = bArr[15];
                sMsgAVIoctrlDeviceInfoResp.v1_mic_mode = bArr[16];
                sMsgAVIoctrlDeviceInfoResp.v1_talk_mode = bArr[17];
                sMsgAVIoctrlDeviceInfoResp.v1_frame_rate = bArr[20];
                sMsgAVIoctrlDeviceInfoResp.v1_alarm_ring = bArr[23];
                sMsgAVIoctrlDeviceInfoResp.v2_version_type = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_day_night_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_hd_resolution = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_alarm_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_ldc_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_is_utc_time = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_alarm_sensitivity = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_beep_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_speaker_volume = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_is_extend = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_silent_upgrade = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_mic_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_baby_crying_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_gesture_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_motion_roi = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_safe_remove_sd = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_version_rollback = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_upload_log = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_wifi_switch = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_video_backup = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_video_talkmode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_micboost_set = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_pgc_live = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_abnormal_sound = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_extend_abnormal_sound_sensitivity = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.day_night_mode = sMsgAVIoctrlDeviceInfoResp.v1_day_night_mode;
                sMsgAVIoctrlDeviceInfoResp.is_utc_time = sMsgAVIoctrlDeviceInfoResp.v1_is_utc_time;
                sMsgAVIoctrlDeviceInfoResp.alarm_sensitivity = sMsgAVIoctrlDeviceInfoResp.v1_alarm_sensitivity;
                sMsgAVIoctrlDeviceInfoResp.version_type = sMsgAVIoctrlDeviceInfoResp.v1_version_type;
                sMsgAVIoctrlDeviceInfoResp.video_backup = sMsgAVIoctrlDeviceInfoResp.v1_video_backup;
                sMsgAVIoctrlDeviceInfoResp.ldc_mode = sMsgAVIoctrlDeviceInfoResp.v1_ldc_mode;
                sMsgAVIoctrlDeviceInfoResp.baby_crying_mode = sMsgAVIoctrlDeviceInfoResp.v1_baby_crying_mode;
                sMsgAVIoctrlDeviceInfoResp.mic_mode = sMsgAVIoctrlDeviceInfoResp.v1_mic_mode;
                sMsgAVIoctrlDeviceInfoResp.talk_mode = sMsgAVIoctrlDeviceInfoResp.v1_talk_mode;
                sMsgAVIoctrlDeviceInfoResp.frame_rate = sMsgAVIoctrlDeviceInfoResp.v1_frame_rate;
                sMsgAVIoctrlDeviceInfoResp.alarm_ring = sMsgAVIoctrlDeviceInfoResp.v1_alarm_ring;
                if ((sMsgAVIoctrlDeviceInfoResp.hardware_version == (byte) 2
                        || sMsgAVIoctrlDeviceInfoResp.hardware_version == (byte) 3
                        || sMsgAVIoctrlDeviceInfoResp.hardware_version == (byte) 20) && sMsgAVIoctrlDeviceInfoResp.interface_version >= (byte) 8)
                {
                    if (bArr.length >= 88)
                    {
                        byte[] obj = new byte[12];
                        System.arraycopy(bArr, 56, obj, 0, 12);
                        sMsgAVIoctrlDeviceInfoResp.presets = new ArrayList(Arrays.asList(SMsgAVIoctrlPTZPresetGETResp.parseDeviceInfo(obj, z)));
                        obj = new byte[20];
                        System.arraycopy(bArr, 68, obj, 0, 20);
                        sMsgAVIoctrlDeviceInfoResp.pizInfo = SMsgAVIoctrlPTZInfoResp.parse(obj, z);
                    }
                    else
                    {
                        sMsgAVIoctrlDeviceInfoResp.presets = new ArrayList();
                        sMsgAVIoctrlDeviceInfoResp.pizInfo = new SMsgAVIoctrlPTZInfoResp();
                    }
                }
            }
            else
            {
                sMsgAVIoctrlDeviceInfoResp.v1_is_utc_time = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_day_night_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_alarm_sensitivity = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_version_type = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.video_backup = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.alarm_ring = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_ldc_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_baby_crying_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_mic_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_talk_mode = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v1_alarm_ring = (byte) 0;
                sMsgAVIoctrlDeviceInfoResp.v2_version_type = bArr[9];
                sMsgAVIoctrlDeviceInfoResp.v2_day_night_mode = bArr[10];
                sMsgAVIoctrlDeviceInfoResp.v2_hd_resolution = bArr[11];
                sMsgAVIoctrlDeviceInfoResp.v2_alarm_mode = bArr[12];
                sMsgAVIoctrlDeviceInfoResp.v2_ldc_mode = bArr[13];
                sMsgAVIoctrlDeviceInfoResp.v2_is_utc_time = bArr[14];
                sMsgAVIoctrlDeviceInfoResp.v2_alarm_sensitivity = bArr[15];
                sMsgAVIoctrlDeviceInfoResp.v2_beep_mode = bArr[16];
                sMsgAVIoctrlDeviceInfoResp.v2_speaker_volume = bArr[17];
                sMsgAVIoctrlDeviceInfoResp.v2_is_extend = bArr[18];
                sMsgAVIoctrlDeviceInfoResp.v2_silent_upgrade = bArr[19];
                sMsgAVIoctrlDeviceInfoResp.day_night_mode = sMsgAVIoctrlDeviceInfoResp.v2_day_night_mode;
                sMsgAVIoctrlDeviceInfoResp.is_utc_time = sMsgAVIoctrlDeviceInfoResp.v2_is_utc_time;
                sMsgAVIoctrlDeviceInfoResp.alarm_sensitivity = sMsgAVIoctrlDeviceInfoResp.v2_alarm_sensitivity;
                sMsgAVIoctrlDeviceInfoResp.version_type = sMsgAVIoctrlDeviceInfoResp.v2_version_type;
                sMsgAVIoctrlDeviceInfoResp.ldc_mode = sMsgAVIoctrlDeviceInfoResp.v2_ldc_mode;
                if (sMsgAVIoctrlDeviceInfoResp.v2_is_extend > (byte) 0 && bArr.length >= 256)
                {
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_mic_mode = bArr[56];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_baby_crying_mode = bArr[57];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_gesture_mode = bArr[58];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_motion_roi = bArr[59];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_safe_remove_sd = bArr[60];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_version_rollback = bArr[61];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_upload_log = bArr[62];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_wifi_switch = bArr[63];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_video_backup = bArr[64];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_video_talkmode = bArr[65];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_pgc_live = bArr[68];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_micboost_set = bArr[69];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_abnormal_sound = bArr[70];
                    sMsgAVIoctrlDeviceInfoResp.v2_extend_abnormal_sound_sensitivity = bArr[71];
                    sMsgAVIoctrlDeviceInfoResp.baby_crying_mode = sMsgAVIoctrlDeviceInfoResp.v2_extend_baby_crying_mode;
                    sMsgAVIoctrlDeviceInfoResp.video_backup = sMsgAVIoctrlDeviceInfoResp.v2_extend_video_backup;
                    sMsgAVIoctrlDeviceInfoResp.mic_mode = sMsgAVIoctrlDeviceInfoResp.v2_extend_mic_mode;
                }
            }
            return sMsgAVIoctrlDeviceInfoResp;
        }
    }
    @SuppressWarnings("unused")
    public static class SMsgAVIoctrlPTZInfoResp
    {
        public byte cruiseMode;
        public byte curiseState;
        public int endTime;
        public byte motionTrackState;
        public int panoramicCruiseStayTime;
        public int presetCruiseStayTime;
        public int startTime;

        public static SMsgAVIoctrlPTZInfoResp parse(byte[] bArr, boolean z)
        {
            SMsgAVIoctrlPTZInfoResp sMsgAVIoctrlPTZInfoResp = new SMsgAVIoctrlPTZInfoResp();
            sMsgAVIoctrlPTZInfoResp.motionTrackState = bArr[0];
            sMsgAVIoctrlPTZInfoResp.curiseState = bArr[1];
            sMsgAVIoctrlPTZInfoResp.cruiseMode = bArr[2];
            sMsgAVIoctrlPTZInfoResp.presetCruiseStayTime = Packet.byteArrayToInt(bArr, 4, z);
            sMsgAVIoctrlPTZInfoResp.panoramicCruiseStayTime = Packet.byteArrayToInt(bArr, 8, z);
            sMsgAVIoctrlPTZInfoResp.startTime = Packet.byteArrayToInt(bArr, 12, z);
            sMsgAVIoctrlPTZInfoResp.endTime = Packet.byteArrayToInt(bArr, 16, z);
            return sMsgAVIoctrlPTZInfoResp;
        }
    }

    @SuppressWarnings("unused")
    public static class SMsgAVIoctrlPTZPresetGETResp
    {
        public short opResult;
        public int point_count;
        public short presetIndex;
        public Integer[] presets;

        public static SMsgAVIoctrlPTZPresetGETResp parse(byte[] bArr, boolean z)
        {
            int i = 0;

            Log.d(LOGTAG,"SMsgAVIoctrlPTZPresetGETResp: presets data=" + Packet.printByteArray(bArr, bArr.length));

            SMsgAVIoctrlPTZPresetGETResp sMsgAVIoctrlPTZPresetGETResp = new SMsgAVIoctrlPTZPresetGETResp();
            sMsgAVIoctrlPTZPresetGETResp.opResult = Packet.byteArrayToShort(bArr, 0, z);
            sMsgAVIoctrlPTZPresetGETResp.presetIndex = Packet.byteArrayToShort(bArr, 2, z);
            sMsgAVIoctrlPTZPresetGETResp.point_count = bArr[4];
            sMsgAVIoctrlPTZPresetGETResp.presets = new Integer[sMsgAVIoctrlPTZPresetGETResp.point_count];
            for (int i2 = 8; i2 < bArr.length; i2++)
            {
                if (bArr[i2] != (byte) 0 && i < sMsgAVIoctrlPTZPresetGETResp.point_count)
                {
                    int i3 = i + 1;
                    sMsgAVIoctrlPTZPresetGETResp.presets[i] = Integer.valueOf(bArr[i2]);
                    i = i3;
                }
            }
            return sMsgAVIoctrlPTZPresetGETResp;
        }

        public static Integer[] parseDeviceInfo(byte[] bArr, boolean z)
        {
            int i = 0;

            Log.d(LOGTAG,"SMsgAVIoctrlPTZPresetGETResp: presets deviceInfo data=" + Packet.printByteArray(bArr, bArr.length));

            byte b = bArr[0];
            Integer[] numArr = new Integer[b];
            for (int i2 = 4; i2 < bArr.length; i2++)
            {
                if (bArr[i2] != (byte) 0 && i < b)
                {
                    int i3 = i + 1;
                    numArr[i] = Integer.valueOf(bArr[i2]);
                    i = i3;
                }
            }
            return numArr;
        }
    }
}
