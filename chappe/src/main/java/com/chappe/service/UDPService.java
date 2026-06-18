package com.chappe.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.chappe.model.Message;
import com.chappe.model.Zustand;

public class UDPService {
    private DatagramSocket socket;

    public UDPService(int port) throws Exception {
        socket = new DatagramSocket(port);
    }

    
    public void send(Message msg, String host, int port) {
        try {
            byte[] data = new byte[5];

            data[0] = (byte) msg.type;
            data[1] = (byte) msg.towerId;
            if(msg.type == 0) {
                //GET request, no Zustand to send
                data[2] = 0;
                data[3] = 0;
                data[4] = 0;
            } else if (msg.type == 1) {
                //RESPONSE, include Zustand
                data[2] = (byte) msg.zustand.getRegulator();
                data[3] = (byte) msg.zustand.getIndicator1();
                data[4] = (byte) msg.zustand.getIndicator2();
            }

            InetAddress address = InetAddress.getByName(host);

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    address,
                    port
            );

            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    public Message receive() {
        try {
            byte[] buffer = new byte[5];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            Message msg = new Message();
            msg.type = buffer[0];
            msg.towerId = buffer[1];
            msg.zustand=Zustand.fromBytes(buffer);
            

            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
