package com.chappe.client;

import com.chappe.model.Arm;
import com.chappe.model.Tower;
import com.chappe.server.UDPServer;
import com.chappe.service.TowerSender;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

    Tower t1 = new Tower(1,new Arm(), new Arm(), new Arm());
    t1.getRegulator().setPosition(23);
    t1.getIndicator1().setPosition(45);
    t1.getIndicator2().setPosition(67);

    Tower t2 = new Tower(2,new Arm(), new Arm(), new Arm());
    t1.setNachfolger(t2);
    try {
        TowerSender towerSender = new TowerSender();
        towerSender.sendWord(t1,"Hallo");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
