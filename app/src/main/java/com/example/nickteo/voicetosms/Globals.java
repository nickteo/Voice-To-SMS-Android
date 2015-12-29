package com.example.nickteo.voicetosms;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Nick Teo on 12/24/2015.
 */
public class Globals {

    public static final int  KEY_PHONE_NUMBER = 0;
    public static final int  KEY_MESSAGE = 1;
    public static final int  KEY_NUM_CONTACTS = 2;
    public static final int  KEY_NEED_CONTACTS = 3;
    public static final int  KEY_FIRST_CONTACT = 100;
    public static final UUID APP_UUID = UUID.fromString("5f8e15dd-acad-4d8b-9f01-1869ef95b57e");

    public static ArrayList<Contact> favorites;
}
