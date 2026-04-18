package com.chappe.repository;

import java.util.Map;

import com.chappe.model.Zustand;

public class PositionSignalMap {

    public Map<Integer, Zustand> signaltoPosition = Map.of(
        0, new Zustand(0, 0, 0),
        1, new Zustand(1, 0, 0),
        2, new Zustand(0, 1, 0),
        3, new Zustand(0, 0, 1),
        4, new Zustand(1, 1, 0),
        5, new Zustand(1, 0, 1),
        6, new Zustand(0, 1, 1),
        7, new Zustand(1, 1, 1)
    );
    public PositionSignalMap(Map<Integer, Zustand> signaltoPosition) {
        this.signaltoPosition = signaltoPosition;
    }

public PositionSignalMap() {}
    public int getPosition(Zustand zustand) {
        if (zustand == null) {
            return -1;
        }
        return signaltoPosition.entrySet().stream()
            .filter(entry -> entry.getValue().getRegulator() == zustand.getRegulator() &&
                             entry.getValue().getIndicator1() == zustand.getIndicator1() &&
                             entry.getValue().getIndicator2() == zustand.getIndicator2())
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(-1);
    }
    public Zustand getZustand(int position) {
        return signaltoPosition.getOrDefault(position, new Zustand(0, 0, 0));
    }
}
