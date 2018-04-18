package de.xavaro.android.brl.comm;

@SuppressWarnings("WeakerAccess")
public class BRLTypes
{
    public static String getFriendlyName(int devType)
    {
        if (devType == 0) return "SP1";
        
        if (devType == 0x2711) return "SP2";
        if (devType == 0x2719) return "SP2 Honeywell";
        if (devType == 0x7919) return "SP2 Honeywell";
        if (devType == 0x271a) return "SP2 Honeywell";
        if (devType == 0x791a) return "SP2 Honeywell";
        if (devType == 0x2720) return "SPMini";

        if (devType == 0x753e) return "SP3";
        if (devType == 0x7D00) return "SP3 OEM Branded";

        if (devType == 0x947a) return "SP3S";
        if (devType == 0x9479) return "SP3S";

        if (devType == 0x2728) return "SPMini2";

        if (devType == 0x2733) return "SPMini OEM branded";
        if (devType == 0x273e) return "SPMini OEM branded";

        if ((devType >= 0x7530) && (devType <= 0x7918)) return "SPMini2 OEM branded";

        if (devType == 0x2736) return "SPMiniPlus";

        if (devType == 0x2712) return "RM2";
        if (devType == 0x2737) return "RM Mini";
        if (devType == 0x273d) return "RM Pro Phicomm";
        if (devType == 0x2783) return "RM2 Home Plus";
        if (devType == 0x277c) return "RM2 Home Plus GDT";
        if (devType == 0x272a) return "RM2 Pro Plus";
        if (devType == 0x2787) return "RM2 Pro Plus2";
        if (devType == 0x279d) return "RM2 Pro Plus3";
        if (devType == 0x27a9) return "RM2 Pro Plus_300";
        if (devType == 0x278b) return "RM2 Pro Plus BL";
        if (devType == 0x2797) return "RM2 Pro Plus HYC";
        if (devType == 0x27a1) return "RM2 Pro Plus R1";
        if (devType == 0x27a6) return "RM2 Pro PP";
        if (devType == 0x278f) return "RM Mini Shate";

        if (devType == 0x2714) return "A1";

        if (devType == 0x4eB5) return "MP1";
        if (devType == 0x4eF7) return "MP1";

        if (devType == 0x4ead) return "Hysen controller";

        if (devType == 0x2722) return "S1 (SmartOne Alarm Kit)";

        if (devType == 0x4e4d) return "Dooya DT360E (DOOYA_CURTAIN_V2)";

        return "Unknown Device";
    }
}
