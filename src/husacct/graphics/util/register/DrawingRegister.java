package husacct.graphics.util.register;

import husacct.graphics.task.AnalysedController;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class DrawingRegister {
	private Logger logger = Logger.getLogger(DrawingRegister.class);
	private NewDrawingState currentState, previousState;
	private HashMap<String, NewDrawingState> states;

	public DrawingRegister() {
		states = new HashMap<String, NewDrawingState>();
	}

	public void clear() {
		states.clear();
		currentState = null;
		previousState = null;
	}

	public NewDrawingState getCurrentState() {
		return currentState;
	}
	
	public boolean hasPreviousAsCurrentState() {
		return currentState.hasParentState();
	}

	public void setPreviousAsCurrentState() {
		if (hasPreviousAsCurrentState()) {
			currentState = currentState.getParentState();
			currentState = currentState.recreateState();
		} else {
			logger.warn("Can't reopen a previous state if there is non. This is probably the root of the application.");
		}
	}

	public void addState(NewDrawingState state) {
		System.err.println(states.keySet().toString());
		System.err.println("key = " + state.getFullPath());
		if (contains(state)) {
			logger.warn("State is known, get old parent");
			NewDrawingState parentState = getState(state.getFullPath()).getParentState();
			previousState = parentState;
		} else {
			logger.warn("State is not known, current state is parent");
			previousState = currentState;
		}
		currentState = state;
		if (null != previousState) {
			state.setParentState(previousState);
		}
		states.put(state.getFullPath(), state);
		previousState = null;
	}

	public NewDrawingState getState(String path) {
		return states.get(path);
	}

	public boolean contains(NewDrawingState state) {
		return states.containsKey(state.getFullPath());
	}
}
