package Core;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class CController {
	public static Stage primaryStage = CApplication.stage;

	public Scene prevScene = null;
	public Scene scene = null;

	/*
	 * ����� ������������� ���������� �����, ����� � ������ ����� ���� ���������
	 * � ����� ������
	 */
	public void setPrevScene(Stage prevScene) {
		this.prevScene = prevScene.getScene();
	}

	public abstract void run();
}
