package com.damhoe.fieldlines.application;

/**
 * Created on 09.12.2017.
 */

public class Element implements IElement {
    private final String name;
    private final String value;

    public Element(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
}
