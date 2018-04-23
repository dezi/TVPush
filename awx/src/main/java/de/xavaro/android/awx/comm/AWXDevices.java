package de.xavaro.android.awx.comm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class AWXDevices
{
    public static final String MODEL_NAME_AWOX_AROMA_LIGHT = "AL-Bc7";
    public static final String MODEL_NAME_AWOX_SMART_LED_13W = "SML_w13";
    public static final String MODEL_NAME_AWOX_SMART_LED_6W_GU10 = "SML-w6-GU10";
    public static final String MODEL_NAME_AWOX_SMART_LED_7W = "SML_w7";
    public static final String MODEL_NAME_AWOX_SMART_LED_9W = "SML_w9";
    public static final String MODEL_NAME_AWOX_SMART_LED_COLOR_13W = "SML_c13";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_AMBIANCE_CUBE = "SML-CU40";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_AMBIANCE_SPHERE = "SML-SP40";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR = "SML_c9";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_GU10 = "SML-c4-GU10";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_GU53 = "SML-c4-GU53";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W = "SMLm-c13";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE = "SMLm-c13g";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = "SMLm-c13gi";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = "SMLm-c13gs";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_15W = "SMLm-c15";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_E14 = "SMLm-c5-E14";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_GU10 = "SMLm-c5-GU10";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W = "SMLm-c9";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = "SMLm-c9i";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = "SMLm-c9s";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_WHITE_GU10 = "SML-w4-GU10";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_WHITE_GU53 = "SML-w4-GU53";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_13W = "SMLm-w13";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_15W = "SMLm-w15";
    public static final String MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_9W = "SMLm-w9";
    public static final String MODEL_NAME_AWOX_SMART_PEBBLE_MOTION = "SMS-M";
    public static final String MODEL_NAME_AWOX_SMART_RCU = "RCUm";
    public static final String MODEL_NAME_AWOX_STRIIM_LIGHT_COLOR = "StriimLIGHT Color";
    public static final String MODEL_NAME_AWOX_STRIIM_LIGHT_MINI_COLOR = "StriimLIGHT mini Color";
    public static final String MODEL_NAME_AWOX_STRIIM_LIGHT_WIFI_COLOR = "SLCW13";
    public static final String MODEL_NAME_AWOX_STRIIM_LIGHT_WIFI_WHITE = "SLW10";
    public static final String MODEL_NAME_AWOX_STRIP_LED_COLOR = "SSL_c362";
    public static final String MODEL_NAME_BRIDGE_HUE = "HueBridge";
    public static final String MODEL_NAME_BRIDGE_OSRAM = "LightifyBridge";
    public static final String MODEL_NAME_ECCO_STRIP_LED_COLOR_PROTO_ELK_BLE = "ELK_BLE";
    public static final String MODEL_NAME_ECCO_STRIP_LED_COLOR_PROTO_RGB1024 = "RGB1024";
    public static final String MODEL_NAME_EGLO_CEILING_MESH_300mm = "ECeil_G30";
    public static final String MODEL_NAME_EGLO_CEILING_MESH_30W = "ECeiling_30";
    public static final String MODEL_NAME_EGLO_CEILING_MESH_385mm = "ECeil_G38";
    public static final String MODEL_NAME_EGLO_DOWNLIGHT_MESH = "EMod_Ceil";
    public static final String MODEL_NAME_EGLO_OUTDOOR_WHITE_WARM_MESH_14W = "EOutdoor_w14w";
    public static final String MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x120 = "EPanel_120";
    public static final String MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x30 = "EPanel_300";
    public static final String MODEL_NAME_EGLO_PANEL_COLOR_MESH_45x45 = "EPanel_450";
    public static final String MODEL_NAME_EGLO_PANEL_COLOR_MESH_60x60 = "EPanel_600";
    public static final String MODEL_NAME_EGLO_PANEL_COLOR_MESH_62x62 = "EPanel_620";
    public static final String MODEL_NAME_EGLO_PENDANT_MESH_20W = "EPendant_20";
    public static final String MODEL_NAME_EGLO_PENDANT_MESH_30W = "EPendant_30";
    public static final String MODEL_NAME_EGLO_PIR = "EPIRm";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE = "ESMLm-c13g";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = "ESMLm-c13gi";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = "ESMLm-c13gs";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_E14 = "ESMLm-c5-E14";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_GU10 = "ESMLm-c5-GU10";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W = "ESMLm-c9";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = "ESMLm-c9i";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = "ESMLm-c9s";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_WHITE_NEUTRAL_MESH_9W = "ESMLm-w9n";
    public static final String MODEL_NAME_EGLO_SMART_LIGHT_WHITE_WARM_MESH_9W = "ESMLm-w9w";
    public static final String MODEL_NAME_EGLO_SMART_RCU = "ERCUm";
    public static final String MODEL_NAME_EGLO_SPOT_MESH_120W = "ESpot_120";
    public static final String MODEL_NAME_EGLO_SPOT_MESH_170W = "ESpot_170";
    public static final String MODEL_NAME_EGLO_SPOT_MESH_225W = "ESpot_225";
    public static final String MODEL_NAME_EGLO_STRIP_MESH_3m = "EStrip_3m";
    public static final String MODEL_NAME_EGLO_STRIP_MESH_5m = "EStrip_5m";
    public static final String MODEL_NAME_EGLO_SURFACE_ROUND_MESH_225 = "EFueva_225r";
    public static final String MODEL_NAME_EGLO_SURFACE_ROUND_MESH_300 = "EFueva_300r";
    public static final String MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_225 = "EFueva_225s";
    public static final String MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_300 = "EFueva_300s";
    public static final String MODEL_NAME_EGLO_TRI_SPOT_MESH_85mm = "ETriSpot_85";
    public static final String MODEL_NAME_GENERIC_MESH_DEVICE = "GENLm";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE = "KSMLm-c13g";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = "KSMLm-c13gi";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = "KSMLm-c13gs";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_E14 = "KSMLm-c5-E14";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_GU10 = "KSMLm-c5-GU10";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W = "KSMLm-c9";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = "KSMLm-c9i";
    public static final String MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = "KSMLm-c9s";
    public static final String MODEL_NAME_KERIA_STRIP_LED_COLOR = "KSL_c362";
    public static final String MODEL_NAME_LEDE_SMART_LIGHT = "SimpleBLEPeripheral";
    public static final String MODEL_NAME_LEDE_SMART_LIGHT_COLOR = "SML-c9";
    public static final String MODEL_NAME_LEDE_SMART_LIGHT_WHITE = "SML-w7";
    public static final String MODEL_NAME_LIGHT_HUE_COLOR = "HueColor";
    public static final String MODEL_NAME_LIGHT_HUE_WHITE = "HueWhite";
    public static final String MODEL_NAME_LIGHT_OSRAM_COLOR = "LightifyColor";
    public static final String MODEL_NAME_LIGHT_OSRAM_WHITE = "LightifyWhite";
    public static final String MODEL_NAME_REVOGI_SMART_PLUG_B13_UK = "SMP-B13-UK";
    public static final String MODEL_NAME_REVOGI_SMART_PLUG_B16_FR = "SMP-B16-FR";
    public static final String MODEL_NAME_REVOGI_SMART_PLUG_B16_GR = "SMP-B16-GR";
    public static final String MODEL_NAME_REVOGI_SMART_PLUG_PROTO_AWOXPLUG = "AWOXPLUG";
    public static final String MODEL_NAME_REVOGI_SMART_PLUG_PROTO_VEMITER = "VEMITER";
    public static final String MODEL_NAME_SCHNEIDER_DIMMER = "SCH-D";
    public static final String MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W = "SSMLm-c9";
    public static final String MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = "SSMLm-c9i";
    public static final String MODEL_NAME_SCHNEIDER_SMART_LIGHT_WHITE_MESH_9W = "SSMLm-w9";
    public static final String MODEL_NAME_SMART_PLUG_KERIA = "KSMP-B16-FR";
    public static final String MODEL_NAME_SMART_PLUG_MESH_FR = "ESMP-Bm10-FR";
    public static final String MODEL_NAME_SMART_PLUG_MESH_GE = "ESMP-Bm10-GE";

    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W = new byte[]{(byte) 0, (byte) 21};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE = new byte[]{(byte) 0, (byte) 43};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = new byte[]{(byte) 0, (byte) 89};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = new byte[]{(byte) 0, (byte) 68};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_15W = new byte[]{(byte) 0, (byte) 23};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_5W_E14 = new byte[]{(byte) 0, (byte) 56};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_5W_GU10 = new byte[]{(byte) 0, (byte) 55};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W = new byte[]{(byte) 0, (byte) 19};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = new byte[]{(byte) 0, (byte) 83};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = new byte[]{(byte) 0, (byte) 67};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_13W = new byte[]{(byte) 0, (byte) 20};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_15W = new byte[]{(byte) 0, (byte) 22};
    public static final byte[] PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_9W = new byte[]{(byte) 0, (byte) 18};
    public static final byte[] PRODUCT_ID_AWOX_SMART_RCU = new byte[]{(byte) 0, (byte) 32};
    public static final byte[] PRODUCT_ID_EGLO_CEILING_MESH_300mm = new byte[]{(byte) 0, (byte) 53};
    public static final byte[] PRODUCT_ID_EGLO_CEILING_MESH_30W = new byte[]{(byte) 0, (byte) 75};
    public static final byte[] PRODUCT_ID_EGLO_CEILING_MESH_385mm = new byte[]{(byte) 0, (byte) 54};
    public static final byte[] PRODUCT_ID_EGLO_DOWNLIGHT_MESH = new byte[]{(byte) 0, (byte) 39};
    public static final byte[] PRODUCT_ID_EGLO_OUTDOOR_WHITE_WARM_MESH_14W = new byte[]{(byte) 0, (byte) 80};
    public static final byte[] PRODUCT_ID_EGLO_PANEL_COLOR_MESH_30x120 = new byte[]{(byte) 0, (byte) 48};
    public static final byte[] PRODUCT_ID_EGLO_PANEL_COLOR_MESH_30x30 = new byte[]{(byte) 0, (byte) 37};
    public static final byte[] PRODUCT_ID_EGLO_PANEL_COLOR_MESH_45x45 = new byte[]{(byte) 0, (byte) 87};
    public static final byte[] PRODUCT_ID_EGLO_PANEL_COLOR_MESH_60x60 = new byte[]{(byte) 0, (byte) 38};
    public static final byte[] PRODUCT_ID_EGLO_PANEL_COLOR_MESH_62x62 = new byte[]{(byte) 0, (byte) 86};
    public static final byte[] PRODUCT_ID_EGLO_PENDANT_MESH_20W = new byte[]{(byte) 0, (byte) 77};
    public static final byte[] PRODUCT_ID_EGLO_PENDANT_MESH_30W = new byte[]{(byte) 0, (byte) 76};
    public static final byte[] PRODUCT_ID_EGLO_PIR = new byte[]{(byte) 0, (byte) 88};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE = new byte[]{(byte) 0, (byte) 41};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = new byte[]{(byte) 0, (byte) 90};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = new byte[]{(byte) 0, (byte) 70};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_5W_E14 = new byte[]{(byte) 0, (byte) 61};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_5W_GU10 = new byte[]{(byte) 0, (byte) 60};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W = new byte[]{(byte) 0, (byte) 35};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = new byte[]{(byte) 0, (byte) 84};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = new byte[]{(byte) 0, (byte) 69};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_WHITE_NEUTRAL_MESH_9W = new byte[]{(byte) 0, (byte) 74};
    public static final byte[] PRODUCT_ID_EGLO_SMART_LIGHT_WHITE_WARM_MESH_9W = new byte[]{(byte) 0, (byte) 73};
    public static final byte[] PRODUCT_ID_EGLO_SMART_RCU = new byte[]{(byte) 0, (byte) 40};
    public static final byte[] PRODUCT_ID_EGLO_SPOT_MESH_120W = new byte[]{(byte) 0, (byte) 50};
    public static final byte[] PRODUCT_ID_EGLO_SPOT_MESH_170W = new byte[]{(byte) 0, (byte) 51};
    public static final byte[] PRODUCT_ID_EGLO_SPOT_MESH_225W = new byte[]{(byte) 0, (byte) 52};
    public static final byte[] PRODUCT_ID_EGLO_STRIP_MESH_3m = new byte[]{(byte) 0, (byte) 78};
    public static final byte[] PRODUCT_ID_EGLO_STRIP_MESH_5m = new byte[]{(byte) 0, (byte) 79};
    public static final byte[] PRODUCT_ID_EGLO_SURFACE_ROUND_MESH_225 = new byte[]{(byte) 0, (byte) 63};
    public static final byte[] PRODUCT_ID_EGLO_SURFACE_ROUND_MESH_300 = new byte[]{(byte) 0, (byte) 65};
    public static final byte[] PRODUCT_ID_EGLO_SURFACE_SQUARE_MESH_225 = new byte[]{(byte) 0, (byte) 64};
    public static final byte[] PRODUCT_ID_EGLO_SURFACE_SQUARE_MESH_300 = new byte[]{(byte) 0, (byte) 66};
    public static final byte[] PRODUCT_ID_EGLO_TRI_SPOT_MESH_85mm = new byte[]{(byte) 0, (byte) 81};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE = new byte[]{(byte) 0, (byte) 42};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH = new byte[]{(byte) 0, (byte) 91};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING = new byte[]{(byte) 0, (byte) 72};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_5W_E14 = new byte[]{(byte) 0, (byte) 59};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_5W_GU10 = new byte[]{(byte) 0, (byte) 58};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W = new byte[]{(byte) 0, (byte) 36};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = new byte[]{(byte) 0, (byte) 85};
    public static final byte[] PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING = new byte[]{(byte) 0, (byte) 71};
    public static final byte[] PRODUCT_ID_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W = new byte[]{(byte) 0, (byte) 34};
    public static final byte[] PRODUCT_ID_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W_INNOTECH = new byte[]{(byte) 0, (byte) 92};
    public static final byte[] PRODUCT_ID_SCHNEIDER_SMART_LIGHT_WHITE_MESH_9W = new byte[]{(byte) 0, (byte) 33};
    public static final byte[] PRODUCT_ID_SMART_PLUG_MESH_FR = new byte[]{(byte) 0, (byte) 98};
    public static final byte[] PRODUCT_ID_SMART_PLUG_MESH_GE = new byte[]{(byte) 0, (byte) 99};

    public static final String PROPERTY_ALARMS = "alarms";
    public static final String PROPERTY_BATTERY_LEVEL = "battery_level";
    public static final String PROPERTY_COLOR = "color";
    public static final String PROPERTY_COLOR_BRIGHTNESS = "color_brightness";
    public static final String PROPERTY_COLOR_SEQUENCE_COLORS = "color_sequence_colors";
    public static final String PROPERTY_COLOR_SEQUENCE_COLOR_DURATION = "color_sequence_color_duration";
    public static final String PROPERTY_COLOR_SEQUENCE_FADE_DURATION = "color_sequence_fade_duration";
    public static final String PROPERTY_COLOR_SEQUENCE_PRESET = "color_sequence_preset";
    public static final String PROPERTY_DAWN_SIMULATOR = "dawn_simulator";
    public static final String PROPERTY_FAN_SPEED = "fan_speed";
    public static final String PROPERTY_FAN_TIMER_OFF_DELAY = "fan_timer_off_delay";
    public static final String PROPERTY_FAN_TIMER_ON_DELAY = "fan_timer_on_delay";
    public static final String PROPERTY_FRIENDLY_NAME = "friendly_name";
    public static final String PROPERTY_LIGHT_MODE = "light_mode";
    public static final String PROPERTY_MESH_ADDRESS = "mesh_address";
    public static final String PROPERTY_MESH_GROUPS = "mesh_groups";
    public static final String PROPERTY_MESH_NETWORK = "mesh_network";
    public static final String PROPERTY_MESH_OTA = "mesh_ota";
    public static final String PROPERTY_NIGHTLIGHT = "nightlight";
    public static final String PROPERTY_OTA_IMAGE_BLOCK = "oad_image_block";
    public static final String PROPERTY_OTA_IMAGE_SET_IDENTIFY = "oad_image_set_identify";
    public static final String PROPERTY_OTA_IMAGE_START_UPGRADE = "ota_image_start_upgrade";
    public static final String PROPERTY_PIR_CURRENT_LUMINOSITY = "pir_current_luminosity";
    public static final String PROPERTY_PIR_SETTINGS = "pir_settings";
    public static final String PROPERTY_PLUG_LED_STATE = "plug_led";
    public static final String PROPERTY_PLUG_MESH_SCHEDULE = "plug_mesh_schedule";
    public static final String PROPERTY_PLUG_SCHEDULE = "plug_schedule";
    public static final String PROPERTY_POWER_CONSUMPTION = "power_consumption";
    public static final String PROPERTY_POWER_CONSUMPTION_DAILY = "power_consumption_daily";
    public static final String PROPERTY_POWER_CONSUMPTION_HISTORY_MESH = "power_consumption_history_mesh";
    public static final String PROPERTY_POWER_CONSUMPTION_HOURLY = "power_consumption_hourly";
    public static final String PROPERTY_POWER_CONSUMPTION_MESH = "power_consumption_mesh";
    public static final String PROPERTY_POWER_CONSUMPTION_MONTHLY = "power_consumption_monthly";
    public static final String PROPERTY_POWER_STATE = "power_state";
    public static final String PROPERTY_PRESENCE_SIMULATOR = "presence_simulator";
    public static final String PROPERTY_PROGRAM = "program";
    public static final String PROPERTY_RESCUE_PEBBLE = "rescue_pebble";
    public static final String PROPERTY_REVOGI_FACTORY_RESET = "revogi_factory_reset";
    public static final String PROPERTY_REVOGI_PRE_AND_POST_UPDATE = "revogi_pre_post_update";
    public static final String PROPERTY_REVOGI_READ = "revogi_read";
    public static final String PROPERTY_REVOGI_UPDATE = "revogi_update";
    public static final String PROPERTY_SCENES = "scenes";
    public static final String PROPERTY_SWITCH_BINDING = "switch_binding";
    public static final String PROPERTY_SWITCH_COMMAND = "switch_command";
    public static final String PROPERTY_SWITCH_GROUP_1 = "switch_group_1";
    public static final String PROPERTY_SWITCH_GROUP_2 = "switch_group_2";
    public static final String PROPERTY_SWITCH_PRESET_LIST_1 = "switch_preset_1";
    public static final String PROPERTY_SWITCH_PRESET_LIST_2 = "switch_preset_2";
    public static final String PROPERTY_SWITCH_PRESET_LIST_3 = "switch_preset_3";
    public static final String PROPERTY_SWITCH_PRESET_LIST_4 = "switch_preset_4";
    public static final String PROPERTY_SWITCH_PRESET_LIST_5 = "switch_preset_5";
    public static final String PROPERTY_SWITCH_PRESET_LIST_6 = "switch_preset_6";
    public static final String PROPERTY_SWITCH_PRESET_LIST_7 = "switch_preset_7";
    public static final String PROPERTY_TIME = "time";
    public static final String PROPERTY_TIMER = "timer";
    public static final String PROPERTY_TIMER_OFF_DELAY = "timer_off_delay";
    public static final String PROPERTY_TIMER_OFF_FADE_DURATION = "timer_off_fade_duration";
    public static final String PROPERTY_TIMER_ON_DELAY = "timer_on_delay";
    public static final String PROPERTY_TIMER_ON_FADE_DURATION = "timer_on_fade_duration";
    public static final String PROPERTY_WHITE_BRIGHTNESS = "white_brightness";
    public static final String PROPERTY_WHITE_TEMPERATURE = "white_temperature";

    private static Map<String, String> device2friendly;

    static
    {
        device2friendly = new HashMap<>();

        for (Field field : AWXDevices.class.getFields())
        {
            if (field.getName().startsWith("MODEL_NAME"))
            {
                try
                {   String name = field.get(null).toString();

                    String search = name.replace("-", "_").toLowerCase();

                    device2friendly.put(search, name);
                }
                catch (IllegalAccessException ignore)
                {
                }
            }
        }
    }

    public static String getFriendlyName(String model)
    {
        String search = model.replace("-", "_").toLowerCase();
        String name = device2friendly.get(search);
        return (name != null) ? name : model;
    }

    public static String getModelNameFromProductId(byte[] productId)
    {
        if (productId != null)
        {
            productId[0] = AWXByteUtils.setBitInByte(productId[0], 4, false);
        }

        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_9W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_13W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_13W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_WHITE_MESH_15W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_15W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_15W))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_15W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_5W_E14))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_E14;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_5W_GU10))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_GU10;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_RCU))
        {
            return MODEL_NAME_AWOX_SMART_RCU;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PANEL_COLOR_MESH_30x120))
        {
            return MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x120;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PANEL_COLOR_MESH_30x30))
        {
            return MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x30;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PANEL_COLOR_MESH_60x60))
        {
            return MODEL_NAME_EGLO_PANEL_COLOR_MESH_60x60;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SPOT_MESH_120W))
        {
            return MODEL_NAME_EGLO_SPOT_MESH_120W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SPOT_MESH_170W))
        {
            return MODEL_NAME_EGLO_SPOT_MESH_170W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SPOT_MESH_225W))
        {
            return MODEL_NAME_EGLO_SPOT_MESH_225W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_CEILING_MESH_300mm))
        {
            return MODEL_NAME_EGLO_CEILING_MESH_300mm;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_CEILING_MESH_385mm))
        {
            return MODEL_NAME_EGLO_CEILING_MESH_385mm;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_DOWNLIGHT_MESH))
        {
            return MODEL_NAME_EGLO_DOWNLIGHT_MESH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_5W_E14))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_E14;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_5W_GU10))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_GU10;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SURFACE_ROUND_MESH_225))
        {
            return MODEL_NAME_EGLO_SURFACE_ROUND_MESH_225;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SURFACE_ROUND_MESH_300))
        {
            return MODEL_NAME_EGLO_SURFACE_ROUND_MESH_300;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SURFACE_SQUARE_MESH_225))
        {
            return MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_225;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SURFACE_SQUARE_MESH_300))
        {
            return MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_300;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_WHITE_WARM_MESH_9W))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_WHITE_WARM_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_WHITE_NEUTRAL_MESH_9W))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_WHITE_NEUTRAL_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_RCU))
        {
            return MODEL_NAME_EGLO_SMART_RCU;
        }
        if (Arrays.equals(productId, PRODUCT_ID_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W))
        {
            return MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_5W_E14))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_E14;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_5W_GU10))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_GU10;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_CEILING_MESH_30W))
        {
            return MODEL_NAME_EGLO_CEILING_MESH_30W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PENDANT_MESH_30W))
        {
            return MODEL_NAME_EGLO_PENDANT_MESH_30W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PENDANT_MESH_20W))
        {
            return MODEL_NAME_EGLO_PENDANT_MESH_20W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_STRIP_MESH_3m))
        {
            return MODEL_NAME_EGLO_STRIP_MESH_3m;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_STRIP_MESH_5m))
        {
            return MODEL_NAME_EGLO_STRIP_MESH_5m;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_OUTDOOR_WHITE_WARM_MESH_14W))
        {
            return MODEL_NAME_EGLO_OUTDOOR_WHITE_WARM_MESH_14W;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_TRI_SPOT_MESH_85mm))
        {
            return MODEL_NAME_EGLO_TRI_SPOT_MESH_85mm;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
        {
            return MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PANEL_COLOR_MESH_62x62))
        {
            return MODEL_NAME_EGLO_PANEL_COLOR_MESH_62x62;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PANEL_COLOR_MESH_45x45))
        {
            return MODEL_NAME_EGLO_PANEL_COLOR_MESH_45x45;
        }
        if (Arrays.equals(productId, PRODUCT_ID_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
        {
            return MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
        {
            return MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
        {
            return MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH;
        }
        if (Arrays.equals(productId, PRODUCT_ID_EGLO_PIR))
        {
            return MODEL_NAME_EGLO_PIR;
        }
        if (Arrays.equals(productId, PRODUCT_ID_SMART_PLUG_MESH_FR))
        {
            return MODEL_NAME_SMART_PLUG_MESH_FR;
        }
        if (Arrays.equals(productId, PRODUCT_ID_SMART_PLUG_MESH_GE))
        {
            return MODEL_NAME_SMART_PLUG_MESH_GE;
        }

        return MODEL_NAME_GENERIC_MESH_DEVICE;
    }

    public static ArrayList<String> getProperties(String model)
    {
        ArrayList<String> properties = new ArrayList<>();

        int obj = -1;

        switch (model.hashCode())
        {
            case -1926896425:
                if (model.equals(MODEL_NAME_EGLO_SPOT_MESH_120W))
                {
                    obj = 45;
                    break;
                }
                break;
            case -1926896270:
                if (model.equals(MODEL_NAME_EGLO_SPOT_MESH_170W))
                {
                    obj = 46;
                    break;
                }
                break;
            case -1926895459:
                if (model.equals(MODEL_NAME_EGLO_SPOT_MESH_225W))
                {
                    obj = 47;
                    break;
                }
                break;
            case -1894505236:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
                {
                    obj = 74;
                    break;
                }
                break;
            case -1894505226:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
                {
                    obj = 64;
                    break;
                }
                break;
            case -1846478993:
                if (model.equals(MODEL_NAME_AWOX_STRIIM_LIGHT_WIFI_COLOR))
                {
                    obj = 27;
                    break;
                }
                break;
            case -1845326159:
                if (model.equals(MODEL_NAME_LEDE_SMART_LIGHT_COLOR))
                {
                    obj = 2;
                    break;
                }
                break;
            case -1845325541:
                if (model.equals(MODEL_NAME_LEDE_SMART_LIGHT_WHITE))
                {
                    obj = 1;
                    break;
                }
                break;
            case -1845278109:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR))
                {
                    obj = 16;
                    break;
                }
                break;
            case -1845277491:
                if (model.equals(MODEL_NAME_AWOX_SMART_LED_7W))
                {
                    obj = 9;
                    break;
                }
                break;
            case -1845277489:
                if (model.equals(MODEL_NAME_AWOX_SMART_LED_9W))
                {
                    obj = 10;
                    break;
                }
                break;
            case -1716015697:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
                {
                    obj = 41;
                    break;
                }
                break;
            case -1656878950:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
                {
                    obj = 81;
                    break;
                }
                break;
            case -1656878940:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
                {
                    obj = 58;
                    break;
                }
                break;
            case -1582860940:
                if (model.equals(MODEL_NAME_SMART_PLUG_KERIA))
                {
                    obj = 8;
                    break;
                }
                break;
            case -1578519744:
                if (model.equals(MODEL_NAME_AWOX_STRIIM_LIGHT_MINI_COLOR))
                {
                    obj = 24;
                    break;
                }
                break;
            case -1373851165:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W))
                {
                    obj = 40;
                    break;
                }
                break;
            case -1369046728:
                if (model.equals(MODEL_NAME_AWOX_SMART_LED_COLOR_13W))
                {
                    obj = 15;
                    break;
                }
                break;
            case -1369027508:
                if (model.equals(MODEL_NAME_AWOX_SMART_LED_13W))
                {
                    obj = 11;
                    break;
                }
                break;
            case -1368679992:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W))
                {
                    obj = 32;
                    break;
                }
                break;
            case -1368679372:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_9W))
                {
                    obj = 82;
                    break;
                }
                break;
            case -1177672160:
                if (model.equals(MODEL_NAME_EGLO_SURFACE_ROUND_MESH_225))
                {
                    obj = 53;
                    break;
                }
                break;
            case -1177672159:
                if (model.equals(MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_225))
                {
                    obj = 55;
                    break;
                }
                break;
            case -1177644446:
                if (model.equals(MODEL_NAME_EGLO_SURFACE_ROUND_MESH_300))
                {
                    obj = 54;
                    break;
                }
                break;
            case -1177644445:
                if (model.equals(MODEL_NAME_EGLO_SURFACE_SQUARE_MESH_300))
                {
                    obj = 56;
                    break;
                }
                break;
            case -1098838513:
                if (model.equals(MODEL_NAME_EGLO_CEILING_MESH_300mm))
                {
                    obj = 48;
                    break;
                }
                break;
            case -1098838505:
                if (model.equals(MODEL_NAME_EGLO_CEILING_MESH_385mm))
                {
                    obj = 49;
                    break;
                }
                break;
            case -1041485740:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
                {
                    obj = 34;
                    break;
                }
                break;
            case -938779904:
                if (model.equals(MODEL_NAME_ECCO_STRIP_LED_COLOR_PROTO_ELK_BLE))
                {
                    obj = 30;
                    break;
                }
                break;
            case -885348685:
                if (model.equals(MODEL_NAME_SMART_PLUG_MESH_FR))
                {
                    obj = 96;
                    break;
                }
                break;
            case -885348667:
                if (model.equals(MODEL_NAME_SMART_PLUG_MESH_GE))
                {
                    obj = 97;
                    break;
                }
                break;
            case -854289219:
                if (model.equals(MODEL_NAME_LIGHT_OSRAM_COLOR))
                {
                    obj = 92;
                    break;
                }
                break;
            case -836030077:
                if (model.equals(MODEL_NAME_LIGHT_OSRAM_WHITE))
                {
                    obj = 94;
                    break;
                }
                break;
            case -752763771:
                if (model.equals(MODEL_NAME_LEDE_SMART_LIGHT))
                {
                    obj = 0;
                    break;
                }
                break;
            case -721457241:
                if (model.equals(MODEL_NAME_EGLO_DOWNLIGHT_MESH))
                {
                    obj = 50;
                    break;
                }
                break;
            case -679253490:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_GU10))
                {
                    obj = 19;
                    break;
                }
                break;
            case -679253363:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_GU53))
                {
                    obj = 20;
                    break;
                }
                break;
            case -676825772:
                if (model.equals(MODEL_NAME_EGLO_TRI_SPOT_MESH_85mm))
                {
                    obj = 71;
                    break;
                }
                break;
            case -480217663:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_GU10))
                {
                    obj = 63;
                    break;
                }
                break;
            case -432604499:
                if (model.equals(MODEL_NAME_REVOGI_SMART_PLUG_PROTO_AWOXPLUG))
                {
                    obj = 3;
                    break;
                }
                break;
            case -395699587:
                if (model.equals(MODEL_NAME_AWOX_STRIIM_LIGHT_COLOR))
                {
                    obj = 25;
                    break;
                }
                break;
            case -267250714:
                if (model.equals(MODEL_NAME_EGLO_PENDANT_MESH_20W))
                {
                    obj = 68;
                    break;
                }
                break;
            case -267250683:
                if (model.equals(MODEL_NAME_EGLO_PENDANT_MESH_30W))
                {
                    obj = 67;
                    break;
                }
                break;
            case -152767825:
                if (model.equals(MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x120))
                {
                    obj = 42;
                    break;
                }
                break;
            case -152765965:
                if (model.equals(MODEL_NAME_EGLO_PANEL_COLOR_MESH_30x30))
                {
                    obj = 43;
                    break;
                }
                break;
            case -152764849:
                if (model.equals(MODEL_NAME_EGLO_PANEL_COLOR_MESH_45x45))
                {
                    obj = 77;
                    break;
                }
                break;
            case -152763082:
                if (model.equals(MODEL_NAME_EGLO_PANEL_COLOR_MESH_60x60))
                {
                    obj = 44;
                    break;
                }
                break;
            case -152763020:
                if (model.equals(MODEL_NAME_EGLO_PANEL_COLOR_MESH_62x62))
                {
                    obj = 76;
                    break;
                }
                break;
            case -136931937:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_E14))
                {
                    obj = 36;
                    break;
                }
                break;
            case -109049054:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_WHITE_GU10))
                {
                    obj = 13;
                    break;
                }
                break;
            case -109048927:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_WHITE_GU53))
                {
                    obj = 14;
                    break;
                }
                break;
            case -51790752:
                if (model.equals(MODEL_NAME_AWOX_SMART_LED_6W_GU10))
                {
                    obj = 12;
                    break;
                }
                break;
            case 2509993:
                if (model.equals(MODEL_NAME_AWOX_SMART_RCU))
                {
                    obj = 89;
                    break;
                }
                break;
            case 44415894:
                if (model.equals(MODEL_NAME_REVOGI_SMART_PLUG_B13_UK))
                {
                    obj = 7;
                    break;
                }
                break;
            case 44504809:
                if (model.equals(MODEL_NAME_REVOGI_SMART_PLUG_B16_FR))
                {
                    obj = 5;
                    break;
                }
                break;
            case 44504840:
                if (model.equals(MODEL_NAME_REVOGI_SMART_PLUG_B16_GR))
                {
                    obj = 6;
                    break;
                }
                break;
            case 50171382:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_5W_GU10))
                {
                    obj = 37;
                    break;
                }
                break;
            case 66179033:
                if (model.equals(MODEL_NAME_EGLO_PIR))
                {
                    obj = 91;
                    break;
                }
                break;
            case 66232942:
                if (model.equals(MODEL_NAME_EGLO_SMART_RCU))
                {
                    obj = 90;
                    break;
                }
                break;
            case 67702993:
                if (model.equals(MODEL_NAME_GENERIC_MESH_DEVICE))
                {
                    obj = 78;
                    break;
                }
                break;
            case 78718895:
                if (model.equals(MODEL_NAME_SCHNEIDER_DIMMER))
                {
                    obj = 29;
                    break;
                }
                break;
            case 79001533:
                if (model.equals(MODEL_NAME_AWOX_STRIIM_LIGHT_WIFI_WHITE))
                {
                    obj = 26;
                    break;
                }
                break;
            case 79027385:
                if (model.equals(MODEL_NAME_AWOX_SMART_PEBBLE_MOTION))
                {
                    obj = 28;
                    break;
                }
                break;
            case 179841082:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_E14))
                {
                    obj = 51;
                    break;
                }
                break;
            case 337331749:
                if (model.equals(MODEL_NAME_EGLO_OUTDOOR_WHITE_WARM_MESH_14W))
                {
                    obj = 88;
                    break;
                }
                break;
            case 348875115:
                if (model.equals(MODEL_NAME_LIGHT_HUE_COLOR))
                {
                    obj = 93;
                    break;
                }
                break;
            case 360286950:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
                {
                    obj = 73;
                    break;
                }
                break;
            case 360286960:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
                {
                    obj = 57;
                    break;
                }
                break;
            case 360306175:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_WHITE_NEUTRAL_MESH_9W))
                {
                    obj = 87;
                    break;
                }
                break;
            case 360306184:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_WHITE_WARM_MESH_9W))
                {
                    obj = 86;
                    break;
                }
                break;
            case 367134257:
                if (model.equals(MODEL_NAME_LIGHT_HUE_WHITE))
                {
                    obj = 95;
                    break;
                }
                break;
            case 446314784:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
                {
                    obj = 80;
                    break;
                }
                break;
            case 446314794:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
                {
                    obj = 65;
                    break;
                }
                break;
            case 462129705:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_AMBIANCE_CUBE))
                {
                    obj = 21;
                    break;
                }
                break;
            case 462601556:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_AMBIANCE_SPHERE))
                {
                    obj = 22;
                    break;
                }
                break;
            case 481700455:
                if (model.equals(MODEL_NAME_KERIA_STRIP_LED_COLOR))
                {
                    obj = 18;
                    break;
                }
                break;
            case 493076253:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_9W))
                {
                    obj = 60;
                    break;
                }
                break;
            case 520593011:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W))
                {
                    obj = 33;
                    break;
                }
                break;
            case 520593013:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_15W))
                {
                    obj = 35;
                    break;
                }
                break;
            case 520593313:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
                {
                    obj = 72;
                    break;
                }
                break;
            case 520593323:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_9W_SEELIGHTING))
                {
                    obj = 38;
                    break;
                }
                break;
            case 520612231:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_13W))
                {
                    obj = 83;
                    break;
                }
                break;
            case 520612233:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_WHITE_MESH_15W))
                {
                    obj = 84;
                    break;
                }
                break;
            case 825728244:
                if (model.equals(MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W_INNOTECH))
                {
                    obj = 75;
                    break;
                }
                break;
            case 902048580:
                if (model.equals(MODEL_NAME_EGLO_CEILING_MESH_30W))
                {
                    obj = 66;
                    break;
                }
                break;
            case 954337396:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_5W_E14))
                {
                    obj = 62;
                    break;
                }
                break;
            case 1064685462:
                if (model.equals(MODEL_NAME_REVOGI_SMART_PLUG_PROTO_VEMITER))
                {
                    obj = 4;
                    break;
                }
                break;
            case 1280200379:
                if (model.equals(MODEL_NAME_EGLO_SMART_LIGHT_COLOR_MESH_5W_GU10))
                {
                    obj = 52;
                    break;
                }
                break;
            case 1399870569:
                if (model.equals(MODEL_NAME_KERIA_SMART_LIGHT_COLOR_MESH_13W_GLOBE))
                {
                    obj = 61;
                    break;
                }
                break;
            case 1539281247:
                if (model.equals(MODEL_NAME_AWOX_STRIP_LED_COLOR))
                {
                    obj = 17;
                    break;
                }
                break;
            case 1550657045:
                if (model.equals(MODEL_NAME_SCHNEIDER_SMART_LIGHT_COLOR_MESH_9W))
                {
                    obj = 59;
                    break;
                }
                break;
            case 1550657665:
                if (model.equals(MODEL_NAME_SCHNEIDER_SMART_LIGHT_WHITE_MESH_9W))
                {
                    obj = 85;
                    break;
                }
                break;
            case 1699397766:
                if (model.equals(MODEL_NAME_EGLO_STRIP_MESH_3m))
                {
                    obj = 69;
                    break;
                }
                break;
            case 1699397828:
                if (model.equals(MODEL_NAME_EGLO_STRIP_MESH_5m))
                {
                    obj = 70;
                    break;
                }
                break;
            case 1855987406:
                if (model.equals(MODEL_NAME_ECCO_STRIP_LED_COLOR_PROTO_RGB1024))
                {
                    obj = 31;
                    break;
                }
                break;
            case 1932489556:
                if (model.equals(MODEL_NAME_AWOX_AROMA_LIGHT))
                {
                    obj = 23;
                    break;
                }
                break;
            case 2073680533:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_INNOTECH))
                {
                    obj = 79;
                    break;
                }
                break;
            case 2073680543:
                if (model.equals(MODEL_NAME_AWOX_SMART_LIGHT_COLOR_MESH_13W_GLOBE_SEELIGHTING))
                {
                    obj = 39;
                    break;
                }
                break;
        }

        switch (obj)
        {
            case 0:
            case 1:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                break;
            case 2:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_PRESET);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                properties.add(PROPERTY_FRIENDLY_NAME);
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_POWER_CONSUMPTION);
                properties.add(PROPERTY_POWER_CONSUMPTION_MESH);
                properties.add(PROPERTY_POWER_CONSUMPTION_HISTORY_MESH);
                properties.add(PROPERTY_POWER_CONSUMPTION_HOURLY);
                properties.add(PROPERTY_POWER_CONSUMPTION_DAILY);
                properties.add(PROPERTY_TIME);
                properties.add(PROPERTY_PLUG_SCHEDULE);
                properties.add(PROPERTY_PLUG_LED_STATE);
                properties.add(PROPERTY_REVOGI_UPDATE);
                properties.add(PROPERTY_REVOGI_READ);
                properties.add(PROPERTY_REVOGI_PRE_AND_POST_UPDATE);
                properties.add(PROPERTY_REVOGI_FACTORY_RESET);
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_TIMER_ON_FADE_DURATION);
                properties.add(PROPERTY_TIMER_OFF_FADE_DURATION);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                break;
            case 15:
            case 16:
            case 17:
            case 18:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_TIMER_ON_FADE_DURATION);
                properties.add(PROPERTY_TIMER_OFF_FADE_DURATION);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                break;
            case 19:
            case 20:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_TIMER_ON_FADE_DURATION);
                properties.add(PROPERTY_TIMER_OFF_FADE_DURATION);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                break;
            case 21:
            case 22:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_TIMER_ON_FADE_DURATION);
                properties.add(PROPERTY_TIMER_OFF_FADE_DURATION);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                properties.add(PROPERTY_BATTERY_LEVEL);
                break;
            case 23:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_TIMER_ON_FADE_DURATION);
                properties.add(PROPERTY_TIMER_OFF_FADE_DURATION);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                properties.add(PROPERTY_FAN_SPEED);
                properties.add(PROPERTY_FAN_TIMER_ON_DELAY);
                properties.add(PROPERTY_FAN_TIMER_OFF_DELAY);
                break;
            case 24:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                break;
            case 25:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_TIMER_ON_DELAY);
                properties.add(PROPERTY_TIMER_OFF_DELAY);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                break;
            case 26:
                /*
                light = (ILightDevice) DeviceScanner.getInstance().getGatewareDevicesByKey(this.uuid);
                if (light != null)
                {
                    properties.add(PROPERTY_POWER_STATE);
                    if (light.supportColorRGB())
                    {
                        properties.add(PROPERTY_COLOR);
                        properties.add(PROPERTY_COLOR_BRIGHTNESS);
                    }
                    else
                    {
                        properties.add(PROPERTY_WHITE_BRIGHTNESS);
                    }
                    if (light.supportTemperature())
                    {
                        properties.add(PROPERTY_WHITE_TEMPERATURE);
                        if (!properties.contains(PROPERTY_WHITE_BRIGHTNESS))
                        {
                            properties.add(PROPERTY_WHITE_BRIGHTNESS);
                        }
                    }
                    if (light.supportColorRGB() && light.supportTemperature())
                    {
                        properties.add(PROPERTY_LIGHT_MODE);
                        break;
                    }
                }
                */
                break;

            case 27:
                /*
                light = (ILightDevice) DeviceScanner.getInstance().getGatewareDevicesByKey(this.uuid);
                if (light != null)
                {
                    properties.add(PROPERTY_POWER_STATE);
                    if (light.supportColorRGB())
                    {
                        properties.add(PROPERTY_COLOR);
                        properties.add(PROPERTY_COLOR_BRIGHTNESS);
                        if (light.supportColorAnimation())
                        {
                            properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                            properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                            properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                        }
                    }
                    else
                    {
                        properties.add(PROPERTY_WHITE_BRIGHTNESS);
                    }
                    if (light.supportTemperature())
                    {
                        properties.add(PROPERTY_WHITE_TEMPERATURE);
                        if (!properties.contains(PROPERTY_WHITE_BRIGHTNESS))
                        {
                            properties.add(PROPERTY_WHITE_BRIGHTNESS);
                        }
                    }
                    if (light.supportColorRGB() && light.supportTemperature())
                    {
                        properties.add(PROPERTY_LIGHT_MODE);
                        break;
                    }
                }
                */
                break;

            case 28:
                properties.add(PROPERTY_SWITCH_GROUP_1);
                properties.add(PROPERTY_SWITCH_GROUP_2);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_1);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_2);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_3);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_4);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_5);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_6);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_7);
                properties.add(PROPERTY_SWITCH_BINDING);
                properties.add(PROPERTY_SWITCH_COMMAND);
                properties.add(PROPERTY_MESH_GROUPS);
                break;

            case 29:
                properties.add(PROPERTY_SWITCH_GROUP_1);
                properties.add(PROPERTY_SWITCH_PRESET_LIST_1);
                properties.add(PROPERTY_SWITCH_COMMAND);
                properties.add(PROPERTY_MESH_GROUPS);
                break;
            case 30:
            case 31:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                break;
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_LIGHT_MODE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_COLOR_BRIGHTNESS);
                properties.add(PROPERTY_COLOR);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLORS);
                properties.add(PROPERTY_COLOR_SEQUENCE_PRESET);
                properties.add(PROPERTY_COLOR_SEQUENCE_COLOR_DURATION);
                properties.add(PROPERTY_COLOR_SEQUENCE_FADE_DURATION);
                properties.add(PROPERTY_TIME);
                properties.add(PROPERTY_ALARMS);
                properties.add(PROPERTY_TIMER);
                properties.add(PROPERTY_NIGHTLIGHT);
                properties.add(PROPERTY_DAWN_SIMULATOR);
                properties.add(PROPERTY_PRESENCE_SIMULATOR);
                properties.add(PROPERTY_PROGRAM);
                properties.add(PROPERTY_SCENES);
                properties.add(PROPERTY_MESH_NETWORK);
                properties.add(PROPERTY_MESH_GROUPS);
                properties.add(PROPERTY_MESH_ADDRESS);
                properties.add(PROPERTY_RESCUE_PEBBLE);
                properties.add(PROPERTY_MESH_OTA);
                break;
            case 82:
            case 83:
            case 84:
            case 85:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_WHITE_TEMPERATURE);
                properties.add(PROPERTY_TIME);
                properties.add(PROPERTY_ALARMS);
                properties.add(PROPERTY_TIMER);
                properties.add(PROPERTY_NIGHTLIGHT);
                properties.add(PROPERTY_DAWN_SIMULATOR);
                properties.add(PROPERTY_PRESENCE_SIMULATOR);
                properties.add(PROPERTY_PROGRAM);
                properties.add(PROPERTY_SCENES);
                properties.add(PROPERTY_MESH_NETWORK);
                properties.add(PROPERTY_MESH_GROUPS);
                properties.add(PROPERTY_MESH_ADDRESS);
                properties.add(PROPERTY_RESCUE_PEBBLE);
                properties.add(PROPERTY_MESH_OTA);
                break;
            case 86:
            case 87:
            case 88:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_WHITE_BRIGHTNESS);
                properties.add(PROPERTY_TIME);
                properties.add(PROPERTY_ALARMS);
                properties.add(PROPERTY_TIMER);
                properties.add(PROPERTY_NIGHTLIGHT);
                properties.add(PROPERTY_DAWN_SIMULATOR);
                properties.add(PROPERTY_PRESENCE_SIMULATOR);
                properties.add(PROPERTY_PROGRAM);
                properties.add(PROPERTY_SCENES);
                properties.add(PROPERTY_MESH_NETWORK);
                properties.add(PROPERTY_MESH_GROUPS);
                properties.add(PROPERTY_MESH_ADDRESS);
                properties.add(PROPERTY_RESCUE_PEBBLE);
                properties.add(PROPERTY_MESH_OTA);
                break;
            case 89:
            case 90:
                properties.add(PROPERTY_MESH_NETWORK);
                properties.add(PROPERTY_MESH_GROUPS);
                properties.add(PROPERTY_MESH_OTA);
                break;
            case 91:
                break;
            case 92:
            case 93:
            case 94:
            case 95:
                /*
                light = (ILightDevice) DeviceScanner.getInstance().getGatewareDevicesByKey(this.uuid);
                if (light != null)
                {
                    properties.add(PROPERTY_POWER_STATE);
                    if (light.supportColorRGB())
                    {
                        properties.add(PROPERTY_COLOR);
                        properties.add(PROPERTY_COLOR_BRIGHTNESS);
                    }
                    else
                    {
                        properties.add(PROPERTY_WHITE_BRIGHTNESS);
                    }
                    if (light.supportTemperature())
                    {
                        properties.add(PROPERTY_WHITE_TEMPERATURE);
                        if (!properties.contains(PROPERTY_WHITE_BRIGHTNESS))
                        {
                            properties.add(PROPERTY_WHITE_BRIGHTNESS);
                        }
                    }
                    if (light.supportColorRGB() && light.supportTemperature())
                    {
                        properties.add(PROPERTY_LIGHT_MODE);
                        break;
                    }
                }
                */
                break;
            case 96:
            case 97:
                properties.add(PROPERTY_POWER_STATE);
                properties.add(PROPERTY_MESH_NETWORK);
                properties.add(PROPERTY_MESH_GROUPS);
                properties.add(PROPERTY_MESH_ADDRESS);
                properties.add(PROPERTY_RESCUE_PEBBLE);
                properties.add(PROPERTY_MESH_OTA);
                properties.add(PROPERTY_POWER_CONSUMPTION_MESH);
                properties.add(PROPERTY_POWER_CONSUMPTION_HISTORY_MESH);
                properties.add(PROPERTY_POWER_CONSUMPTION_HOURLY);
                properties.add(PROPERTY_POWER_CONSUMPTION_DAILY);
                properties.add(PROPERTY_PLUG_MESH_SCHEDULE);
                properties.add(PROPERTY_PLUG_LED_STATE);
                break;
        }

        properties.add(PROPERTY_MESH_NETWORK);
        properties.add(PROPERTY_MESH_GROUPS);
        properties.add(PROPERTY_MESH_OTA);
        properties.add(PROPERTY_PIR_SETTINGS);
        properties.add(PROPERTY_PIR_CURRENT_LUMINOSITY);

        return properties;
    }
}
