package husaccttest.graphics;

import static org.junit.Assert.*;
import husacct.common.dto.AbstractDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ViolationDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.task.DrawingController;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class DrawingControllerTest extends DrawingController {

	@Before
	public void setup() {
	}
	
	@Test
	public void createCombinedPathHelper(){
		ArrayList<String> paths = new ArrayList<String>();
		paths.add("a");
		paths.add("b");
		assertEquals("a+b", createCombinedPathHelper(paths));
		paths.add("c");
		assertEquals("a+b+c", createCombinedPathHelper(paths));
	}
	
	@Test
	public void createCombinedPathHelperForRoot(){
		ArrayList<String> paths = new ArrayList<String>();
		assertEquals("", createCombinedPathHelper(paths));
		
		paths.add("");
		assertEquals("", createCombinedPathHelper(paths));
	}

	@Override
	public void moduleOpen(String[] paths) {
	}

	@Override
	public void moduleZoom(BaseFigure[] zoomedModuleFigure) {
	}

	@Override
	public void moduleZoomOut() {
	}

	@Override
	protected DependencyDTO[] getDependenciesBetween(AbstractDTO dtoFrom, AbstractDTO dtoTo) {
		return null;
	}

	@Override
	protected ViolationDTO[] getViolationsBetween(BaseFigure figureFrom, BaseFigure figureTo) {
		return null;
	}

	@Override
	public void refreshDrawing() {
	}
}
