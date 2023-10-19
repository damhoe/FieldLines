package com.damhoe.fieldlines.domain;

import android.graphics.Point;

import java.util.UUID;

/**
 * Created by damian on 25.11.2017.
 */
public class Charge {
    private final UUID uuid;
    public Point position;
    public double amount;

    private Charge(UUID uuid, Point position, double amount) {
        this.uuid = uuid;
        this.position = position;
        this.amount = amount;
    }

    public Charge(Point position, double amount){
        uuid = UUID.randomUUID();
        this.position = position;
        this.amount = amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Charge deepCopy() {
        return new Charge(uuid, position, amount);
    }
}
