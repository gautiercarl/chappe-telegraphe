package com.chappe.utils;

import com.chappe.model.Tower;

public class Functions {

    public static boolean hasNextTowerTheSameState(Tower t) {// to implement
    Tower next = t.getNachfolger();
    if (next == null) return false;
    // send request to next tower
    // udp.send(request, "localhost", next.getPort());

    return next.getRegulator().getPosition() == t.getRegulator().getPosition() &&
           next.getIndicator1().getPosition() == t.getIndicator1().getPosition() &&
           next.getIndicator2().getPosition() == t.getIndicator2().getPosition();
}
    
}
