package husaccttest.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.ModuleFigure;
import husacct.graphics.util.register.DrawingRegister;
import husacct.graphics.util.register.NewDrawingState;

import org.junit.Before;
import org.junit.Test;

public class DrawingRegisterTest {
	private DrawingRegister register;
	private NewDrawingState rootState, domainState;
	private String rootPath, domainPath;

	@Before
	public void setup() {

		register = new DrawingRegister();

		rootPath = "";
		rootState = new NewDrawingState(rootPath);
		register.addState(rootState);

		domainPath = "domain";
		domainState = new NewDrawingState(domainPath);
		register.addState(domainState);
	}

	@Test
	public void addRootState() {
		assertTrue(register.contains(rootState));
	}

	@Test
	public void addSecondState() {
		assertTrue(register.contains(domainState));
	}

	@Test
	public void getState() {
		NewDrawingState state = register.getState(rootPath);
		assertEquals(state, rootState);
	}

	@Test
	public void addFigure() {
		NewDrawingState state = register.getState(rootPath);
		BaseFigure figure = new ModuleFigure("name", "type");
		state.addFigure("domain", figure, new AbstractDTO());
		assertNotNull(state.getFigureDTO(figure));
	}

	@Test
	public void addContext() {
		BaseFigure contextFigure = new ModuleFigure("name", "type");
		AbstractDTO contextDTO = new AnalysedModuleDTO("domain", "domain", "package", "direct");
		NewDrawingState rootState = register.getState(rootPath);
		rootState.addFigure("domain", contextFigure, contextDTO);
		
		domainState.addContextFigure(contextFigure);

		assertNotNull(domainState.getFigureDTO(contextFigure));
	}

	@Test
	public void emp() {

	}
}
