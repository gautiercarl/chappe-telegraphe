package com.chappe.network;

import java.util.List;

import com.chappe.model.Tower;

public class FranceNetwork {

    //paris - lyon - marseille
   
    private List<Tower> towersParisLyonMarseille;

  public FranceNetwork(List<Tower> towers) {
        this.towersParisLyonMarseille = towers;
    }

    public List<Tower> getTowers() {
        return towersParisLyonMarseille;
    }
    
}
