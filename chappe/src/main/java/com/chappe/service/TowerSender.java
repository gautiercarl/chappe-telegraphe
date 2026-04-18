package com.chappe.service;

import com.chappe.model.Arm;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import com.chappe.repository.PositionSignalMap;
import com.chappe.utils.CodeKey;

import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.text.Position;

public class TowerSender {

    PositionSignalMap signaltoPosition = new PositionSignalMap();

    public static void sendState(Tower t) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        byte[] data = new byte[4];

        data[0] = (byte) t.getId(); // Tower ID
        data[1] = (byte) t.getRegulator().getPosition(); // Regulator
        data[2] = (byte) t.getIndicator1().getPosition(); // Indicator1
        data[3] = (byte) t.getIndicator2().getPosition(); // Indicator2

        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName("localhost"),
                5000);

        socket.send(packet);
        socket.close();

        System.out.println(" Tower " + t.getId() + " aktualisiert");
    }

    public void sendMessage(Tower t, List<String> words) throws Exception {
        Queue<CodeKey> codeKeysList = getCodeKeys(words);
        codeKeysList.forEach(key -> {
            t.setRegulator((signaltoPosition.getZustand(key.seite).getRegulator()));
            t.setIndicator1((signaltoPosition.getZustand(key.seite).getIndicator1()));
            t.setIndicator2((signaltoPosition.getZustand(key.seite).getIndicator2()));
            // wait till the state is sent and received by the server before setting the
            // next one
            //Functions.hasNextTowerTheSameState(tower) checken bevor den nächsten Zustand setzen, damit die Türme nicht zu schnell hintereinander aktualisiert werden
            try {
                sendState(t);
                Thread.sleep(500); // Wartezeit anpassen, je nach Bedarf
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Wort gesendet mit Seite: " + key.seite);
            t.setRegulator((signaltoPosition.getZustand(key.position).getRegulator()));
            t.setIndicator1((signaltoPosition.getZustand(key.position).getIndicator1()));
            t.setIndicator2((signaltoPosition.getZustand(key.position).getIndicator2()));

            // wait till the state is sent and received by the server before setting the
            // next one
            try {
                sendState(t);
                Thread.sleep(500); // Wartezeit anpassen, je nach Bedarf
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Wort gesendet mit Position: " + key.position);
                        codeKeysList.poll(); // nötig?


        });

    }

   public Queue<CodeKey> getCodeKeys(List<String> words) {
    WordService ws = new WordService();
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

}
