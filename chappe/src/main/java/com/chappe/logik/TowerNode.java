package com.chappe.logik;

import com.chappe.model.Message;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import com.chappe.repository.TowerRepository;
import com.chappe.service.UDPService;
import com.chappe.utils.Functions;

public class TowerNode {
    private Tower tower;
    private UDPService udp;

    public TowerNode(Tower tower) throws Exception {
        this.tower = tower;
        this.udp = new UDPService(tower.getPort());
    }

    public void start() {
        new Thread(this::listen).start();
        new Thread(this::pollPrevious).start();
    }

    private void listen() {
        while (true) {
            Message msg = udp.receive();

            if (msg.type == 0) { // GET
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
            tower.getIndicator2().getPosition()
        );

        udp.send(response, "localhost", TowerRepository.getTowerById(msg.towerId).getPort());
    }

    private void pollPrevious() {
        while (true) {
            try {
                Message request = new Message();
                request.type = 0;

                udp.send(request, "localhost", tower.getVorgaenger().getPort());

                Message response = udp.receive();
                if (response.type == 1) {
                    updateState(response);
                }
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateState(Message msg) {
        // check if the successor has already the same actual state before updating the
        // state
        //check if the signal is any special signal(start, stop, etc.) and handle it accordingly
        if (Functions.hasNextTowerTheSameState(tower)) {
            tower.getRegulator().setPosition(msg.zustand.getRegulator());
            tower.getIndicator1().setPosition(msg.zustand.getIndicator1());
            tower.getIndicator2().setPosition(msg.zustand.getIndicator2());

            System.out.println("Tower " + tower.getId() + " updated!");

        }

    }
}
