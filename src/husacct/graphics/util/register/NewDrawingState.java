package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ModuleDTO;
import husacct.common.dto.ViolationDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.RelationFigure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class NewDrawingState {
	private NewDrawingState parentState;
	private String fullPath;
	private HashMap<String, NewFigureMap> paths;
	private HashMap<RelationFigure, DependencyDTO[]> dependencyDTOMap;
	private HashMap<RelationFigure, ViolationDTO[]> violationDTOMap;
	private HashMap<BaseFigure, ViolationDTO[]> violatedFigureDTOMap;
	private HashMap<BaseFigure, Double> figurePositions;

	private int maxDependencies, maxViolations, maxAll;

	public NewDrawingState(String path) {
		fullPath = path;
		paths = new HashMap<String, NewFigureMap>();
		dependencyDTOMap = new HashMap<RelationFigure, DependencyDTO[]>();
		violationDTOMap = new HashMap<RelationFigure, ViolationDTO[]>();
		violatedFigureDTOMap = new HashMap<BaseFigure, ViolationDTO[]>();
	}

	public void setParentState(NewDrawingState state) {
		parentState = state;
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

	public void addFigure(String subPath, BaseFigure figure, AbstractDTO dto) {
		addPath(subPath);
		NewFigureMap map = getPath(subPath);
		map.addFigure(figure, dto);
	}

	public AbstractDTO getFigureDTO(BaseFigure figure) {
		AbstractDTO dto = null;
		for (NewFigureMap map : paths.values()) {
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

	public void addContextFigure(BaseFigure contextFigure) {
		if (null != parentState) {
			AbstractDTO contextDTO = parentState.getFigureDTO(contextFigure);
			if (null != contextDTO) {
				String path = null;
				if (contextDTO instanceof ModuleDTO) {
					path = ((ModuleDTO) contextDTO).logicalPath;
				} else if (contextDTO instanceof AnalysedModuleDTO) {
					path = ((AnalysedModuleDTO) contextDTO).uniqueName;
				} else {
					throw new RuntimeException(String.format("DTO type not supported %s", contextDTO.getClass().getName()));
				}
				addFigure(path, contextFigure, contextDTO);
			}
		}
	}
	
	public ArrayList<BaseFigure> getFigures() {
		ArrayList<BaseFigure> figures = new ArrayList<BaseFigure>();
		Collection<NewFigureMap> figureMaps = paths.values();
		for(NewFigureMap map : figureMaps){
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
}
