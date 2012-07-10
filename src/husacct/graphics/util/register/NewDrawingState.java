package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.common.dto.ModuleDTO;
import husacct.graphics.presentation.figures.BaseFigure;

import java.util.HashMap;

public class NewDrawingState {
	private NewDrawingState parentState;
	private String fullPath;
	private HashMap<String, NewFigureMap> paths;
	
	public NewDrawingState(String path){
		fullPath = path;
		paths = new HashMap<String, NewFigureMap>();
	}
	
	public void setParentState(NewDrawingState state){
		parentState = state;
	}
	
	public NewDrawingState getParentState(){
		return parentState;
	}
	
	public String getPath(){
		return fullPath;
	}
	
	private void addPath(String path) {
		if (!paths.containsKey(path)) {
			paths.put(path, new NewFigureMap());
		}
	}
	
	private NewFigureMap getPath(String path){
		return paths.get(path);
	}

	public void addFigure(String subPath, BaseFigure figure, AbstractDTO dto) {
		addPath(subPath);
		NewFigureMap map = getPath(subPath);
		map.addFigure(figure, dto);
	}
	
	public AbstractDTO getFigureDTO(BaseFigure figure){
		AbstractDTO dto = null;
		for(NewFigureMap map : paths.values()){
			if(map.containsFigure(figure)){
				dto = map.getFigureDTO(figure);
			}
		}
		return dto;
	}
	
	public String getFigurePath(BaseFigure figure){
		String path = null;
		for(String key : paths.keySet()){
			if(paths.get(key).containsFigure(figure)){
				path = key;
			}
		}
		return path;
	}

	public void addContextFigure(BaseFigure contextFigure) {
		if (null != parentState) {
			AbstractDTO contextDTO = parentState.getFigureDTO(contextFigure);
			String path = null;
			if(contextDTO instanceof ModuleDTO){
				path = ((ModuleDTO) contextDTO).logicalPath;
			}else if (contextDTO instanceof AnalysedModuleDTO){
				path = ((AnalysedModuleDTO) contextDTO).uniqueName;
			}else{
				throw new RuntimeException("DTO type not supported");
			}
			addFigure(path, contextFigure, contextDTO);
		}
	}
}
