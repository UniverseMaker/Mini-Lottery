package com.dspark.lottery;

public class LotteryWorkRecyclerItem {
    private int id;
    private String name;
    private String number;

    public LotteryWorkRecyclerItem(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public LotteryWorkRecyclerItem(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
