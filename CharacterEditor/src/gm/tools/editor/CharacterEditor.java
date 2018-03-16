package gm.tools.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CharacterEditor extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
		primaryStage.setTitle("Character Editor");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label stLabel = new Label("ST");
		grid.add(stLabel, 0, 0);

		Label dxLabel = new Label("DX");
		grid.add(dxLabel, 0, 1);

		Label iqLabel = new Label("IQ");
		grid.add(iqLabel, 0, 2);

		Label htLabel = new Label("HT");
		grid.add(htLabel, 0, 3);

		TextField stTextField = new TextField();
		stTextField.setPrefColumnCount(2);
		grid.add(stTextField, 1, 0);

		TextField dxTextField = new TextField();
		dxTextField.setPrefColumnCount(2);
		grid.add(dxTextField, 1, 1);

		TextField iqTextField = new TextField();
		iqTextField.setPrefColumnCount(2);
		grid.add(iqTextField, 1, 2);

		TextField htTextField = new TextField();
		htTextField.setPrefColumnCount(2);
		grid.add(htTextField, 1, 3);

		primaryStage.setScene(new Scene(grid, 300, 275));
		primaryStage.show();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
