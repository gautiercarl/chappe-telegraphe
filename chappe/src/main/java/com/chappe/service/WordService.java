package com.chappe.service;

import java.util.Map;

import com.chappe.utils.CodeKey;

public class WordService {

    Map<CodeKey, String> wordBook= Map.of(
        new CodeKey(1, 1), "Hallo",
        new CodeKey(1, 2), "Welt",
        new CodeKey(2, 1), "Chappe",
        new CodeKey(2, 2), "Tower"
    );

    public String getWord(int seite, int position) {
        return wordBook.getOrDefault(new CodeKey(seite, position), "Unbekannt");
    }
     public CodeKey getCodeKey(String word) {
        return wordBook.entrySet().stream()
            .filter(entry -> entry.getValue().equals(word))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
     }
}
