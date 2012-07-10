package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.ModuleDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.graphics.presentation.figures.BaseFigure;

import java.util.HashMap;

public class DrawingRegister {
	private NewDrawingState previousState, currentState;
	private HashMap<String, NewDrawingState> states;

	public DrawingRegister() {
		states = new HashMap<String, NewDrawingState>();
	}
	
	public NewDrawingState getCurrentState(){
		return currentState;
	}

	public void addState(NewDrawingState state) {
		states.put(state.getPath(), state);
		previousState = currentState;
		currentState = state;
	}

	public NewDrawingState getState(String path) {
		return states.get(path);
	}

	public boolean contains(NewDrawingState state) {
		return states.containsValue(state);
	}

	public void addContextFigure(BaseFigure contextFigure) {
		if (null != previousState) {
			AbstractDTO contextDTO = previousState.getFigureDTO(contextFigure);
			String path = null;
			if(contextDTO instanceof ModuleDTO){
				path = ((ModuleDTO) contextDTO).logicalPath;
			}else if (contextDTO instanceof AnalysedModuleDTO){
				path = ((AnalysedModuleDTO) contextDTO).uniqueName;
			}else{
				throw new RuntimeException("DTO type not supported");
			}
			currentState.addContextFigure(path, contextFigure, contextDTO);
		}
	}
}
