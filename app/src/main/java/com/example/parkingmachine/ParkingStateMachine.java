package com.example.parkingmachine;

import com.example.parkingmachine.ParkingEvent;
import com.example.parkingmachine.ParkingState;

import java.util.HashMap;
import java.util.Map;

public class ParkingStateMachine {

    private ParkingState currentState;

    private final Map<ParkingState, Map<ParkingEvent, ParkingState>> transitions;

    public ParkingStateMachine() {
        currentState = ParkingState.FREE;
        transitions = new HashMap<>();
        initTransitions();
    }

    private void initTransitions() {

        addTransition(ParkingState.FREE, ParkingEvent.RESERVE, ParkingState.RESERVED);

        addTransition(ParkingState.RESERVED, ParkingEvent.START_SESSION, ParkingState.ACTIVE);
        addTransition(ParkingState.RESERVED, ParkingEvent.RESET, ParkingState.FREE);

        addTransition(ParkingState.ACTIVE, ParkingEvent.FINISH_SESSION, ParkingState.FINISHED);
        addTransition(ParkingState.ACTIVE, ParkingEvent.RESET, ParkingState.FREE);

        addTransition(ParkingState.FINISHED, ParkingEvent.PAY, ParkingState.PAID);
        addTransition(ParkingState.FINISHED, ParkingEvent.RESET, ParkingState.FREE);

        addTransition(ParkingState.PAID, ParkingEvent.RESET, ParkingState.FREE);
    }

    private void addTransition(ParkingState from, ParkingEvent event, ParkingState to) {
        transitions
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(event, to);
    }

    public ParkingState getCurrentState() {
        return currentState;
    }

    public boolean handleEvent(ParkingEvent event) {
        Map<ParkingEvent, ParkingState> stateTransitions = transitions.get(currentState);

        if (stateTransitions != null && stateTransitions.containsKey(event)) {
            currentState = stateTransitions.get(event);
            return true;
        }
        return false;
    }
}
