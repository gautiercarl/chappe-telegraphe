package com.chappe.service;

import java.net.DatagramSocket;

import com.chappe.model.Message;

public class UDPService {
    private DatagramSocket socket;

    public UDPService(int port) throws Exception {
        socket = new DatagramSocket(port);
    }

    public void send(Message msg, String host, int port) {
        // Message → byte[] → senden
        
    }

    public Message receive() {
        // byte[] → Message
        return null;
    }
}
