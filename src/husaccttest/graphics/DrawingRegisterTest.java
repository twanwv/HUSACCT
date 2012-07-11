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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DrawingRegisterTest {
	private DrawingRegister register;
	private NewDrawingState rootState, domainState, domainBlogState;
	private String rootPath, domainPath, domainBlogPath;

	@Before
	public void setup() {
		register = new DrawingRegister();
	}

	@After
	public void cleanup() {
		register.clear();
	}

	private void createRootState() {
		rootPath = "";
		rootState = new NewDrawingState(rootPath);
		register.addState(rootState);
	}

	private void createDomainState() {
		domainPath = "domain";
		domainState = new NewDrawingState(domainPath);
		register.addState(domainState);
	}

	private void createDomainBlogState() {
		domainBlogPath = "domain.blog";
		domainBlogState = new NewDrawingState(domainBlogPath);
		register.addState(domainBlogState);
	}

	@Test
	public void addRootState() {
		createRootState();
		assertTrue(register.contains(rootState));
	}

	@Test
	public void addMoreStates() {
		createRootState();
		createDomainState();
		createDomainBlogState();
		assertTrue(register.contains(domainState));
		assertTrue(register.contains(domainBlogState));
	}

	@Test
	public void reopenStateAlreadyCreated() {
		createRootState();
		createDomainState();
		createDomainBlogState();
		createDomainState(); // Zoom out
		assertTrue(register.contains(domainState));
		assertEquals(register.getCurrentState(), domainState);
		assertEquals(register.getCurrentState().getParentState(), rootState);
	}

	@Test
	public void getState() {
		createRootState();
		NewDrawingState state = register.getCurrentState();
		assertNotNull(state);
		assertEquals(state, rootState);
	}

	@Test
	public void addFigure() {
		createRootState();
		NewDrawingState state = register.getCurrentState();
		BaseFigure figure = new ModuleFigure("name", "type");
		AbstractDTO dto = new AbstractDTO();
		state.addFigure("domain", figure, dto);
		assertNotNull(state.getFigureDTO(figure));
		assertEquals(dto, state.getFigureDTO(figure));
	}
	
	@Test
	public void returnPaths() {
		createRootState();
		NewDrawingState state = register.getCurrentState();
		BaseFigure figureOne = new ModuleFigure("name", "type");
		AbstractDTO dtoOne = new AbstractDTO();
		state.addFigure("domain", figureOne, dtoOne);
		
		BaseFigure figureTwo = new ModuleFigure("name", "type");
		AbstractDTO dtoTwo = new AbstractDTO();
		state.addFigure("presentation", figureTwo, dtoTwo);
		
		assertEquals(2, state.getPaths().size());
	}
	
	@Test
	public void returnFigures() {
		createRootState();
		NewDrawingState state = register.getCurrentState();
		BaseFigure figureOne = new ModuleFigure("name", "type");
		AbstractDTO dtoOne = new AbstractDTO();
		state.addFigure("domain", figureOne, dtoOne);
		
		BaseFigure figureTwo = new ModuleFigure("name", "type");
		AbstractDTO dtoTwo = new AbstractDTO();
		state.addFigure("presentation", figureTwo, dtoTwo);
		
		BaseFigure figureThree = new ModuleFigure("name", "type");
		AbstractDTO dtoThree = new AbstractDTO();
		state.addFigure("presentation", figureThree, dtoThree);
		
		assertEquals(1, state.getFiguresByPath("domain").size());
		assertEquals(2, state.getFiguresByPath("presentation").size());
	}

	@Test
	public void addContext() {
		createRootState();
		createDomainState();
		BaseFigure contextFigure = new ModuleFigure("name", "type");
		AbstractDTO contextDTO = new AnalysedModuleDTO("domain", "domain", "package", "direct");
		NewDrawingState rootState = register.getState(rootPath);
		rootState.addFigure("domain", contextFigure, contextDTO);

		NewDrawingState state = register.getCurrentState();
		state.addContextFigure(contextFigure);

		assertNotNull(domainState.getFigureDTO(contextFigure));
		assertEquals(contextDTO, domainState.getFigureDTO(contextFigure));
	}

	@Test
	public void emp() {

	}
}
