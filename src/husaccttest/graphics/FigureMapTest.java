package husaccttest.graphics;

import java.awt.Color;

import husacct.common.dto.AbstractDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ViolationDTO;
import husacct.common.dto.ViolationTypeDTO;
import husacct.graphics.presentation.figures.ModuleFigure;
import husacct.graphics.presentation.figures.RelationFigure;
import husacct.graphics.util.FigureMap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FigureMapTest {
	private FigureMap figureMap;
	private DependencyDTO[] dependenciesCollection1, dependenciesCollection2;
	private ViolationDTO[] violationsCollection1, violationsCollection2;
	private ModuleFigure normalFigure;
	private RelationFigure dependencyLineFigure1, dependencyLineFigure2;
	private RelationFigure violationLineFigure1, violationLineFigure2;
	private ModuleFigure violatedFigure;

	@Before
	public void setup() {
		figureMap = new FigureMap();
		dependenciesCollection1 = new DependencyDTO[1];
		dependenciesCollection1[0] = new DependencyDTO("from", "to", "type", 1);
		dependenciesCollection2 = new DependencyDTO[4];
		dependenciesCollection2[0] = new DependencyDTO("from", "to", "type", 1);
		dependenciesCollection2[1] = new DependencyDTO("from", "to", "type", 1);
		dependenciesCollection2[2] = new DependencyDTO("from", "to", "type", 1);
		dependenciesCollection2[3] = new DependencyDTO("from", "to", "type", 1);

		violationsCollection1 = new ViolationDTO[1];
		violationsCollection1[0] = new ViolationDTO("physicalFrom", "physicalTo", "logicalFrom", "logicalTo", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between a and b", 1, Color.red, "High", 3, false);
		violationsCollection2 = new ViolationDTO[2];
		violationsCollection2[0] = new ViolationDTO("physicalFrom", "physicalTo", "logicalFrom", "logicalTo", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between a and b", 1, Color.red, "High", 3, false);
		violationsCollection2[1] = new ViolationDTO("physicalFrom", "physicalTo", "logicalFrom", "logicalTo", new ViolationTypeDTO("import", "importDescription", true), null, "Violation between a and b", 1, Color.red, "High", 3, false);

		normalFigure = new ModuleFigure("Test figure", "Test type");
		dependencyLineFigure1 = new RelationFigure("Dependency from a to b", false, 0);
		dependencyLineFigure2 = new RelationFigure("Dependency from c to d", false, 0);
		violationLineFigure1 = new RelationFigure("Violation from a to b", true, 0);
		violationLineFigure2 = new RelationFigure("Violation from c to d", true, 0);
		violatedFigure = new ModuleFigure("Test figure", "Test type");
	}

	@Test
	public void clearAll() {
		figureMap.linkModule(normalFigure, new AbstractDTO());
		figureMap.linkDependencies(dependencyLineFigure1, dependenciesCollection1);
		figureMap.linkViolations(violationLineFigure1, violationsCollection1);
		figureMap.linkViolatedModule(violatedFigure, violationsCollection1);
		assertNotNull(figureMap.getModuleDTO(normalFigure));
		figureMap.linkViolations(violationLineFigure1, violationsCollection1);
		figureMap.linkDependencies(dependencyLineFigure2, dependenciesCollection2);
		assertEquals(4, figureMap.getMaxAll());

		figureMap.clearAll();
		assertNull(figureMap.getModuleDTO(normalFigure));
		assertEquals(0, figureMap.getMaxDependencies());
		assertEquals(0, figureMap.getMaxViolations());
		assertEquals(0, figureMap.getMaxAll());
	}

	@Test
	public void addFigure() {
		figureMap.linkModule(normalFigure, new AbstractDTO());
		assertNotNull(figureMap.getModuleDTO(normalFigure));
	}

	@Test
	public void addDependencyLine() {
		figureMap.linkDependencies(dependencyLineFigure1, dependenciesCollection1);
		assertNotNull(figureMap.getDependencyDTOs(dependencyLineFigure1));
		assertTrue(figureMap.isDependencyLine(dependencyLineFigure1));
	}

	@Test
	public void addViolationLine() {
		figureMap.linkViolations(violationLineFigure1, violationsCollection1);
		assertNotNull(figureMap.getViolationDTOs(violationLineFigure1));
		assertTrue(figureMap.isViolationLine(violationLineFigure1));
	}

	@Test
	public void addViolatedFigure() {
		figureMap.linkViolatedModule(violatedFigure, violationsCollection1);
		assertNotNull(figureMap.getViolatedDTOs(violatedFigure));
		assertTrue(figureMap.isViolatedFigure(violatedFigure));
	}
	
	@Test
	public void maxDependenciesCountUpdated() {
		figureMap.linkDependencies(dependencyLineFigure1, dependenciesCollection1);
		assertEquals(1, figureMap.getMaxDependencies());
		figureMap.linkDependencies(dependencyLineFigure2, dependenciesCollection2);
		assertEquals(4, figureMap.getMaxDependencies());
	}

	@Test
	public void maxViolationsCountUpdated() {
		figureMap.linkViolations(violationLineFigure1, violationsCollection1);
		assertEquals(1, figureMap.getMaxViolations());
		figureMap.linkViolations(violationLineFigure2, violationsCollection2);
		assertEquals(2, figureMap.getMaxViolations());
	}

	@Test
	public void maxAllCountUpdated() {
		figureMap.linkDependencies(dependencyLineFigure1, dependenciesCollection1);
		assertEquals(1, figureMap.getMaxDependencies());
		figureMap.linkViolations(violationLineFigure1, violationsCollection1);
		assertEquals(1, figureMap.getMaxViolations());
		figureMap.linkViolations(violationLineFigure2, violationsCollection2);
		assertEquals(2, figureMap.getMaxViolations());
		figureMap.linkDependencies(dependencyLineFigure2, dependenciesCollection2);
		assertEquals(4, figureMap.getMaxDependencies());
	}

}
