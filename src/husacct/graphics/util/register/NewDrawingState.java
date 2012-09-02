package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ViolationDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.FigureFactory;
import husacct.graphics.presentation.figures.RelationFigure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class NewDrawingState {
	protected NewDrawingState parentState;
	protected String fullPath;
	protected HashMap<String, NewFigureMap> paths, contextPaths;
	protected HashMap<RelationFigure, DependencyDTO[]> dependencyDTOMap;
	protected HashMap<RelationFigure, ViolationDTO[]> violationDTOMap;
	protected HashMap<BaseFigure, ViolationDTO[]> violatedFigureDTOMap;
	protected HashMap<BaseFigure, Double> figurePositions;

	protected int maxDependencies, maxViolations, maxAll;

	public NewDrawingState(String path) {
		fullPath = path;
		paths = new HashMap<String, NewFigureMap>();
		contextPaths = new HashMap<String, NewFigureMap>();
		dependencyDTOMap = new HashMap<RelationFigure, DependencyDTO[]>();
		violationDTOMap = new HashMap<RelationFigure, ViolationDTO[]>();
		violatedFigureDTOMap = new HashMap<BaseFigure, ViolationDTO[]>();
	}

	public void setParentState(NewDrawingState state) {
		parentState = state;
	}

	public boolean hasParentState() {
		return parentState != null;
	}

	public NewDrawingState getParentState() {
		return parentState;
	}

	public String getFullPath() {
		return fullPath;
	}

	public ArrayList<String> getPaths() {
		return new ArrayList<String>(paths.keySet());
	}

	private void addPath(String path) {
		if (!paths.containsKey(path)) {
			paths.put(path, new NewFigureMap());
		}
	}

	private NewFigureMap getPath(String path) {
		return paths.get(path);
	}
	
	public boolean hasContext() {
		return !contextPaths.isEmpty();
	}
	
	private void addContextPath(String path) {
		if (!contextPaths.containsKey(path)) {
			contextPaths.put(path, new NewFigureMap());
		}
	}
	
	private NewFigureMap getContextPath(String path) {
		return contextPaths.get(path);
	}
	
	public ArrayList<String> getContextPaths() {
		return new ArrayList<String>(contextPaths.keySet());
	}

	public void addFigure(String subPath, BaseFigure figure, AbstractDTO dto) {
		addPath(subPath);
		NewFigureMap map = getPath(subPath);
		map.addFigure(figure, dto);
	}

	public void addContextFigure(BaseFigure contextFigure) {
		if (null != parentState) {
			String path = parentState.getParentPathOfFigure(contextFigure);
			AbstractDTO contextDTO = parentState.getFigureDTO(contextFigure);
			if (!path.isEmpty() && null != contextDTO) {
				addContextPath(path);
				NewFigureMap map = getContextPath(path);
				FigureFactory figureFactory = new FigureFactory();
				BaseFigure newContextFigure = figureFactory.createFigure(contextDTO);
				map.addFigure(newContextFigure, contextDTO);
			}
		}
	}

	public AbstractDTO getFigureDTO(BaseFigure figure) {
		AbstractDTO dto = null;
		for (NewFigureMap map : paths.values()) {
			if (map.containsFigure(figure)) {
				dto = map.getFigureDTO(figure);
			}
		}
		for (NewFigureMap map : contextPaths.values()) {
			if (map.containsFigure(figure)) {
				dto = map.getFigureDTO(figure);
			}
		}
		return dto;
	}

	public String getParentFigurePath(BaseFigure figure) {
		String path = null;
		for (String key : paths.keySet()) {
			if (paths.get(key).containsFigure(figure)) {
				path = key;
			}
		}
		return path;
	}
	
	public String getParentPathOfFigure(BaseFigure figure){
		String parentPath = "";
		for(String path : paths.keySet()){
			if(paths.get(path).containsFigure(figure)){
				parentPath = path;
				continue;
			}
		}
		return parentPath;
	}
	
	public ArrayList<BaseFigure> getAllFigures() {
		ArrayList<BaseFigure> figures = new ArrayList<BaseFigure>();
		Collection<NewFigureMap> figureMaps = paths.values();
		for(NewFigureMap map : figureMaps){
			figures.addAll(map.getFigures());
		}
		Collection<NewFigureMap> contextFigureMaps = contextPaths.values();
		for(NewFigureMap map : contextFigureMaps){
			figures.addAll(map.getFigures());
		}
		return figures;
	}

	public ArrayList<BaseFigure> getFiguresByPath(String path) {
		NewFigureMap pathMap = paths.get(path);
		if (null != pathMap) {
			return pathMap.getFigures();
		} else {
			return new ArrayList<BaseFigure>();
		}
	}
	
	public ArrayList<BaseFigure> getContextFiguresByPath(String path) {
		NewFigureMap pathMap = contextPaths.get(path);
		if (null != pathMap) {
			return pathMap.getFigures();
		} else {
			return new ArrayList<BaseFigure>();
		}
	}

	public void addDependency(RelationFigure relationFigure, DependencyDTO[] dtos) {
		setMaxDependencies(dtos.length);
		dependencyDTOMap.put(relationFigure, dtos);
	}

	public DependencyDTO[] getDependencyDTOs(BaseFigure relationFigure) {
		return dependencyDTOMap.get(relationFigure);
	}

	public boolean isDependencyLine(BaseFigure selectedFigure) {
		return dependencyDTOMap.containsKey(selectedFigure);
	}

	public void addViolation(RelationFigure relationFigure, ViolationDTO[] dtos) {
		setMaxViolations(dtos.length);
		violationDTOMap.put(relationFigure, dtos);
	}

	public ViolationDTO[] getViolationDTOs(BaseFigure relationFigure) {
		return violationDTOMap.get(relationFigure);
	}

	public boolean isViolatedFigure(BaseFigure selectedFigure) {
		return violatedFigureDTOMap.containsKey(selectedFigure);
	}

	public void addViolatedFigure(BaseFigure figure, ViolationDTO[] dtos) {
		violatedFigureDTOMap.put(figure, dtos);
	}

	public ViolationDTO[] getViolatedDTOs(BaseFigure figure) {
		return violatedFigureDTOMap.get(figure);
	}

	public boolean isViolationLine(BaseFigure selectedFigure) {
		return violationDTOMap.containsKey(selectedFigure);
	}

	private void setMaxDependencies(int newMax) {
		if (newMax > maxDependencies) {
			maxDependencies = newMax;
		}
		if (newMax > maxAll) {
			maxAll = newMax;
		}
	}

	public int getMaxDependencies() {
		return maxDependencies;
	}

	private void setMaxViolations(int newMax) {
		if (newMax > maxViolations) {
			maxViolations = newMax;
		}
		if (newMax > maxAll) {
			maxAll = newMax;
		}
	}

	public int getMaxViolations() {
		return maxViolations;
	}

	public int getMaxAll() {
		return maxAll;
	}

	public NewDrawingState recreateState() {
		NewDrawingState copy = new NewDrawingState(fullPath);
		copy.setParentState(getParentState());
		for(String key : paths.keySet()){
			NewFigureMap oldMap = paths.get(key);
			copy.addPath(key);
			copy.recreateMap(key, oldMap);
		}
		for(String key : contextPaths.keySet()){
			NewFigureMap oldMap = contextPaths.get(key);
			copy.addPath(key);
			copy.recreateMap(key, oldMap);
		}
		// No need to copy over the dependency and violation entities
		// These are fetched from the service on drawing 
		return copy;
	}
	
	
	private void recreateMap(String path, NewFigureMap oldMap){
		FigureFactory factory = new FigureFactory();
		for(AbstractDTO dto : oldMap.figureDTOMap.values()){
			// New figures are created, because you can't reuse JHotDraw figures.
			BaseFigure newFigure = factory.createFigure(dto);
			addFigure(path, newFigure, dto);
		}
	}
}
