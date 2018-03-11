package de.xavaro.android.ai;

import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;

public class AIEvalQuestion
{
    public static JSONObject evaluate(String message)
    {
        JSONObject result = new JSONObject();


        return result;
    }

    public static void buildTable()
    {
        JSONObject table = new JSONObject();

        Json.put(table, "Pronomen.personal.agens", "wer, welche, welcher");
        Json.put(table, "Pronomen.personal.patiens", "wem, wen, welchen, welchem");
        Json.put(table, "Pronomen.personal.possessiv", "wessen");
        Json.put(table, "Pronomen.personal.apersonal", "was, welches");

        Json.put(table, "Adverb.kausal", "warum, weshalb, weswegen, wieso");
        Json.put(table, "Adverb.modal", "wie, wieweit, wie viel");
        Json.put(table, "Adverb.instrumental", "wofür, wozu, womit, wodurch, worum, worüber, wobei, wovon, woraus");
        Json.put(table, "Adverb.lokal", "wo");
        Json.put(table, "Adverb.direktional", "wohin, woher");
        Json.put(table, "Adverb.positional", "woran, worin, worauf, worunter, wovor, wohinter, woneben");
        Json.put(table, "Adverb.temporal", "wann");
        Json.put(table, "Adverb.konditional", "wann");
    }
}
