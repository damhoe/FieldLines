package com.damhoe.fieldlines;

/**
 * Created by shoedtke on 09.12.2017.
 */

class Element implements IElement {
    private final String name;
    private final String value;

    Element(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
}
