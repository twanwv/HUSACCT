package husacct.graphics.task;

import husacct.common.dto.AbstractDTO;

public class DrawingTask implements Runnable {
	
	private DrawingController controller;
	private AbstractDTO[] toDrawModules;
	
	public DrawingTask(DrawingController theController, AbstractDTO[] modules){
		controller = theController;
		toDrawModules = modules;
	}

	@Override
	public void run() {
		try {
			controller.actuallyDraw(toDrawModules);
			Thread.sleep(10);
			controller.refreshFrameClean();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
