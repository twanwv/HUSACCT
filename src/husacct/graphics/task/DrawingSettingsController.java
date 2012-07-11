package husacct.graphics.task;

import husacct.ServiceProvider;
import husacct.graphics.util.DrawingDetail;
import husacct.graphics.util.UserInputListener;

public abstract class DrawingSettingsController implements UserInputListener {
	protected boolean areSmartLinesOn;
	protected boolean areDependenciesShown;
	protected boolean areViolationsShown;
	
	@Deprecated
	protected String[] currentPaths = new String[] {};
	
	public DrawingSettingsController(){
	}
	
	public void loadDefaultSettings(){
		showDependencies();
		hideViolations();
		hideSmartLines();
	}
	
	protected DrawingDetail getCurrentDrawingDetail() {
		DrawingDetail detail = DrawingDetail.WITHOUT_VIOLATIONS;
		if (areViolationsShown()) {
			detail = DrawingDetail.WITH_VIOLATIONS;
		}
		return detail;
	}
	
	public void notifyServiceListeners() {
		ServiceProvider.getInstance().getGraphicsService().notifyServiceListeners();
	}
	
	public boolean areDependenciesShown(){
		return areDependenciesShown;
	}
	
	public void showDependencies(){
		areDependenciesShown = true;
	}

	public void hideDependencies() {
		areDependenciesShown = false;
	}
	
	public boolean areViolationsShown() {
		return areViolationsShown;
	}

	public void showViolations() {
		areViolationsShown = true;
	}
	
	public void hideViolations() {
		areViolationsShown = false;
	}

	public boolean areSmartLinesOn(){
		return areSmartLinesOn;
	}
	
	public void hideSmartLines() {
		areSmartLinesOn = false;
	}

	public void showSmartLines() {
		areSmartLinesOn = true;
	}
	
	@Deprecated
	public String[] getCurrentPaths() {
		return currentPaths;
	}

	@Deprecated
	public String getCurrentPathsToString() {
		String stringPaths = "";
		for (String path : getCurrentPaths()) {
			stringPaths += path + " + ";
		}
		return stringPaths;
	}

	@Deprecated
	public void resetCurrentPaths() {
		currentPaths = new String[] {};
	}

	@Deprecated
	public void setCurrentPaths(String[] paths) {
		currentPaths = paths;
	}

}
