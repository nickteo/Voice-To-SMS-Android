package com.example.nickteo.voicetosms;

import java.util.ArrayList;

/**
 * Created by Nick Teo on 12/24/2015.
 */
public class Contact {
    private ArrayList<String> numbers;
    private String name;
    private String id;

    /**
     * Constructor for when there is only 1 number
     * @param name
     * @param number
     */
    public Contact(String name, String number, String id) {
        this.name = name;
        this.numbers.add(number);
        this.id = id;
    }

    /**
     * Constructor for when there are more than 1 numbers
     * @param name
     * @param numbers
     */
    public Contact(String name, ArrayList<String> numbers, String id) {
        this.name = name;
        this.numbers = numbers;
        this.id = id;
    }

    /**
     * Add number to numbers array list
     * @param number
     */
    public void addNumber(String number) {
        numbers.add(number);
    }
}
