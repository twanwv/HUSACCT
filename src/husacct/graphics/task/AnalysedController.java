package husacct.graphics.task;

import husacct.ServiceProvider;
import husacct.analyse.IAnalyseService;
import husacct.common.dto.AbstractDTO;
import husacct.common.dto.AnalysedModuleDTO;
import husacct.common.dto.DependencyDTO;
import husacct.common.dto.ViolationDTO;
import husacct.common.services.IServiceListener;
import husacct.graphics.presentation.figures.BaseFigure;
import husacct.graphics.util.DrawingDetail;
import husacct.validate.IValidateService;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class AnalysedController extends DrawingController {
	private Logger logger = Logger.getLogger(AnalysedController.class);
	protected IAnalyseService analyseService;
	protected IValidateService validateService;

	public AnalysedController() {
		super();
		initializeServices();
	}

	private void initializeServices() {
		analyseService = ServiceProvider.getInstance().getAnalyseService();
		analyseService.addServiceListener(new IServiceListener() {
			@Override
			public void update() {
				refreshDrawing();
			}
		});
		validateService = ServiceProvider.getInstance().getValidateService();
		validateService.addServiceListener(new IServiceListener() {
			@Override
			public void update() {
				if (areViolationsShown()) {
					refreshDrawing();
				}
			}
		});
	}

	@Override
	public void refreshDrawing() {
		super.notifyServiceListeners();
		// getAndDrawModulesIn(getCurrentPaths());
	}

	@Override
	public void showViolations() {
		if (validateService.isValidated()) {
			super.showViolations();
		}
	}

	@Override
	public void drawArchitecture(DrawingDetail detail) {
		super.drawArchitecture(getCurrentDrawingDetail());
		super.notifyServiceListeners();
		// TODO use drawIn()
		AbstractDTO[] modules = analyseService.getRootModules();
		createState("");
		for (AbstractDTO dto : modules) {
			addFigure("", dto);
		}
		if (DrawingDetail.WITH_VIOLATIONS == detail) {
			showViolations();
		}
		drawDrawing();
	}

	@Override
	protected DependencyDTO[] getDependenciesBetween(AbstractDTO dtoFrom, AbstractDTO dtoTo) {
		try {
			return analyseService.getDependencies(((AnalysedModuleDTO) dtoFrom).uniqueName, ((AnalysedModuleDTO) dtoTo).uniqueName);
		} catch (Exception e) {
			logger.error("Could not fetch dependency between two modules.", e);
		}
		return new DependencyDTO[] {};
	}

	@Override
	protected ViolationDTO[] getViolationsBetween(BaseFigure figureFrom, BaseFigure figureTo) {
		AnalysedModuleDTO dtoFrom = (AnalysedModuleDTO) getFigureMap().getModuleDTO(figureFrom);
		AnalysedModuleDTO dtoTo = (AnalysedModuleDTO) getFigureMap().getModuleDTO(figureTo);
		return validateService.getViolationsByPhysicalPath(dtoFrom.uniqueName, dtoTo.uniqueName);
	}

	@Override
	public void moduleZoom(BaseFigure[] figures) {
		ArrayList<BaseFigure> analysedContextFigures = new ArrayList<BaseFigure>();
		super.notifyServiceListeners();
		ArrayList<BaseFigure> parentFigures = new ArrayList<BaseFigure>();
		for (BaseFigure figure : figures) {
			if (figure.isModule()) {
				try {
					parentFigures.add(figure);
				} catch (Exception e) {
					e.printStackTrace();
					logger.warn("Could not zoom on this object: " + figure.getName() + ". Expected a different DTO type.");
				}
			} else if (!figure.isLine()) {
				logger.warn("Could not zoom on this object: " + figure.getName() + ". Not a module to zoom on. Figure is accepted as context for multizoom.");
			} else {
				logger.warn("Could not zoom on this object: " + figure.getName() + ". Not a module to zoom on.");
			}
		}

		ArrayList<String> parentNames = getParentNamesHelper(parentFigures);
		String combinedPath = createCombinedPathHelper(parentNames);
		createState(combinedPath);
		for (BaseFigure figure : analysedContextFigures) {
			addContextFigure(figure);
		}

		if (parentNames.size() > 0) {
			getModulesIn(parentNames);
		}
		drawDrawing();
	}

	@Override
	public void moduleZoomOut() {
		super.notifyServiceListeners();
		ArrayList<String> currentPaths = getCurrentState().getPaths();
		if (currentPaths.size() > 0) {
			ArrayList<String> parentNames = new ArrayList<String>();
			for (String path : currentPaths) {
				AnalysedModuleDTO parentDTO = analyseService.getParentModuleForModule(path);
				parentNames.add(parentDTO.uniqueName);
			}
			String combinedPath = createCombinedPathHelper(parentNames);
			createState(combinedPath);

			if (parentNames.size() > 0) {
				getModulesIn(parentNames);
			}
			drawDrawing();
		} else {
			logger.warn("Tried to zoom out from \"" + getCurrentState().getFullPath() + "\", but it has no parent (could be root if it's an empty string).");
			logger.debug("Reverting to the root of the application.");
			drawArchitecture(getCurrentDrawingDetail());
		}
	}

	private void getModulesIn(ArrayList<String> parentNames) {
		if (parentNames.size() == 0) {
			drawArchitecture(getCurrentDrawingDetail());
		} else {
			for (String parentName : parentNames) {
				AbstractDTO[] children = analyseService.getChildModulesInModule(parentName);
				if (parentName.equals("")) {
					drawArchitecture(getCurrentDrawingDetail());
					continue;
				} else if (children.length > 0) {
					for (AbstractDTO child : children) {
						addFigure(parentName, child);
					}
				} else {
					logger.warn("Tried to draw modules for \"" + parentName + "\", but it has no children.");
				}
			}
		}
	}

	@Override
	public void moduleOpen(String[] paths) {
		super.notifyServiceListeners();
		if (paths.length == 0) {
			drawArchitecture(getCurrentDrawingDetail());
		} else {
			getModulesIn((ArrayList<String>) Arrays.asList(paths));
		}
	}
}
