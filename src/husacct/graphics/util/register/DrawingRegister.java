package husacct.graphics.util.register;

import java.util.HashMap;

public class DrawingRegister {
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

	public void addState(NewDrawingState state) {
		if (contains(state)) {
			NewDrawingState parentState = getState(state.getFullPath()).getParentState();
			previousState = parentState;
		} else {
			previousState = currentState;
		}
		currentState = state;
		if (null != previousState) {
			state.setParentState(previousState);
		}
		states.put(state.getFullPath(), state);
	}

	public NewDrawingState getState(String path) {
		return states.get(path);
	}

	public boolean contains(NewDrawingState state) {
		return states.containsKey(state.getFullPath());
	}
}
