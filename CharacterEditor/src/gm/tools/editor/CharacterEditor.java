package gm.tools.editor;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.CostCalculator;
import gm.tools.editor.character.characteristcs.*;
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

import java.util.HashMap;
import java.util.Map;

public class CharacterEditor extends Application {

	private GridPane grid;
	private TextField nameTextField;
	private Map<Attribute, Spinner<Integer>> attributeSpinnerMap = new HashMap<>();
	private Map<SecondaryCharacteristic, Spinner<Integer>> characteristicSpinnerMap = new HashMap<>();
	private Map<SecondaryCharacteristic, Label> characteristicValueLabelMap = new HashMap<>();

	private Label characterPointsValueLabel, basicLiftValueLabel;

	private CostCalculator costCalculator = new CostCalculator();
	private BasicLiftCalculator basicLiftCalculator = new BasicLiftCalculator();
	private HitPointsCalculator hitPointsCalculator = new HitPointsCalculator();
	private WillCalculator willCalculator = new WillCalculator();
	private PerceptionCalculator perceptionCalculator = new PerceptionCalculator();
	private FatiguePointsCalculator fatiguePointsCalculator = new FatiguePointsCalculator();

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

		createAttribute(Attribute.STRENGTH, "ST", 0, 1);
		createAttribute(Attribute.DEXTERITY, "DX", 0, 2);
		createAttribute(Attribute.INTELLIGENCE, "IQ", 0, 3);
		createAttribute(Attribute.HEALTH, "HT", 0, 4);

		// secondary characteristics

		createCharacteristic(SecondaryCharacteristic.HIT_POINTS, "HP", 2, 1);
		createCharacteristic(SecondaryCharacteristic.WILL, "Will", 2, 2);
		createCharacteristic(SecondaryCharacteristic.PERCEPTION, "Per", 2, 3);
		createCharacteristic(SecondaryCharacteristic.FATIGUE_POINTS, "FP", 2, 4);

		Label basicLiftLabel = new Label("BL");
		grid.add(basicLiftLabel, 5, 1);

		basicLiftValueLabel = new Label("0");
		grid.add(basicLiftValueLabel, 6, 1);

		//

		primaryStage.setScene(new Scene(grid, 600, 275));
		primaryStage.show();

		readData();
	}

	private void createAttribute(Attribute attribute, String text, int columnIndex, int rowIndex) {
		Label label = new Label(text);
		grid.add(label, columnIndex, rowIndex);

		attributeSpinnerMap.put(attribute, createAttributeSpinner(columnIndex + 1, rowIndex));
	}

	private void createCharacteristic(SecondaryCharacteristic characteristic, String text, int columnIndex, int rowIndex) {
		Label label = new Label(text);
		grid.add(label, columnIndex, rowIndex);

		characteristicSpinnerMap.put(characteristic, createSpinner(columnIndex + 1, rowIndex, -10, +10, 0));

		Label valueLabel = new Label("0");
		grid.add(valueLabel, columnIndex + 2, rowIndex);
		characteristicValueLabelMap.put(characteristic, valueLabel);
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
		int strength = attributeSpinnerMap.get(Attribute.STRENGTH).getValue();
		int dexterity = attributeSpinnerMap.get(Attribute.DEXTERITY).getValue();
		int intelligence = attributeSpinnerMap.get(Attribute.INTELLIGENCE).getValue();
		int health = attributeSpinnerMap.get(Attribute.HEALTH).getValue();

		int hitPoints = characteristicSpinnerMap.get(SecondaryCharacteristic.HIT_POINTS).getValue();
		int will = characteristicSpinnerMap.get(SecondaryCharacteristic.WILL).getValue();
		int perception = characteristicSpinnerMap.get(SecondaryCharacteristic.PERCEPTION).getValue();
		int fatiguePoints = characteristicSpinnerMap.get(SecondaryCharacteristic.FATIGUE_POINTS).getValue();

		CharacterTemplateBuilder builder = new CharacterTemplateBuilder(nameTextField.getText());
		builder.setAttributes(strength, dexterity, intelligence, health);
		builder.setHitPointsModifier(hitPoints);
		builder.setWillModifier(will);
		builder.setPerceptionModifier(perception);
		builder.setFatiguePointsModifier(fatiguePoints);
		CharacterTemplate template = builder.createCharacterTemplate();

		characterPointsValueLabel.setText(Integer.toString(costCalculator.calculate(template)));

		characteristicValueLabelMap.get(SecondaryCharacteristic.HIT_POINTS).setText(Integer.toString(hitPointsCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.WILL).setText(Integer.toString(willCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.PERCEPTION).setText(Integer.toString(perceptionCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.FATIGUE_POINTS).setText(Integer.toString(fatiguePointsCalculator.calculate(template)));

		basicLiftValueLabel.setText(Integer.toString(basicLiftCalculator.calculate(template)));
	}


	public static void main(String[] args) {
		launch(args);
	}
}
