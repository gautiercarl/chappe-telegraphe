package com.chappe.repository;

import java.util.List;

import com.chappe.model.Arm;
import com.chappe.model.Tower;

public class TowerRepository {

    private List<Tower> towers= List.of(
        new Tower(1,new Arm(), new Arm(), new Arm(), 5000, 5001,true),
        new Tower(2,new Arm(), new Arm(), new Arm(), 5002, 5003,false),
        new Tower(3,new Arm(), new Arm(), new Arm(), 5004, 5005,true)
    );

    // Tower t1 = new Tower(1,new Arm(), new Arm(), new Arm(), 5000, 5001);
    // Tower t2 = new Tower(2,new Arm(), new Arm(), new Arm(), 5002, 5003);
    // Tower t3 = new Tower(3,new Arm(), new Arm(), new Arm(), 5004, 5005);
    // t1.setNachfolger(t2);
    // t2.setVorgaenger(t1);
    // t2.setNachfolger(t3);
    // t3.setVorgaenger(t2);

    public static Tower getTowerById(int id) {
        return List.of(
            new Tower(1,new Arm(), new Arm(), new Arm(), 5000, 5001,true),
            new Tower(2,new Arm(), new Arm(), new Arm(), 5002, 5003,false),
            new Tower(3,new Arm(), new Arm(), new Arm(), 5004, 5005,true)
        ).stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }
    
}
