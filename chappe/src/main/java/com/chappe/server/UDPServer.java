package com.chappe.server;

import com.chappe.model.Arm;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import com.chappe.repository.PositionSignalMap;
import com.chappe.service.TowerSender;
import com.chappe.service.WordService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UDPServer {
    private Map<Tower, Zustand> towerZustaende = new HashMap<>();
    private Map<Tower, Long> letzteUpdate = new HashMap<>();
    private int timeout = 500; // ms
    private PositionSignalMap positionSignalMap = new PositionSignalMap();
    WordService wordDecoder = new WordService();
    private ArrayList<Integer> previousSignal = new ArrayList<>();//temporäre
    private int counter = 0;//temporäre
    public void start() throws Exception {
        System.out.println("UDP Server gestartet, warte auf Daten...");
        DatagramSocket socket = new DatagramSocket(5000);
        byte[] buffer = new byte[1024];

        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("Packet empfangen von " + packet.getAddress() + ":" + packet.getPort());
            // 1. Status aus Packet extrahieren (z.B. JSON)
            Zustand z = Zustand.fromBytes(packet.getData());
            Tower t = getTowerFromPacket(packet);
            System.out.println("tower: " + t.getId() + ", Regulator: " + z.getRegulator() + ", Indicator1: "
                    + z.getIndicator1() + ", Indicator2: " + z.getIndicator2());
            // 2. Status speichern
            towerZustaende.put(t, z);
            letzteUpdate.put(t, System.currentTimeMillis());

            // 3. Prüfen ob Signal komplett ist
            if (istSignalVollstaendig(z)) {
                int signal = berechneSignal(z);
                System.out.println("Signal komplett: " + signal);
                 ArrayList<Integer> signals = t.getSignals();
                 if (!previousSignal.isEmpty()&& counter>2) {
                   
                         signals.add(previousSignal.get(0)); // temp
                     
                 }
                 previousSignal.add(signal); // temp
               if (counter>2)  signals.add(signal);
               counter++;
                 System.out.println(counter-1 + " Aktuelle gesammelte Signale für Tower " + t.getId() + ": " + signals);
                
                // 4. Weiterleiten an Nachfolger
                Tower next = t.getNachfolger();
                if (next != null) {
                    next.setRegulator(z.getRegulator());
                    next.setIndicator1(z.getIndicator1());
                    next.setIndicator2(z.getIndicator2());
                    next.setSignals(signals); // Weiterleiten der gesammelten Signale
                    // Optional: Sende UDP zum nächsten Tower
                    sendUDP(next);
                    signals.clear(); // Clear signals after forwarding

                } else {
                    System.out.println("Kein Nachfolger für Tower " + t.getId());
                    String messageString= "";
                    // for two consecutive signals in signals list, create a message string with
                    // stringbuilder and send it to word sender
                    for (int i = 0; i < signals.size() - 1; i++) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(wordDecoder.getWord(signals.get(i), signals.get(i + 1))).append("-");
                        messageString = sb.toString();
                    }
                    if (!messageString.isEmpty()) {
                        System.out.println("Nachricht am Ende der Kette: " + messageString);
                    } else {
                        System.out.println("Keine vollständige Nachricht am Ende der Kette.");
                        System.out.println("Empfangene Signale: " + signals);
                    }
                

                }
            }

            // reset socket

            // 5. Optional: Zeit-Check für fehlende Updates
            // towerZustaende.forEach((tower, zustand) -> {
            // if (System.currentTimeMillis() - letzteUpdate.get(tower) > timeout) {
            // System.out.println("Tower " + tower + " hat lange kein Update gesendet!");
            // }
            // });
        }
    }

    private boolean istSignalVollstaendig(Zustand z) {
        // Beispiel: alle Arme gesetzt, keine -1
        return z.getRegulator() != -1 && z.getIndicator1() != -1 && z.getIndicator2() != -1;
    }

    private void sendUDP(Tower t) {
        // Sende den neuen Zustand an den Tower via UDP
        System.out.println("Sende UDP an Tower " + t.getId());
        try {
            TowerSender.sendState(t);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private int berechneSignal(Zustand z) {
        // Mapping aus Arme → Signal
        return positionSignalMap.getPosition(z);
    }

    private Tower getTowerFromPacket(DatagramPacket packet) {
        Tower t1 = new Tower(packet.getData()[0], new Arm(), new Arm(), new Arm());
        Tower t2 = new Tower(2, new Arm(), new Arm(), new Arm());
        //t2.setSignals(new ArrayList<>(1));
        t1.setNachfolger(t2);
        if (packet.getData()[0] == 1) {
            return t1;
        } else {
            return t2;
        }
    }
}