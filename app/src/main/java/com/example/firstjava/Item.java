package com.example.firstjava;

public class Item {
    String name;
    String intoUrl;
    String bill;
    String receptionStatus;

    String getTitle() {
        return this.name;
    }
    String getIntoUrl() {
        return this.intoUrl;
    }
    String getBill() {
        return this.bill;
    }
    String getReceptionStatus() {
        return this.receptionStatus;
    }

    Item(String name, String intoUrl, String bill, String receptionStatus) {
        this.name = name;
        this.intoUrl = intoUrl;
        this.bill = bill;
        this.receptionStatus = receptionStatus;
    }
}
