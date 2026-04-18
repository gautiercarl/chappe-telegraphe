package com.chappe.server;

import com.chappe.model.Arm;
import com.chappe.model.Tower;
import com.chappe.service.TowerSender;

public class server {
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        UDPServer server = new UDPServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
    }
}
}
