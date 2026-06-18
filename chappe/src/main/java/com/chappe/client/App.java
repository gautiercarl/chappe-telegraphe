package com.chappe.client;

import java.util.List;

import com.chappe.logik.MainTowerNode;
import com.chappe.logik.TowerNode;
import com.chappe.model.Arm;
import com.chappe.model.Tower;
import com.chappe.network.FranceNetwork;
import com.chappe.repository.TowerRepository;
import com.chappe.service.TowerSender;
import com.chappe.utils.Functions;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

    Tower t1 = TowerRepository.getTowerById(1);
    Tower t2 = TowerRepository.getTowerById(2);
    Tower t3 = TowerRepository.getTowerById(3);
    t1.setNachfolger(t2);
    t2.setVorgaenger(t1);
    t2.setNachfolger(t3);
    t3.setVorgaenger(t2);
    FranceNetwork network = new FranceNetwork(List.of(t1, t2, t3));
    List<TowerNode> towerNodes = Functions.createTowerNodes(network.getTowers());
    MainTowerNode mainNode = (MainTowerNode) towerNodes.get(0);
   // MainTowerNode lastMainNode = (MainTowerNode) towerNodes.get(towerNodes.size() - 1);


    Functions.initializeTowerNodes(towerNodes);

    try {
        for (TowerNode node : towerNodes) {
    if (Functions.hasVorgaenger(node.getTower())) {
        Thread poolThread = new Thread(node::pollPrevious);
        poolThread.start();
        if(node.isEndOfTransmission()) {
            poolThread.interrupt();
            // if (node.getTower().getId() == lastMainNode.getTower().getId()) {
            //     Functions.mainTowerDecoder(lastMainNode, lastMainNode.getSignals());
            // }
        }
        //new Thread(node::pollPrevious).start();
    }
}
       new Thread(() -> {
            try {
                mainNode.sendMessage(List.of("Hallo", "Welt","Hallo"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
