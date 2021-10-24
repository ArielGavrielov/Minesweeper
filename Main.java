package mines;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Minesweeper.fxml"));
		Parent root = loader.load();

		//((VBox) root).setFillWidth(true);
		MyController controller = loader.getController();
		controller.setStage(primaryStage);
		controller.startGame();

		Scene s = new Scene(root);
		s.getStylesheets().add(getClass().getResource("minesweeper.css").toExternalForm());
		primaryStage.setScene(s);
		primaryStage.sizeToScene();
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
	}

	public static void main(String[] args) {
		launch(args);
	}
}