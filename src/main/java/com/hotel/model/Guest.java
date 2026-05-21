package com.hotel.model;

public class Guest {
    private final int id;
    private final String name;
    private final String email;
    private final String phone;

    public Guest(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
