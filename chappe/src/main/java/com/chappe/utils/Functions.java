package com.chappe.utils;

import java.util.List;

import com.chappe.logik.MainTowerNode;
import com.chappe.logik.TowerNode;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;

public class Functions {

    public static boolean hasNextTowerTheSameState(Tower t) {// to implement
        Tower next = t.getNachfolger();
        if (next == null)
            return false;
        // send request to next tower
        // udp.send(request, "localhost", next.getPort());

        return next.getRegulator().getPosition() == t.getRegulator().getPosition() &&
                next.getIndicator1().getPosition() == t.getIndicator1().getPosition() &&
                next.getIndicator2().getPosition() == t.getIndicator2().getPosition();
    }

    public static boolean hasVorgaenger(Tower t) {
        return t.getVorgaenger() != null;
    }

    public static boolean hasNachfolger(Tower t) {
        return t.getNachfolger() != null;
    }
public static List<TowerNode> createTowerNodes(List<Tower> towers) {
        return towers.stream().map(t -> {
            try {
                if ((t.getVorgaenger() == null||t.getNachfolger()==null)&&t.isMainTower()) {
                    return new MainTowerNode(t);
                } else {
                    return new TowerNode(t);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).toList();
    }
    public static boolean initializeTowerNodes(List<TowerNode> towerNodes) {// to implement
        try {
           towerNodes.forEach(TowerNode::start);
           System.out.println("Alle TowerNodes gestartet");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void mainTowerDecoder(MainTowerNode t, List<Zustand> signals) throws Exception {
       Tower tower = t.getTower();
         {
         
    
             System.out.println("MainTowerNode " + tower.getId() + ": End of Transmission Signal empfangen. Dekodierung gestartet.");
            System.out.println("MainTowerNode " + tower.getId() + ": Dekodierung der empfangenen Signale gestartet.");
            if (!signals.isEmpty()) {
            Zustand lastSignal = signals.get(signals.size() - 1);
            System.out.println("MainTowerNode " + tower.getId() + ": Letztes empfangenes Signal - Regulator: " + lastSignal.getRegulator() + ", Indicator1: " + lastSignal.getIndicator1() + ", Indicator2: " + lastSignal.getIndicator2());
            String decodedMessage = t.decodedString(signals);
            System.out.println("MainTowerNode " + tower.getId() + ": Dekodierte Nachricht: " + decodedMessage);
            }
        }
    }

}
