package de.xavaro.android.brl.comm;

@SuppressWarnings("WeakerAccess")
public class BRLTypes
{
    //region Hex device numbers.

    public static final int DEV_A1 = 0x2714;

    public static final int DEV_MP1 = 0x4EB5;

    public static final int DEV_RM_2 = 0x2712;
    public static final int DEV_RM_MINI = 0x2737;
    public static final int DEV_RM_PRO_PHICOMM = 0x273d;
    public static final int DEV_RM_2_HOME_PLUS = 0x2783;
    public static final int DEV_RM_2_2HOME_PLUS_GDT = 0x277c;
    public static final int DEV_RM_2_PRO_PLUS = 0x272a;
    public static final int DEV_RM_2_PRO_PLUS_2 = 0x2787;
    public static final int DEV_RM_2_PRO_PLUS_2_BL = 0x278b;
    public static final int DEV_RM_MINI_SHATE = 0x278f;

    public static final int DEV_SP1 = 0x0;
    public static final int DEV_SP2 = 0x2711;
    public static final int DEV_SP2_HONEYWELL_ALT1 = 0x2719;
    public static final int DEV_SP2_HONEYWELL_ALT2 = 0x7919;
    public static final int DEV_SP2_HONEYWELL_ALT3 = 0x271a;
    public static final int DEV_SP2_HONEYWELL_ALT4 = 0x791a;
    public static final int DEV_SP3 = 0x753e;
    public static final int DEV_SP3_1 = 0x7547;
    public static final int DEV_SP3_S1 = 0x947a;
    public static final int DEV_SP3_S2 = 0x9479;

    public static final int DEV_SPMINI = 0x2720;
    public static final int DEV_SPMINI2 = 0x2728;
    public static final int DEV_SPMINI_OEM_ALT1 = 0x2733;
    public static final int DEV_SPMINI_OEM_ALT2 = 0x273e;
    public static final int DEV_SPMINI_PLUS = 0x2736;

    //endregion Hex device numbers.

    //region Friendly names.

    public static final String DESC_A1 = "A1 Air Quality";

    public static final String DESC_MP1 = "MP1 Power Strip";

    public static final String DESC_RM_2 = "RM 2";
    public static final String DESC_RM_MINI = "RM Mini";
    public static final String DESC_RM_PRO_PHICOMM = "RM Pro";
    public static final String DESC_RM_2_HOME_PLUS = "RM 2 Home Plus";
    public static final String DESC_RM_2_2HOME_PLUS_GDT = "RM 2 Home Plus GDT";
    public static final String DESC_RM_2_PRO_PLUS = "RM 2 Pro Plus";
    public static final String DESC_RM_2_PRO_PLUS_2 = "RM 2 Pro Plus 2";
    public static final String DESC_RM_2_PRO_PLUS_2_BL = "RM 2 Pro Plus 2 BL";
    public static final String DESC_RM_MINI_SHATE = "RM Mini SHATE";

    public static final String DESC_SP1 = "Smart Plug V1";
    public static final String DESC_SP2 = "Smart Plug V2";
    public static final String DESC_SP2_HONEYWELL_ALT1 = "Smart Plug Honeywell Alt 1";
    public static final String DESC_SP2_HONEYWELL_ALT2 = "Smart Plug Honeywell Alt 2";
    public static final String DESC_SP2_HONEYWELL_ALT3 = "Smart Plug Honeywell Alt 3";
    public static final String DESC_SP2_HONEYWELL_ALT4 = "Smart Plug Honeywell Alt 4";
    public static final String DESC_SPMINI = "Smart Plug Mini";
    public static final String DESC_SP3 = "Smart Plug V3";
    public static final String DESC_SP3_1 = "Smart Plug V3 1";
    public static final String DESC_SP3_S1 = "Smart Plug V3 S1";
    public static final String DESC_SP3_S2 = "Smart Plug V3 S2";

    public static final String DESC_SPMINI2 = "Smart Plug Mini V2";
    public static final String DESC_SPMINI_OEM_ALT1 = "Smart Plug OEM Alt 1";
    public static final String DESC_SPMINI_OEM_ALT2 = "Smart Plug OEM Alt 2";
    public static final String DESC_SPMINI_PLUS = "Smart Plug Mini Plus";

    public static final String DESC_UNKNOWN = "Unknown Device";

    //endregion Friendly names.

    public static String getFriendlyName(int devType)
    {
        switch (devType)
        {
            case DEV_A1: return DESC_A1;

            case DEV_MP1: return DESC_MP1;

            case DEV_RM_2: return DESC_RM_2;
            case DEV_RM_MINI: return DESC_RM_MINI;
            case DEV_RM_PRO_PHICOMM: return DESC_RM_PRO_PHICOMM;
            case DEV_RM_2_HOME_PLUS: return DESC_RM_2_HOME_PLUS;
            case DEV_RM_2_2HOME_PLUS_GDT: return DESC_RM_2_2HOME_PLUS_GDT;
            case DEV_RM_2_PRO_PLUS: return DESC_RM_2_PRO_PLUS;
            case DEV_RM_2_PRO_PLUS_2: return DESC_RM_2_PRO_PLUS_2;
            case DEV_RM_2_PRO_PLUS_2_BL: return DESC_RM_2_PRO_PLUS_2_BL;
            case DEV_RM_MINI_SHATE: return DESC_RM_MINI_SHATE;

            case DEV_SP1: return DESC_SP1;
            case DEV_SP2: return DESC_SP2;
            case DEV_SP2_HONEYWELL_ALT1: return DESC_SP2_HONEYWELL_ALT1;
            case DEV_SP2_HONEYWELL_ALT2: return DESC_SP2_HONEYWELL_ALT2;
            case DEV_SP2_HONEYWELL_ALT3: return DESC_SP2_HONEYWELL_ALT3;
            case DEV_SP2_HONEYWELL_ALT4: return DESC_SP2_HONEYWELL_ALT4;
            case DEV_SP3: return DESC_SP3;
            case DEV_SP3_1: return DESC_SP3_1;
            case DEV_SP3_S1: return DESC_SP3_S1;
            case DEV_SP3_S2: return DESC_SP3_S2;

            case DEV_SPMINI: return DESC_SPMINI;
            case DEV_SPMINI2: return DESC_SPMINI2;
            case DEV_SPMINI_OEM_ALT1: return DESC_SPMINI_OEM_ALT1;
            case DEV_SPMINI_OEM_ALT2: return DESC_SPMINI_OEM_ALT2;
            case DEV_SPMINI_PLUS: return DESC_SPMINI_PLUS;
        }

        return DESC_UNKNOWN;
    }
}
