package com.chappe.logik;

import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainTowerNodeTest {

    private MainTowerNode node;

    private void closeSockets(Object nodeObj) throws Exception {
        Field front = nodeObj.getClass().getSuperclass().getDeclaredField("frontUdp");
        Field back = nodeObj.getClass().getSuperclass().getDeclaredField("backUdp");
        front.setAccessible(true);
        back.setAccessible(true);
        Object frontUdp = front.get(nodeObj);
        Object backUdp = back.get(nodeObj);
        for (Object udp : new Object[]{frontUdp, backUdp}) {
            if (udp == null) continue;
            Field s = udp.getClass().getDeclaredField("socket");
            s.setAccessible(true);
            DatagramSocket sock = (DatagramSocket) s.get(udp);
            if (sock != null && !sock.isClosed()) sock.close();
        }
    }

    @After
    public void tearDown() throws Exception {
        if (node != null) closeSockets(node);
    }

    @Test
    public void testGetCodeKeysAndDecodedString() throws Exception {
        Tower t = new Tower(10);
        t.setFrontPort(0);
        t.setBackPort(0);
        t.setRegulator(0);
        t.setIndicator1(0);
        t.setIndicator2(0);
        node = new MainTowerNode(t);

        List<String> words = new ArrayList<>();
        words.add("Hallo");
        words.add("Welt");
        Queue<?> keys = node.getCodeKeys(words);
        Assert.assertEquals(2, keys.size());

        // create signals for position 1,1 which maps to "Hallo" in WordService
        List<Zustand> signals = new ArrayList<>();
        signals.add(new Zustand(1,0,0));
        signals.add(new Zustand(1,0,0));

        String decoded = node.decodedString(signals);
        Assert.assertEquals("Hallo", decoded);
    }

    @Test
    public void testCompositionAndEndSignalsSetPositions() throws Exception {
        Tower t = new Tower(11);
        t.setFrontPort(0);
        t.setBackPort(0);
        node = new MainTowerNode(t);

        node.sendCompositionSignal(t);
        Assert.assertEquals(45, t.getRegulator().getPosition());
        Assert.assertEquals(0, t.getIndicator1().getPosition());
        Assert.assertEquals(0, t.getIndicator2().getPosition());

        node.sendEndOfTransmissionSignal(t);
        Assert.assertEquals(90, t.getRegulator().getPosition());
        Assert.assertEquals(90, t.getIndicator1().getPosition());
        Assert.assertEquals(-90, t.getIndicator2().getPosition());
    }
}
