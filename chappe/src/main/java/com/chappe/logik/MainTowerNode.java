package com.chappe.logik;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.chappe.repository.PositionSignalMap;
import com.chappe.utils.CodeKey;
import com.chappe.utils.Functions;
import com.chappe.model.Message;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import com.chappe.service.WordService;

public class MainTowerNode extends TowerNode {
    PositionSignalMap signaltoPosition = new PositionSignalMap();
    WordService ws = new WordService();

    public MainTowerNode(Tower tower) throws Exception {
        super(tower);
    }

    public void sendMessage(List<String> words) throws Exception {
        Tower t = this.tower;
        Queue<CodeKey> codeKeysList = getCodeKeys(words);
        //print codeKeysList
        System.out.println("CodeKeys to send:");
        codeKeysList.forEach(key -> System.out.println("Seite: " + key.seite + ", Position: " + key.position));
        while (!codeKeysList.isEmpty()) {
            CodeKey key = codeKeysList.poll();

            // Warten bis Nachfolger synchronisiert ist
            synchronizeWithNext();
            System.out.println("TowerNode " + tower.getId() + ": Bereit zum Senden des nächsten Wortes ");

            // Kompositionssignal senden
            sendCompositionSignal(t);
            // Warten bis Nachfolger synchronisiert ist
            synchronizeWithNext();

            // Seite senden
            t.setRegulator(signaltoPosition.getZustand(key.seite).getRegulator());
            t.setIndicator1(signaltoPosition.getZustand(key.seite).getIndicator1());
            t.setIndicator2(signaltoPosition.getZustand(key.seite).getIndicator2());

            System.out.println("TowerNode " + tower.getId() + ": Wort gesendet mit Seite: " + key.seite);

            // Wieder warten
            synchronizeWithNext();

            // Noch einmal Kompositionssignal senden
            sendCompositionSignal(t);
            // Wieder warten
            synchronizeWithNext();
            // Position senden
            t.setRegulator(signaltoPosition.getZustand(key.position).getRegulator());
            t.setIndicator1(signaltoPosition.getZustand(key.position).getIndicator1());
            t.setIndicator2(signaltoPosition.getZustand(key.position).getIndicator2());

            System.out.println("TowerNode " + tower.getId() + ": Wort gesendet mit Position: " + key.position);
        }
        if (codeKeysList.isEmpty()) {
            System.out.println("TowerNode " + tower.getId() + ": Alle Wörter gesendet!");
            synchronizeWithNext();
            sendEndOfTransmissionSignal(t);
        }

    }

    public Queue<CodeKey> getCodeKeys(List<String> words) {

        Queue<CodeKey> keys = new LinkedList<>();

        for (String word : words) {
            CodeKey key = ws.getCodeKey(word);

            if (key != null) {
                keys.add(key);
            } else {
                System.out.println("Wort '" + word + "' nicht gefunden!");
            }
        }

        return keys;
    }

    public String decodedString(List<Zustand> signals) {
       
            for (Zustand signal : signals) {
            System.out.println("Empfangenes Signal - Regulator: " + signal.getRegulator() + ", Indicator1: " + signal.getIndicator1() + ", Indicator2: " + signal.getIndicator2());
         } 
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < signals.size(); i=i+2) {
            if (i + 1 < signals.size()) {
                Zustand signal = signals.get(i);
                Zustand nextSignal = signals.get(i + 1);
                int signalPosition1 = signaltoPosition.getPosition(signal);
                int signalPosition2 = signaltoPosition.getPosition(nextSignal);
                String word = ws.getWord(signalPosition1, signalPosition2);
                if (word != null) {
                    decoded.append(word).append(" ");
                }
            }
        }
        return decoded.toString().trim();
    }

    public void sendCompositionSignal(Tower t) {
        t.setRegulator(45);
        t.setIndicator1(0);
        t.setIndicator2(0);
        System.out.println("TowerNode " + tower.getId() + ": Composition Signal gesendet: R=45, I1=0, I2=0");
    }

    public void sendEndOfTransmissionSignal(Tower t) {
        t.setRegulator(90);
        t.setIndicator1(90);
        t.setIndicator2(-90);
        System.out
                .println("TowerNode " + tower.getId() + ": Ende der Übertragung Signal gesendet: R=90, I1=90, I2=-90");
    }

    public void synchronizeWithNext() {
        while (!Functions.hasNextTowerTheSameState(tower)) {
            System.out.println(
                    "TowerNode " + tower.getId() + ": Warte auf Nachfolger, um den Zustand zu aktualisieren...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateState(Message msg) throws Exception {

       super.updateState(msg);
        if (tower.getNachfolger() == null && super.isEndOfTransmission()) {
            Functions.mainTowerDecoder(this, super.getSignals());
        }
    }

}
