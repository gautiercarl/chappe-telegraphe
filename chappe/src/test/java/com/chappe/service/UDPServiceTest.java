package com.chappe.service;

import com.chappe.model.Message;
import com.chappe.model.Zustand;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServiceTest {

    @Test
    public void testSendGetMessage() throws Exception {
        try (DatagramSocket receiver = new DatagramSocket(0)) {
            int listenPort = receiver.getLocalPort();

            UDPService svc = new UDPService(0);

            Message msg = new Message();
            msg.type = 0;
            msg.towerId = 42;

            svc.send(msg, "127.0.0.1", listenPort);

            byte[] buf = new byte[5];
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            receiver.setSoTimeout(2000);
            receiver.receive(p);

            Assert.assertEquals(0, buf[0]);
            Assert.assertEquals(42, buf[1]);
            Assert.assertEquals(0, buf[2]);
            Assert.assertEquals(0, buf[3]);
            Assert.assertEquals(0, buf[4]);
        }
    }

    @Test
    public void testSendResponseMessage() throws Exception {
        try (DatagramSocket receiver = new DatagramSocket(0)) {
            int listenPort = receiver.getLocalPort();

            UDPService svc = new UDPService(0);

            Message msg = new Message();
            msg.type = 1;
            msg.towerId = 7;
            msg.zustand = new Zustand(3, 4, 5);

            svc.send(msg, "127.0.0.1", listenPort);

            byte[] buf = new byte[5];
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            receiver.setSoTimeout(2000);
            receiver.receive(p);

            Assert.assertEquals(1, buf[0]);
            Assert.assertEquals(7, buf[1]);
            Assert.assertEquals(3, buf[2]);
            Assert.assertEquals(4, buf[3]);
            Assert.assertEquals(5, buf[4]);
        }
    }

    @Test
    public void testReceiveParsesMessage() throws Exception {
        UDPService svc = new UDPService(0);

        // get the internal socket's local port via reflection
        Field socketField = UDPService.class.getDeclaredField("socket");
        socketField.setAccessible(true);
        DatagramSocket internal = (DatagramSocket) socketField.get(svc);
        int port = internal.getLocalPort();

        try (DatagramSocket sender = new DatagramSocket()) {
            // build a 5 byte packet: type=1, towerId=9, zustand 1,2,3
            byte[] data = new byte[]{1, 9, 1, 2, 3};
            DatagramPacket p = new DatagramPacket(data, data.length, java.net.InetAddress.getByName("127.0.0.1"), port);
            sender.send(p);

            Message received = svc.receive();
            Assert.assertNotNull(received);
            Assert.assertEquals(1, received.type);
            Assert.assertEquals(9, received.towerId);
            Assert.assertNotNull(received.zustand);
            Assert.assertEquals(1, received.zustand.getRegulator());
            Assert.assertEquals(2, received.zustand.getIndicator1());
            Assert.assertEquals(3, received.zustand.getIndicator2());
        }
    }
}
