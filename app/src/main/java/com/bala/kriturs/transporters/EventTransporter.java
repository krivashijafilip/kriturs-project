package com.bala.kriturs.transporters;

import com.bala.kriturs.models.Event;

public class EventTransporter {
    private static Event event;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event event) {
        EventTransporter.event = event;
    }
}
