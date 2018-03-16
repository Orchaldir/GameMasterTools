package gm.tools.editor;

import gm.tools.editor.character.CharacterTemplate;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CharacterEditor extends Application {

	private GridPane grid;
	private TextField nameTextField;
	private Spinner<Integer> strengthSpinner, intelligenceSpinner, dexteritySpinner, healthSpinner;
	private Label characterPointsValueLabel;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
		primaryStage.setTitle("Character Editor");

		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		nameTextField = new TextField("Character");
		grid.add(nameTextField, 0, 0, 2, 1);

		Label stLabel = new Label("ST");
		grid.add(stLabel, 0, 1);

		Label dxLabel = new Label("DX");
		grid.add(dxLabel, 0, 2);

		Label iqLabel = new Label("IQ");
		grid.add(iqLabel, 0, 3);

		Label htLabel = new Label("HT");
		grid.add(htLabel, 0, 4);

		Label characterPointsLabel = new Label("CP");
		grid.add(characterPointsLabel, 0, 5);

		characterPointsValueLabel = new Label("0");
		grid.add(characterPointsValueLabel, 1, 5);

		strengthSpinner = createSpinner(1, 1);
		dexteritySpinner = createSpinner(1, 2);
		intelligenceSpinner = createSpinner(1, 3);
		healthSpinner = createSpinner(1, 4);

		primaryStage.setScene(new Scene(grid, 300, 275));
		primaryStage.show();
	}

	private Spinner<Integer> createSpinner(int columnIndex, int rowIndex) {
		Spinner<Integer> spinner = new Spinner<>();
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 10));

		spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			readData();
		});

		grid.add(spinner, columnIndex, rowIndex);

		return spinner;
	}

	private void readData() {
		int strength = strengthSpinner.getValue();

		CharacterTemplate template = new CharacterTemplate(nameTextField.getText(), strengthSpinner.getValue(), dexteritySpinner.getValue(), intelligenceSpinner.getValue(), healthSpinner.getValue());

		characterPointsValueLabel.setText(Integer.toString(template.calculateCharacterPoints()));
	}


	public static void main(String[] args) {
		launch(args);
	}
}
