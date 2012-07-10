package husacct.graphics.util.register;

import java.util.HashMap;

public class DrawingRegister {
	private NewDrawingState previousState;
	private HashMap<String, NewDrawingState> states;

	public DrawingRegister() {
		states = new HashMap<String, NewDrawingState>();
	}

	public void addState(NewDrawingState state) {
		states.put(state.getPath(), state);
		if(null!=previousState){
			state.setParentState(previousState);
		}
		previousState = state;
	}

	public NewDrawingState getState(String path) {
		return states.get(path);
	}

	public boolean contains(NewDrawingState state) {
		return states.containsValue(state);
	}
}
