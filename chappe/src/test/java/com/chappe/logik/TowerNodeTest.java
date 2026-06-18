package com.chappe.logik;

import com.chappe.model.Message;
import com.chappe.model.Tower;
import com.chappe.model.Zustand;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class TowerNodeTest {

    private TowerNode node;

    private void closeSockets(Object nodeObj) throws Exception {
        Field front = nodeObj.getClass().getDeclaredField("frontUdp");
        Field back = nodeObj.getClass().getDeclaredField("backUdp");
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
        if (node != null) {
            closeSockets(node);
        }
    }

    @Test
    public void testSignalChecks() throws Exception {
        Tower t = new Tower(1);
        t.setFrontPort(0);
        t.setBackPort(0);
        t.setRegulator(0);
        t.setIndicator1(0);
        t.setIndicator2(0);

        node = new TowerNode(t);

        Message mComp = new Message();
        mComp.zustand = new Zustand(45, 0, 0);
        Assert.assertTrue(node.isCompositionSignal(mComp));

        Message mEnd = new Message();
        mEnd.zustand = new Zustand(90, 90, -90);
        Assert.assertTrue(node.isEndOfTransmissionSignal(mEnd));

        Message mSec = new Message();
        mSec.zustand = new Zustand(0, 0, 0);
        Assert.assertTrue(node.isSecurityPositionSignal(mSec));
    }

    @Test
    public void testIsNewStateAndUpdateStateAddsSignal() throws Exception {
        Tower t = new Tower(2);
        t.setFrontPort(0);
        t.setBackPort(0);
        t.setRegulator(0);
        t.setIndicator1(0);
        t.setIndicator2(0);

        Tower next = new Tower(3);
        next.setRegulator(0);
        next.setIndicator1(0);
        next.setIndicator2(0);
        t.setNachfolger(next);

        node = new TowerNode(t);

        Message m = new Message();
        m.type = 1;
        m.towerId = 99;
        m.zustand = new Zustand(1, 1, 1);

        Assert.assertTrue(node.isNewState(m));
        node.updateState(m);

        Assert.assertEquals(1, t.getRegulator().getPosition());
        Assert.assertEquals(1, t.getIndicator1().getPosition());
        Assert.assertEquals(1, t.getIndicator2().getPosition());

        Assert.assertFalse(node.getSignals().isEmpty());
        Assert.assertEquals(1, node.getSignals().get(0).getRegulator());
    }
}
