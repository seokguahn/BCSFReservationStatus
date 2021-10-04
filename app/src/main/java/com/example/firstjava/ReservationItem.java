package com.example.firstjava;

import java.util.List;

public class ReservationItem {
    public int type;
    public String text;
    public List<ReservationItem> invisibleChildren;

    public ReservationItem() {
    }

    public ReservationItem(int type, String text) {
        this.type = type;
        this.text = text;
    }
}
