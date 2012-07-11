package husacct.graphics.util.threads;

import husacct.graphics.task.DrawingController;

public class DrawingFiguresThread implements Runnable {
	private DrawingController controller;
	
	public DrawingFiguresThread(DrawingController theController){
		controller = theController;
	}

	@Override
	public void run() {
		try {
			controller.clearDrawing();
			controller.drawDrawingReal();
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
