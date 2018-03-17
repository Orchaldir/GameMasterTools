package gm.tools.editor;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CostCalculator;
import gm.tools.editor.character.characteristcs.BasicLiftCalculator;
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
	private Spinner<Integer> strengthSpinner, intelligenceSpinner, dexteritySpinner, healthSpinner, hitPointsSpinner;
	private Label characterPointsValueLabel, basicLiftValueLabel, hitPointsValueLabel;

	private CostCalculator costCalculator = new CostCalculator();
	private BasicLiftCalculator basicLiftCalculator = new BasicLiftCalculator();

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

		Label characterPointsLabel = new Label("CP");
		grid.add(characterPointsLabel, 0, 5);

		characterPointsValueLabel = new Label("0");
		grid.add(characterPointsValueLabel, 1, 5);

		// attributes

		Label stLabel = new Label("ST");
		grid.add(stLabel, 0, 1);

		Label dxLabel = new Label("DX");
		grid.add(dxLabel, 0, 2);

		Label iqLabel = new Label("IQ");
		grid.add(iqLabel, 0, 3);

		Label htLabel = new Label("HT");
		grid.add(htLabel, 0, 4);

		strengthSpinner = createAttributeSpinner(1, 1);
		dexteritySpinner = createAttributeSpinner(1, 2);
		intelligenceSpinner = createAttributeSpinner(1, 3);
		healthSpinner = createAttributeSpinner(1, 4);

		// secondary characteristics

		Label hitPointsLabel = new Label("HP");
		grid.add(hitPointsLabel, 2, 1);

		hitPointsSpinner = createSpinner(3, 1, -10, +10, 0);

		hitPointsValueLabel = new Label("0");
		grid.add(hitPointsValueLabel, 4, 1);

		Label basicLiftLabel = new Label("BL");
		grid.add(basicLiftLabel, 5, 1);

		basicLiftValueLabel = new Label("0");
		grid.add(basicLiftValueLabel, 6, 1);

		//

		primaryStage.setScene(new Scene(grid, 600, 275));
		primaryStage.show();

		readData();
	}

	private Spinner<Integer> createAttributeSpinner(int columnIndex, int rowIndex) {
		return createSpinner(columnIndex, rowIndex, 1, 20, 10);
	}

	private Spinner<Integer> createSpinner(int columnIndex, int rowIndex, int min, int max, int defaultValue) {
		Spinner<Integer> spinner = new Spinner<>();
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, defaultValue));

		spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			readData();
		});

		grid.add(spinner, columnIndex, rowIndex);

		return spinner;
	}

	private void readData() {
		int strength = strengthSpinner.getValue();

		CharacterTemplate template = new CharacterTemplate(nameTextField.getText(), strengthSpinner.getValue(), dexteritySpinner.getValue(), intelligenceSpinner.getValue(), healthSpinner.getValue(), 0);

		characterPointsValueLabel.setText(Integer.toString(costCalculator.calculate(template)));

		basicLiftValueLabel.setText(Integer.toString(basicLiftCalculator.calculate(template)));
	}


	public static void main(String[] args) {
		launch(args);
	}
}
