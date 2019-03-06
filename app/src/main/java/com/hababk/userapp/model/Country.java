package com.hababk.userapp.model;

public class Country {
    private String code;
    private String name;
    private String dialCode;

    public Country(String code, String name, String dialCode) {
        this.code = code;
        this.name = name;
        this.dialCode = dialCode;
    }

    public String getDialCode() {
        return dialCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + " (" + getDialCode() + ")";
    }
}
