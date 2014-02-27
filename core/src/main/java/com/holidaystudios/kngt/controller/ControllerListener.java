package com.holidaystudios.kngt.controller;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public interface ControllerListener {

    public enum EventType {
        knightNewRoom
    }

    public void handleControllerEvent(EventType type, Object emitter, Object data);
}
