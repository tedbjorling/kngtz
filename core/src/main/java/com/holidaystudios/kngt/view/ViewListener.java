package com.holidaystudios.kngt.view;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public interface ViewListener {

    public enum EventType {
        fling, keyDown, keyUp, doneMoving
    }

    public void handleViewEvent(EventType type, Object data);
}
