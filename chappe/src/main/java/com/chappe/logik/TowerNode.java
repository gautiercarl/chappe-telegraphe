package com.chappe.logik;

import java.util.ArrayList;
import java.util.List;

import com.chappe.model.Message;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import com.chappe.repository.TowerRepository;
import com.chappe.service.UDPService;
import com.chappe.utils.Functions;

public class TowerNode {
    protected Tower tower;
    private UDPService frontUdp;
    private UDPService backUdp;
    private List<Zustand> signals = new ArrayList<>();
    private boolean endOfTransmission = false;

    public TowerNode(Tower tower) throws Exception {
        this.tower = tower;
        this.frontUdp = new UDPService(tower.getFrontPort());
        this.backUdp = new UDPService(tower.getBackPort());
    }

    public void start() {
        new Thread(this::listen).start();
        System.out.println("TowerNode " + tower.getId() + " started and listening on ports: front="
                + tower.getFrontPort() + ", back=" + tower.getBackPort());
        // new Thread(this::pollPrevious).start(); //only if all towers are initialized,
        // otherwise we can have a problem with the main tower which has no predecessor
        // and will never update its state
    }

    private void listen() {
        while (true) {
            Message msg = backUdp.receive();

            if (msg.type == 0) { // GET
                System.out.println("TowerNode " + tower.getId() + ": Received GET request from Tower " + msg.towerId);
                handleRequest(msg);
            }
        }
    }

    private void handleRequest(Message msg) {
        Message response = new Message();
        response.type = 1;
        response.towerId = tower.getId();
        response.zustand = new Zustand(
                tower.getRegulator().getPosition(),
                tower.getIndicator1().getPosition(),
                tower.getIndicator2().getPosition());

        backUdp.send(response, "localhost", TowerRepository.getTowerById(msg.towerId).getFrontPort());
        System.out.println("TowerNode " + tower.getId() + ": Sent RESPONSE to Tower " + msg.towerId + " from Tower "
                + tower.getId());
    }

    public void pollPrevious() {
        while (!endOfTransmission) {
            if (Functions.hasVorgaenger(tower)) {
                try {

                    Message request = new Message();
                    request.type = 0;
                    request.towerId = tower.getId();

                    frontUdp.send(request, "localhost", tower.getVorgaenger().getBackPort());
                    System.out.println("TowerNode " + tower.getId() + ": Sent GET request to Tower "
                            + tower.getVorgaenger().getId() + " from Tower " + tower.getId());

                    // wait for response and update state with loop
                    while (true) {
                        System.out.println("TowerNode " + tower.getId() + ": Warte auf RESPONSE von Tower "
                                + tower.getVorgaenger().getId() + "...");
                        Message response = frontUdp.receive();
                        if (response.type == 1) {
                            System.out.println("TowerNode " + tower.getId() + ": Received RESPONSE from Tower "
                                    + response.towerId);
                            updateState(response);
                            break; // exit the inner loop after processing the response
                        }
                    }
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void updateState(Message msg) throws Exception {
        //print received message
        System.out.println("TowerNode " + tower.getId() + ": Received message - Type: " + msg.type + ", TowerId: " + msg.towerId + ", Zustand: R=" + msg.zustand.getRegulator() + ", I1=" + msg.zustand.getIndicator1() + ", I2=" + msg.zustand.getIndicator2());
        


        // check if the successor has already the same actual state before updating the
        // state
        // check if the signal is any special signal(start, stop, etc.) and handle it
        // accordingly
        while (!Functions.hasNextTowerTheSameState(tower) && tower.getNachfolger() != null) {
            System.out.println(
                    "TowerNode " + tower.getId() + ": Warte auf Nachfolger, um den Zustand zu aktualisieren...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (isNewState(msg)) {
            tower.getRegulator().setPosition(msg.zustand.getRegulator());
            tower.getIndicator1().setPosition(msg.zustand.getIndicator1());
            tower.getIndicator2().setPosition(msg.zustand.getIndicator2());

            if (isCompositionSignal(msg)) {
            System.out.println("TowerNode " + tower.getId() + ": Composition Signal empfangen: R=45, I1=0, I2=0");
        } else if (isEndOfTransmissionSignal(msg)) {
            System.out.println(
                    "TowerNode " + tower.getId() + ": Ende der Übertragung Signal empfangen: R=90, I1=90, I2=-90");
            endOfTransmission = true;
        } else if (isSecurityPositionSignal(msg)) {
            System.out
                    .println("TowerNode " + tower.getId() + ": Sicherheitsposition Signal empfangen: R=0, I1=0, I2=0");
        } else {
            System.out.println(
                    "TowerNode " + tower.getId() + ": Normaler Zustand empfangen: R=" + msg.zustand.getRegulator()
                            + ", I1=" + msg.zustand.getIndicator1() + ", I2=" + msg.zustand.getIndicator2());
            signals.add(msg.zustand);
        }
        System.out.println("TowerNode " + tower.getId() + ": Tower " + tower.getId() + " updated!");
        }
        else {
            System.out.println("TowerNode " + tower.getId() + ": Empfangener Zustand ist derselbe wie der aktuelle Zustand. Keine Aktualisierung erforderlich.");
        }
        
    }

    public boolean isEndOfTransmission() {
        return endOfTransmission;
    }

    public boolean setEndOfTransmission(boolean endOfTransmission) {
        this.endOfTransmission = endOfTransmission;
        return endOfTransmission;
    }

    public boolean isCompositionSignal(Message m) {
        return m.zustand.getRegulator() == 45 && m.zustand.getIndicator1() == 0 && m.zustand.getIndicator2() == 0;
    }

    public boolean isEndOfTransmissionSignal(Message m) {
        return m.zustand.getRegulator() == 90 && m.zustand.getIndicator1() == 90 && m.zustand.getIndicator2() == -90;
    }

    public boolean isSecurityPositionSignal(Message m) {
        return m.zustand.getRegulator() == 0 && m.zustand.getIndicator1() == 0 && m.zustand.getIndicator2() == 0;
    }

    public boolean isNewState(Message m) {
        return tower.getRegulator().getPosition() != m.zustand.getRegulator() ||
                tower.getIndicator1().getPosition() != m.zustand.getIndicator1() ||
                tower.getIndicator2().getPosition() != m.zustand.getIndicator2();
    }

    public Tower getTower() {
        return tower;
    }

    public List<Zustand> getSignals() {
        return signals;
    }
}
