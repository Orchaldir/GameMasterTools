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
	// gui

	private GridPane grid;
	private TextField nameTextField;
	private Map<Attribute, Spinner<Integer>> attributeSpinnerMap = new HashMap<>();
	private Map<SecondaryCharacteristic, Spinner<Integer>> characteristicSpinnerMap = new HashMap<>();
	private Map<SecondaryCharacteristic, Label> characteristicValueLabelMap = new HashMap<>();
	private Label characterPointsValueLabel;

	// calculators

	private CostCalculator costCalculator = new CostCalculator();

	private HitPointsCalculator hitPointsCalculator = new HitPointsCalculator();
	private WillCalculator willCalculator = new WillCalculator();
	private PerceptionCalculator perceptionCalculator = new PerceptionCalculator();
	private FatiguePointsCalculator fatiguePointsCalculator = new FatiguePointsCalculator();

	private BasicLiftCalculator basicLiftCalculator = new BasicLiftCalculator();
	private BasicSpeedCalculator basicSpeedCalculator = new BasicSpeedCalculator();
	private BasicMoveCalculator basicMoveCalculator = new BasicMoveCalculator(basicSpeedCalculator);

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

		createCharacteristicWithValue(SecondaryCharacteristic.HIT_POINTS, "HP", 2, 1);
		createCharacteristicWithValue(SecondaryCharacteristic.WILL, "Will", 2, 2);
		createCharacteristicWithValue(SecondaryCharacteristic.PERCEPTION, "Per", 2, 3);
		createCharacteristicWithValue(SecondaryCharacteristic.FATIGUE_POINTS, "FP", 2, 4);

		createCharacteristicWithValue(SecondaryCharacteristic.BASIC_SPEED, "BS", 5, 2);
		createCharacteristicWithValue(SecondaryCharacteristic.BASIC_MOVE, "BM", 5, 3);
		createCharacteristic(SecondaryCharacteristic.SIZE_MODIFIER, "SM", 2, 0);

		Label basicLiftLabel = new Label("BL");
		grid.add(basicLiftLabel, 5, 1);

		Label basicLiftValueLabel = new Label("0");
		grid.add(basicLiftValueLabel, 6, 1);
		characteristicValueLabelMap.put(SecondaryCharacteristic.BASIC_LIFT, basicLiftValueLabel);

		//

		primaryStage.setScene(new Scene(grid, 800, 275));
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
	}

	private void createCharacteristicWithValue(SecondaryCharacteristic characteristic, String text, int columnIndex, int rowIndex) {
		createCharacteristic(characteristic, text, columnIndex, rowIndex);

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

		int basicSpeed = characteristicSpinnerMap.get(SecondaryCharacteristic.BASIC_SPEED).getValue();
		int basicMove = characteristicSpinnerMap.get(SecondaryCharacteristic.BASIC_MOVE).getValue();
		int sizeModifier = characteristicSpinnerMap.get(SecondaryCharacteristic.SIZE_MODIFIER).getValue();

		CharacterTemplateBuilder builder = new CharacterTemplateBuilder(nameTextField.getText());
		builder.setAttributes(strength, dexterity, intelligence, health);
		builder.setHitPointsModifier(hitPoints);
		builder.setWillModifier(will);
		builder.setPerceptionModifier(perception);
		builder.setFatiguePointsModifier(fatiguePoints);
		builder.setBasicSpeedModifier(basicSpeed);
		builder.setBasicMoveModifier(basicMove);
		builder.setSizeModifier(sizeModifier);
		CharacterTemplate template = builder.createCharacterTemplate();

		characterPointsValueLabel.setText(Integer.toString(costCalculator.calculate(template)));

		characteristicValueLabelMap.get(SecondaryCharacteristic.HIT_POINTS).setText(Integer.toString(hitPointsCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.WILL).setText(Integer.toString(willCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.PERCEPTION).setText(Integer.toString(perceptionCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.FATIGUE_POINTS).setText(Integer.toString(fatiguePointsCalculator.calculate(template)));

		characteristicValueLabelMap.get(SecondaryCharacteristic.BASIC_LIFT).setText(Integer.toString(basicLiftCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.BASIC_SPEED).setText(String.format("%.2f", basicSpeedCalculator.calculate(template)));
		characteristicValueLabelMap.get(SecondaryCharacteristic.BASIC_MOVE).setText(Integer.toString(basicMoveCalculator.calculate(template)));
	}


	public static void main(String[] args) {
		launch(args);
	}
}
