package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.graphics.presentation.figures.BaseFigure;

import java.util.HashMap;

public class NewDrawingState {
	private String fullPath;
	private HashMap<String, NewFigureMap> paths;
	
	public NewDrawingState(String path){
		fullPath = path;
		paths = new HashMap<String, NewFigureMap>();
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
	
	public void addContextFigure(String originalPath, BaseFigure figure, AbstractDTO dto){
		addPath(originalPath);
		NewFigureMap map = getPath(originalPath);
		map.addFigure(figure, dto);
	}

}
