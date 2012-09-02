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
		ArrayList<String> currentPaths = getCurrentState().getPaths();
		createState(currentPaths);
		getModulesIn(currentPaths);
		drawDrawing();
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
		createState(new ArrayList<String>());
		if (DrawingDetail.WITH_VIOLATIONS == detail) {
			showViolations();
		}
		getModulesIn(new ArrayList<String>());
		drawDrawing();
	}

	@Override
	public void moduleZoom(BaseFigure[] figures) {
		super.notifyServiceListeners();
		ArrayList<BaseFigure> analysedContextFigures = new ArrayList<BaseFigure>();
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
				analysedContextFigures.add(figure);
			} else {
				logger.warn("Could not zoom on this object: " + figure.getName() + ". Not a module to zoom on.");
			}
		}

		ArrayList<String> parentNames = getParentNamesHelper(parentFigures);
		createState(parentNames);
		logger.debug(parentNames);
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
		openPreviousAsCurrentState();
	}

	private void getModulesIn(ArrayList<String> parentNames) {
		if ((parentNames.size() == 0 || (parentNames.size() == 1 && parentNames.get(0).isEmpty())) && !getCurrentState().hasContext()) {
			AbstractDTO[] children = analyseService.getRootModules();
			addChildFigures("", children);
		} else {
			for (String parentName : parentNames) {
				AbstractDTO[] children = analyseService.getChildModulesInModule(parentName);
				addChildFigures(parentName, children);
			}
		}
	}

	private void addChildFigures(String parentName, AbstractDTO[] children) {
		if (null != children) {
			for (AbstractDTO child : children) {
				addFigure(parentName, child);
			}
		} else {
			logger.warn("Tried to draw modules for \"" + parentName + "\", but it has no children.");
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
	protected ViolationDTO[] getViolationsBetween(AbstractDTO dtoFrom, AbstractDTO dtoTo) {
		try {
			return validateService.getViolationsByPhysicalPath(((AnalysedModuleDTO)dtoFrom).uniqueName, ((AnalysedModuleDTO)dtoTo).uniqueName);
		} catch (Exception e) {
			logger.error("Could not fetch violations between two modules.", e);
		}
		return new ViolationDTO[] {};
	}
}
