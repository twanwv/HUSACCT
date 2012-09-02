package husaccttest.graphics;

import static org.junit.Assert.*;

import java.awt.Color;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ViolationDTO;
import husacct.common.dto.ViolationTypeDTO;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.presentation.figures.FigureFactory;
import husacct.graphics.presentation.figures.ModuleFigure;
import husacct.graphics.presentation.figures.RelationFigure;
import husacct.graphics.util.register.DrawingRegister;
import husacct.graphics.util.register.DrawingState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DrawingRegisterTest {
	private DrawingRegister register;
	private DrawingState rootState, domainState, domainBlogState;
	private String rootPath, domainPath, domainBlogPath;
	private FigureFactory factory;

	@Before
	public void setup() {
		register = new DrawingRegister();
		factory = new FigureFactory();
	}

	@After
	public void cleanup() {
		register.clear();
	}

	private void createRootState() {
		rootPath = "";
		rootState = new DrawingState(rootPath);
		register.addState(rootState);
	}

	private void createDomainState() {
		domainPath = "domain";
		domainState = new DrawingState(domainPath);
		register.addState(domainState);
	}

	private void createDomainBlogState() {
		domainBlogPath = "domain.blog";
		domainBlogState = new DrawingState(domainBlogPath);
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
		
		DrawingState state = register.getCurrentState();
		BaseFigure figure = new ModuleFigure("name", "type");
		AbstractDTO dto = new AbstractDTO();
		state.addFigure("domain", figure, dto);
		
		createDomainBlogState();
		createDomainState(); // Zoom out
		
		state = register.getCurrentState();
		assertEquals(0, state.getFiguresByPath("domain").size());
		
		assertTrue(register.contains(domainState));
		assertEquals(register.getCurrentState(), domainState);
		assertEquals(register.getCurrentState().getParentState(), rootState);
	}

	@Test
	public void getState() {
		createRootState();
		DrawingState state = register.getCurrentState();
		assertNotNull(state);
		assertEquals(state, rootState);
	}

	@Test
	public void addFigure() {
		createRootState();
		DrawingState state = register.getCurrentState();
		BaseFigure figure = new ModuleFigure("name", "type");
		AbstractDTO dto = new AbstractDTO();
		state.addFigure("domain", figure, dto);
		assertNotNull(state.getFigureDTO(figure));
		assertEquals(dto, state.getFigureDTO(figure));
	}

	@Test
	public void addContext() {
		createRootState();
		createDomainState();
		BaseFigure contextFigure = new ModuleFigure("name", "type");
		AbstractDTO contextDTO = new AnalysedModuleDTO("domain", "domain", "package", "direct");
		DrawingState rootState = register.getState(rootPath);
		rootState.addFigure("domain", contextFigure, contextDTO);

		DrawingState state = register.getCurrentState();
		state.addContextFigure(contextFigure);

		assertNotNull(domainState.getFigureDTO(contextFigure));
		assertEquals(contextDTO, domainState.getFigureDTO(contextFigure));
	}
	
	@Test
	public void addDependency() {
		createRootState();
		DrawingState state = register.getCurrentState();
		DependencyDTO[] dtos = new DependencyDTO[]{ new DependencyDTO("domain", "presentation", "import", false, 1)};
		RelationFigure dependencyFigure = factory.createFigure(dtos);
		state.addDependency(dependencyFigure, dtos);
		assertNotNull(state.getDependencyDTOs(dependencyFigure));
		assertArrayEquals(dtos, state.getDependencyDTOs(dependencyFigure));
	}

	@Test
	public void maxDependenciesCountUpdated() {
		createRootState();
		DrawingState state = register.getCurrentState();
		
		DependencyDTO[] dtosCollectionOne = new DependencyDTO[]{ new DependencyDTO("domain", "presentation", "import", false, 1)};
		RelationFigure dependencyFigureOne = factory.createFigure(dtosCollectionOne);
		state.addDependency(dependencyFigureOne, dtosCollectionOne);		
		assertEquals(1, state.getMaxDependencies());
		
		DependencyDTO[] dtosCollectionTwo = new DependencyDTO[]{ new DependencyDTO("domain", "presentation", "import", false, 1), new DependencyDTO("domain", "presentation", "import", false, 2) };
		RelationFigure dependencyFigureTwo = factory.createFigure(dtosCollectionTwo);
		state.addDependency(dependencyFigureTwo, dtosCollectionTwo);			
		assertEquals(2, state.getMaxDependencies());
	}
	
	@Test
	public void addViolation() {
		createRootState();
		DrawingState state = register.getCurrentState();
		ViolationDTO[] dtos = new ViolationDTO[]{ new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 1, Color.red, "High", 3, false)};
		RelationFigure violationFigure = factory.createFigure(dtos);
		state.addViolation(violationFigure, dtos);
		assertNotNull(state.getViolationDTOs(violationFigure));
		assertArrayEquals(dtos, state.getViolationDTOs(violationFigure));
	}
	
	@Test
	public void maxViolationsCountUpdated() {
		createRootState();
		DrawingState state = register.getCurrentState();
		
		ViolationDTO[] dtosCollectionOne = new ViolationDTO[]{ new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 1, Color.red, "High", 3, false) };
		RelationFigure violationFigureOne = factory.createFigure(dtosCollectionOne);
		state.addViolation(violationFigureOne, dtosCollectionOne);		
		assertEquals(1, state.getMaxViolations());
		
		ViolationDTO[] dtosCollectionTwo = new ViolationDTO[]{ new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 1, Color.red, "High", 3, false), new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 2, Color.red, "High", 3, false) };
		RelationFigure violationFigureTwo = factory.createFigure(dtosCollectionTwo);
		state.addViolation(violationFigureTwo, dtosCollectionTwo);			
		assertEquals(2, state.getMaxViolations());
	}
	

	@Test
	public void maxAllCountUpdated() {
		createRootState();
		DrawingState state = register.getCurrentState();
		
		DependencyDTO[] dtosCollectionOne = new DependencyDTO[]{ new DependencyDTO("domain", "presentation", "import", false, 1)};
		RelationFigure dependencyFigureOne = factory.createFigure(dtosCollectionOne);
		state.addDependency(dependencyFigureOne, dtosCollectionOne);		
		assertEquals(1, state.getMaxDependencies());
		
		ViolationDTO[] dtosCollectionTwo = new ViolationDTO[]{ new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 1, Color.red, "High", 3, false), new ViolationDTO("domain", "presentation", "domain", "presentation", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between domain and presentation", 2, Color.red, "High", 3, false) };
		RelationFigure violationFigureTwo = factory.createFigure(dtosCollectionTwo);
		state.addViolation(violationFigureTwo, dtosCollectionTwo);			
		assertEquals(2, state.getMaxViolations());
		
		DependencyDTO[] dtosCollectionThree = new DependencyDTO[]{ new DependencyDTO("domain", "presentation", "import", false, 1), new DependencyDTO("domain", "presentation", "import", false, 2), new DependencyDTO("domain", "presentation", "import", false, 3), new DependencyDTO("domain", "presentation", "import", false, 4)};
		RelationFigure dependencyFigureThree = factory.createFigure(dtosCollectionThree);
		state.addDependency(dependencyFigureThree, dtosCollectionThree);			
		assertEquals(4, state.getMaxDependencies());
	}

	@Test
	public void returnPaths() {
		createRootState();
		DrawingState state = register.getCurrentState();
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
		DrawingState state = register.getCurrentState();
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
	public void emp() {

	}
}
