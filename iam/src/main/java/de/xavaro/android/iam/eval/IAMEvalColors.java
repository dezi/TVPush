package de.xavaro.android.iam.eval;

import java.util.HashMap;
import java.util.Map;

public class IAMEvalColors
{
    private final static String LOGTAG = IAMEvalColors.class.getSimpleName();

    private static final Map<String, Integer> colorsLower = new HashMap<>();

    public static boolean isColor(String name)
    {
        name = name.replace(" ", "");

        Integer color = colorsLower.get(name.toLowerCase());

        return (color != null);
    }

    public static int getColor(String name)
    {
        name = name.replace(" ", "");

        Integer color = colorsLower.get(name.toLowerCase());

        return (color == null) ? 0 : color;
    }

    @SuppressWarnings("unused")
    private static void color(String ral, String name, int val)
    {
        colorsLower.put(name.toLowerCase(), val);
    }

    static
    {
        // @formatter:off

        color("DIMM 1", "Hell",               0xffffff);
        color("DIMM 3", "Intim",              0x222222);
        color("DIMM 4", "Dunkel",             0x000000);

        color("BASIC 1", "Normal",            0xfffffe);
        color("BASIC 2", "Weiß",              0xfffffe);
        color("BASIC 3", "Rot",               0xff0000);
        color("BASIC 4", "Grün",              0x00ff00);
        color("BASIC 5", "Blau",              0x0000ff);
        color("BASIC 6", "Cyan",              0x00ffff);
        color("BASIC 7", "Magenta",           0xff00ff);
        color("BASIC 8", "Gelb",              0xffff00);

        color("COMMON 1", "Lila",             0x7e63a1);
        color("COMMON 2", "Orange",           0xff2300);
        color("COMMON 3", "Braun",            0x45302b);
        color("COMMON 4", "Violett",          0x60007f);

        color("SPECIAL 1", "Bordellrot",      0xff0000);
        color("SPECIAL 2", "Bordellroth",     0xff0000);
        color("SPECIAL 3", "Kackbraun",       0x815333);
        color("SPECIAL 4", "Pissgelb",        0xeaf044);

        color("RAL 1000", "Grünbeige",        0xccc58f);
        color("RAL 1001", "Beige",            0xd1bc8a);
        color("RAL 1002", "Sandgelb",         0xd2b773);
        color("RAL 1003", "Signalgelb",       0xf7ba0b);
        color("RAL 1004", "Goldgelb",         0xe2b007);
        color("RAL 1005", "Honiggelb",        0xc89f04);
        color("RAL 1006", "Maisgelb",         0xe1a100);
        color("RAL 1007", "Narzissengelb",    0xe79c00);
        color("RAL 1011", "Braunbeige",       0xaf8a54);
        color("RAL 1012", "Zitronengelb",     0xd9c022);
        color("RAL 1013", "Perlweiss",        0xe9e5ce);
        color("RAL 1014", "Elfenbein",        0xdfcea1);
        color("RAL 1015", "Hellelfenbein",    0xeadebd);
        color("RAL 1016", "Schwefelgelb",     0xeaf044);
        color("RAL 1017", "Safrangelb",       0xf4b752);
        color("RAL 1018", "Zinkgelb",         0xf3e03b);
        color("RAL 1019", "Graubeige",        0xa4957d);
        color("RAL 1020", "Olivgelb",         0x9a9464);
        color("RAL 1021", "Rapsgelb",         0xeec900);
        color("RAL 1023", "Verkehrsgelb",     0xffe92a);
        color("RAL 1024", "Ockergelb",        0xb89c50);
        color("RAL 1026", "Leuchtgelb",       0xf5ff00);
        color("RAL 1027", "Currygelb",        0xa38c15);
        color("RAL 1028", "Melonengelb",      0xffab00);
        color("RAL 1032", "Ginstergelb",      0xddb20f);
        color("RAL 1033", "Dahliengelb",      0xfaab21);
        color("RAL 1034", "Pastellgelb",      0xedab56);
        color("RAL 1035", "Perlbeige",        0xa29985);
        color("RAL 1036", "Perlgold",         0x927549);
        color("RAL 1037", "Sonnengelb",       0xeea205);
        color("RAL 2000", "Gelborange",       0xdd7907);
        color("RAL 2001", "Rotorange",        0xbe4e20);
        color("RAL 2002", "Blutorange",       0xc63927);
        color("RAL 2003", "Pastellorange",    0xfa842b);
        color("RAL 2004", "Reinorange",       0xe75b12);
        color("RAL 2005", "Leuchtorange",     0xff2300);
        color("RAL 2007", "Leuchthellorange", 0xffa421);
        color("RAL 2008", "Hellrotorange",    0xf3752c);
        color("RAL 2009", "Verkehrsorange",   0xe15501);
        color("RAL 2010", "Signalorange",     0xd55d23);
        color("RAL 2011", "Tieforange",       0xec7c25);
        color("RAL 2012", "Lachsorange",      0xdb6a50);
        color("RAL 2013", "Perlorange",       0x954527);
        color("RAL 3000", "Feuerrot",         0xab2524);
        color("RAL 3001", "Signalrot",        0xa02128);
        color("RAL 3002", "Karminrot",        0xa1232b);
        color("RAL 3003", "Rubinrot",         0x861a22);
        color("RAL 3004", "Purpurrot",        0x701f29);
        color("RAL 3005", "Weinrot",          0x5e2028);
        color("RAL 3007", "Schwarzrot",       0x402225);
        color("RAL 3009", "Oxidrot",          0x6d312b);
        color("RAL 3011", "Braunrot",         0x791f24);
        color("RAL 3012", "Beigerot",         0xc68873);
        color("RAL 3013", "Tomatenrot",       0x992a28);
        color("RAL 3014", "Altrosa",          0xcb7375);
        color("RAL 3015", "Hellrosa",         0xe3a0ac);
        color("RAL 3016", "Korallenrot",      0xab392d);
        color("RAL 3017", "Rosa",             0xcc515e);
        color("RAL 3018", "Erdbeerrot",       0xca3f51);
        color("RAL 3020", "Verkehrsrot",      0xbf111b);
        color("RAL 3022", "Lachsrot",         0xd36b56);
        color("RAL 3024", "Leuchtrot",        0xfc0a1c);
        color("RAL 3026", "Leuchthellrot",    0xff4c0f);
        color("RAL 3027", "Himbeerrot",       0xb01d42);
        color("RAL 3031", "Orientrot",        0xa7323e);
        color("RAL 3032", "Perlrubinrot",     0x8a3342);
        color("RAL 3033", "Perlrosa",         0xc74c51);
        color("RAL 4001", "Rotlila",          0x865d86);
        color("RAL 4002", "Rotviolett",       0x8f3f51);
        color("RAL 4003", "Erikaviolett",     0xca5b91);
        color("RAL 4004", "Bordeauxviolett",  0x69193b);
        color("RAL 4005", "Blaulila",         0x7e63a1);
        color("RAL 4006", "Verkehrspurpur",   0x912d76);
        color("RAL 4007", "Purpurviolett",    0x48233e);
        color("RAL 4008", "Signalviolett",    0x853d7d);
        color("RAL 4009", "Pastellviolett",   0x9d8493);
        color("RAL 4010", "Telemagenta",      0xc03f7d);
        color("RAL 4011", "Perlviolett",      0x8B76AB);
        color("RAL 4012", "Perlbrombeer",     0x807C9F);
        color("RAL 5000", "Violettblau",      0x2f4a71);
        color("RAL 5001", "Grünblau",         0x0e4666);
        color("RAL 5002", "Ultramarinblau",   0x162e7b);
        color("RAL 5003", "Saphirblau",       0x193058);
        color("RAL 5004", "Schwarzblau",      0x1a1d2a);
        color("RAL 5005", "Signalblau",       0x004389);
        color("RAL 5007", "Brillantblau",     0x38618c);
        color("RAL 5008", "Graublau",         0x2d3944);
        color("RAL 5009", "Azurblau",         0x245878);
        color("RAL 5010", "Enzianblau",       0x00427f);
        color("RAL 5011", "Stahlblau",        0x1a2740);
        color("RAL 5012", "Lichtblau",        0x2781bb);
        color("RAL 5013", "Kobaltblau",       0x202e53);
        color("RAL 5014", "Taubenblau",       0x667b9a);
        color("RAL 5015", "Himmelblau",       0x0071b5);
        color("RAL 5017", "Verkehrsblau",     0x004c91);
        color("RAL 5018", "Türkisblau",       0x138992);
        color("RAL 5019", "Capriblau",        0x005688);
        color("RAL 5020", "Ozeanblau",        0x2a5059);
        color("RAL 5021", "Wasserblau",       0x00747d);
        color("RAL 5022", "Nachtblau",        0x28275a);
        color("RAL 5023", "Fernblau",         0x486591);
        color("RAL 5024", "Pastellblau",      0x6391b0);
        color("RAL 5025", "Perlenzian",       0x346d8c);
        color("RAL 5026", "Perlnachtblau",    0x243f70);
        color("RAL 6000", "Patinagrün",       0x327663);
        color("RAL 6001", "Smaragdgrün",      0x266d3b);
        color("RAL 6002", "Laubgrün",         0x276230);
        color("RAL 6003", "Olivgrün",         0x4e553d);
        color("RAL 6004", "Blaugrün",         0x004547);
        color("RAL 6005", "Moosgrün",         0x0e4438);
        color("RAL 6006", "Grauoliv",         0x3b3d33);
        color("RAL 6007", "Flaschengrün",     0x2b3626);
        color("RAL 6008", "Braungrün",        0x302f22);
        color("RAL 6009", "Tannengrün",       0x213529);
        color("RAL 6010", "Grasgrün",         0x426e38);
        color("RAL 6011", "Resedagrün",       0x68825f);
        color("RAL 6012", "Schwarzgrün",      0x293a37);
        color("RAL 6013", "Schilfgrün",       0x76785b);
        color("RAL 6014", "Gelboliv",         0x443f31);
        color("RAL 6015", "Schwarzoliv",      0x383b34);
        color("RAL 6016", "Türkisgrün",       0x00664f);
        color("RAL 6017", "Maigrün",          0x4d8542);
        color("RAL 6018", "Gelbgrün",         0x4b9b3e);
        color("RAL 6019", "Weißgrün",         0xb2d8b4);
        color("RAL 6020", "Chromoxidgrün",    0x394937);
        color("RAL 6021", "Blassgrün",        0x87a180);
        color("RAL 6022", "Braunoliv",        0x3c372a);
        color("RAL 6024", "Verkehrsgrün",     0x008455);
        color("RAL 6025", "Farngrün",         0x56723d);
        color("RAL 6026", "Opalgrün",         0x005c54);
        color("RAL 6027", "Lichtgrün",        0x7dccbd);
        color("RAL 6028", "Kieferngrün",      0x2e554b);
        color("RAL 6029", "Minzgrün",         0x006f43);
        color("RAL 6032", "Signalgrün",       0x00855a);
        color("RAL 6034", "Pastelltürkis",    0x75adb1);
        color("RAL 6035", "Perlgrün",         0x226442);
        color("RAL 6036", "Perlopalgrün",     0x14706C);
        color("RAL 7000", "Fehgrau",          0x798790);
        color("RAL 7001", "Silbergrau",       0x8c969f);
        color("RAL 7002", "Olivgrau",         0x827d67);
        color("RAL 7003", "Moosgrau",         0x79796c);
        color("RAL 7004", "Signalgrau",       0x999a9f);
        color("RAL 7005", "Mausgrau",         0x6d7270);
        color("RAL 7006", "Beigegrau",        0x766a5d);
        color("RAL 7008", "Khakigrau",        0x756444);
        color("RAL 7009", "Grüngrau",         0x585e55);
        color("RAL 7010", "Zeltgrau",         0x565957);
        color("RAL 7011", "Eisengrau",        0x525a60);
        color("RAL 7012", "Basaltgrau",       0x575e62);
        color("RAL 7013", "Braungrau",        0x585346);
        color("RAL 7015", "Schiefergrau",     0x4c5057);
        color("RAL 7016", "Anthrazitgrau",    0x363d43);
        color("RAL 7021", "Schwarzgrau",      0x2e3236);
        color("RAL 7022", "Umbragrau",        0x464644);
        color("RAL 7023", "Betongrau",        0x7f8279);
        color("RAL 7024", "Graphitgrau",      0x484b52);
        color("RAL 7026", "Granitgrau",       0x354044);
        color("RAL 7030", "Steingrau",        0x919089);
        color("RAL 7031", "Blaugrau",         0x5b686f);
        color("RAL 7032", "Kieselgrau",       0xb5b5a7);
        color("RAL 7033", "Zementgrau",       0x7a8376);
        color("RAL 7034", "Gelbgrau",         0x928d75);
        color("RAL 7035", "Lichtgrau",        0xc4caca);
        color("RAL 7036", "Platingrau",       0x949294);
        color("RAL 7037", "Staubgrau",        0x7e8082);
        color("RAL 7038", "Achatgrau",        0xb0b3af);
        color("RAL 7039", "Quarzgrau",        0x6d6b64);
        color("RAL 7040", "Fenstergrau",      0x9aa0a7);
        color("RAL 7043", "Verkehrsgrau",     0x505455);
        color("RAL 7044", "Seidengrau",       0xbab9b0);
        color("RAL 7045", "Telegrau",         0x92989b);
        color("RAL 7048", "Perlmausgrau",     0x969590);
        color("RAL 8000", "Grünbraun",        0x8b7045);
        color("RAL 8001", "Ockerbraun",       0x9c6935);
        color("RAL 8002", "Signalbraun",      0x774c3b);
        color("RAL 8003", "Lehmbraun",        0x815333);
        color("RAL 8004", "Kupferbraun",      0x904e3b);
        color("RAL 8007", "Rehbraun",         0x6b442a);
        color("RAL 8008", "Olivbraun",        0x735230);
        color("RAL 8011", "Nussbraun",        0x5b3927);
        color("RAL 8012", "Rotbraun",         0x64312a);
        color("RAL 8014", "Sepiabraun",       0x49372a);
        color("RAL 8015", "Kastanienbraun",   0x5a2e2a);
        color("RAL 8016", "Mahagonibraun",    0x4f3128);
        color("RAL 8017", "Schokoladenbraun", 0x45302b);
        color("RAL 8019", "Graubraun",        0x3b3332);
        color("RAL 8022", "Schwarzbraun",     0x1e1a1a);
        color("RAL 8023", "Orangebraun",      0xa45c32);
        color("RAL 8024", "Beigebraun",       0x7b5741);
        color("RAL 8025", "Blassbraun",       0x765d4d);
        color("RAL 8028", "Terrabraun",       0x4f3b2b);
        color("RAL 8029", "Perlkupfer",       0x8C4C41);
        color("RAL 9001", "Cremeweiß",        0xeee9da);
        color("RAL 9002", "Grauweiß",         0xdadbd5);
        color("RAL 9003", "Signalweiß",       0xf8f9fb);
        color("RAL 9004", "Signalschwarz",    0x252427);
        color("RAL 9005", "Tiefschwarz",      0x151619);
        color("RAL 9006", "Weißaluminium",    0xA3A8AC);
        color("RAL 9007", "Graualuminium",    0x8F9190);
        color("RAL 9010", "Reinweiß",         0xf4f4ed);
        color("RAL 9011", "Graphit- schwarz", 0x1f2126);
        color("RAL 9016", "Verkehrsweiß",     0xf3f6f6);
        color("RAL 9017", "Verkehrsschwarz",  0x1b191d);
        color("RAL 9018", "Papyrusweiß",      0xcbd2d0);
        color("RAL 9022", "Perlhellgrau",     0xA3A8AC);
        color("RAL 9023", "Perldunkelgrau",   0x8E959F);
        color("RAL 4000", "Violett",          0x60007f);
        color("RAL 6031", "Bronzegrün",       0x485746);
        color("RAL 7027", "Grau",             0x7b765e);
        color("RAL 7028", "Dunkelgelb",       0x645e46);
        color("RAL 8020", "Gelbbraun",        0xcfaf7f);
        color("RAL 8027", "Lederbraun",       0x504938);
        color("RAL 9021", "Teerschwarz",      0x01050e);

        // @formatter:on
    }
}
