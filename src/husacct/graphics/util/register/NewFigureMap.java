package husacct.graphics.util.register;

import husacct.common.dto.AbstractDTO;
import husacct.graphics.presentation.figures.BaseFigure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class NewFigureMap {
	private HashMap<BaseFigure, AbstractDTO> figureDTOMap;
	private HashMap<BaseFigure, Double> figurePositions;
	
	public NewFigureMap(){
		figureDTOMap = new HashMap<BaseFigure, AbstractDTO>(); 
	}
	
	public void addFigure(BaseFigure figure, AbstractDTO dto){
		figureDTOMap.put(figure, dto);
	}

	public boolean containsFigure(BaseFigure figure) {
		return figureDTOMap.containsKey(figure);
	}

	public AbstractDTO getFigureDTO(BaseFigure figure) {
		return figureDTOMap.get(figure);
	}

	public ArrayList<BaseFigure> getFigures() {
		return new ArrayList<BaseFigure>(figureDTOMap.keySet());
	}
}
