package gm.tools.editor;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.CostCalculator;
import gm.tools.editor.character.characteristic.*;
import gm.tools.editor.character.damage.Damage;
import gm.tools.editor.character.skill.Difficulty;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.skill.SkillCalculator;
import gm.tools.editor.character.skill.SkillManager;
import gm.tools.editor.gui.SkillAndLevel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CharacterEditor extends Application {
	// gui

	private GridPane grid;
	private TextField nameTextField;
	private Map<Enum, Spinner<Integer>> characteristicSpinnerMap = new HashMap<>();
	private Map<Enum, Label> characteristicValueLabelMap = new HashMap<>();
	private Label characterPointsValueLabel;
	private TableView<SkillAndLevel> skillTable = new TableView<>();
	private Map<Skill, SkillAndLevel> skillAndLevels = new HashMap<>();

	// calculators

	private SkillManager skillManager = new SkillManager();

	private CostCalculator costCalculator = new CostCalculator();

	private AttributeCalculator attributeCalculator = new AttributeCalculator();
	private HitPointsCalculator hitPointsCalculator = new HitPointsCalculator(attributeCalculator);
	private FatiguePointsCalculator fatiguePointsCalculator = new FatiguePointsCalculator(attributeCalculator);

	private BasicLiftCalculator basicLiftCalculator = new BasicLiftCalculator(attributeCalculator);
	private BasicSpeedCalculator basicSpeedCalculator = new BasicSpeedCalculator(attributeCalculator);
	private BasicMoveCalculator basicMoveCalculator = new BasicMoveCalculator(basicSpeedCalculator);
	private DamageCalculator damageCalculator = new DamageCalculator(attributeCalculator);

	private SkillCalculator skillCalculator = new SkillCalculator(attributeCalculator);

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

		// attributes

		createCharacteristicWithValue(Attribute.STRENGTH, "ST", 0, 1);
		createCharacteristicWithValue(Attribute.DEXTERITY, "DX", 0, 2);
		createCharacteristicWithValue(Attribute.INTELLIGENCE, "IQ", 0, 3);
		createCharacteristicWithValue(Attribute.HEALTH, "HT", 0, 4);

		// secondary characteristics

		createCharacteristicWithValue(Characteristic.HIT_POINTS, "HP", 3, 1);
		createCharacteristicWithValue(Attribute.WILL, "Will", 3, 2);
		createCharacteristicWithValue(Attribute.PERCEPTION, "Per", 3, 3);
		createCharacteristicWithValue(Characteristic.FATIGUE_POINTS, "FP", 3, 4);

		createCharacteristicWithValue(Characteristic.BASIC_SPEED, "BS", 6, 2);
		createCharacteristicWithValue(Characteristic.BASIC_MOVE, "BM", 6, 3);
		createCharacteristic(Characteristic.SIZE_MODIFIER, "SM", 3, 0);

		Label basicLiftLabel = new Label("BL");
		grid.add(basicLiftLabel, 6, 1);

		Label basicLiftValueLabel = new Label("0");
		grid.add(basicLiftValueLabel, 7, 1);
		characteristicValueLabelMap.put(Characteristic.BASIC_LIFT, basicLiftValueLabel);

		Label damageLabel = new Label("Damage");
		grid.add(damageLabel, 6, 4);

		Label damageValueLabel = new Label("0");
		grid.add(damageValueLabel, 7, 4);
		characteristicValueLabelMap.put(Characteristic.DAMAGE, damageValueLabel);

		// skills

		skillManager.add(new Skill("Swords", Attribute.DEXTERITY, Difficulty.VERY_HARD));
		skillManager.add(new Skill("Magic", Attribute.INTELLIGENCE, Difficulty.VERY_HARD));
		skillManager.add(new Skill("Shooting", Attribute.PERCEPTION, Difficulty.VERY_HARD));

		TableColumn skillNameCol = new TableColumn("Skill");
		skillNameCol.setMinWidth(100);
		skillNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn relativeSkillLevelCol = new TableColumn("Relative");
		relativeSkillLevelCol.setMinWidth(100);
		relativeSkillLevelCol.setCellValueFactory(new PropertyValueFactory<>("relativeLevel"));

		TableColumn absoluteSkillLevelCol = new TableColumn("Absolute");
		absoluteSkillLevelCol.setMinWidth(100);
		absoluteSkillLevelCol.setCellValueFactory(new PropertyValueFactory<>("absoluteLevel"));

		skillTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		skillTable.getColumns().addAll(skillNameCol, relativeSkillLevelCol, absoluteSkillLevelCol);

		grid.add(skillTable, 0, 5, 5, 4);

		ComboBox<String> skillComboBox = new ComboBox<>();
		skillComboBox.setItems(FXCollections.observableArrayList(skillManager.getSkillNames()));

		grid.add(skillComboBox, 6, 5, 2, 1);

		Spinner<Integer> skillSpinner = new Spinner<>();
		skillSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));

		grid.add(skillSpinner, 6, 6, 2, 1);

		Button addSkillButton = new Button("Add Skill");
		addSkillButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Optional<Skill> skill = skillManager.get(skillComboBox.getValue());

				if (skill.isPresent()) {
					int relativeLevel = skillSpinner.getValue();
					SkillAndLevel skillAndLevel = new SkillAndLevel(skill.get(), relativeLevel, 1);
					skillAndLevels.put(skill.get(), skillAndLevel);
					readData();
				}
			}
		});

		grid.add(addSkillButton, 6, 7, 2, 1);

		Button removeSkillButton = new Button("Remove Skill");
		removeSkillButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Optional<Skill> skill = skillManager.get(skillComboBox.getValue());

				if (skill.isPresent()) {
					skillAndLevels.remove(skill.get());
					readData();
				}
			}
		});

		grid.add(removeSkillButton, 6, 8, 2, 1);

		// character  points

		Label characterPointsLabel = new Label("CP");
		grid.add(characterPointsLabel, 0, 9);

		characterPointsValueLabel = new Label("0");
		grid.add(characterPointsValueLabel, 1, 9);

		//

		primaryStage.setScene(new Scene(grid, 800, 400));
		primaryStage.show();

		readData();
	}

	private void createAttribute(Enum characteristic, String text, int columnIndex, int rowIndex) {
		createCharacteristic(characteristic, text, columnIndex, rowIndex, 1, 20, 10);
	}

	private void createCharacteristic(Enum characteristic, String text, int columnIndex, int rowIndex) {
		createCharacteristic(characteristic, text, columnIndex, rowIndex, -10, +10, 0);
	}

	private void createCharacteristic(Enum characteristic, String text, int columnIndex, int rowIndex, int min, int max, int defaultValue) {
		Label label = new Label(text);
		grid.add(label, columnIndex, rowIndex);

		characteristicSpinnerMap.put(characteristic, createSpinner(columnIndex + 1, rowIndex, min, max, defaultValue));
	}

	private void createCharacteristicWithValue(Enum characteristic, String text, int columnIndex, int rowIndex) {
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
		int strength = characteristicSpinnerMap.get(Attribute.STRENGTH).getValue();
		int dexterity = characteristicSpinnerMap.get(Attribute.DEXTERITY).getValue();
		int intelligence = characteristicSpinnerMap.get(Attribute.INTELLIGENCE).getValue();
		int health = characteristicSpinnerMap.get(Attribute.HEALTH).getValue();

		int hitPoints = characteristicSpinnerMap.get(Characteristic.HIT_POINTS).getValue();
		int will = characteristicSpinnerMap.get(Attribute.WILL).getValue();
		int perception = characteristicSpinnerMap.get(Attribute.PERCEPTION).getValue();
		int fatiguePoints = characteristicSpinnerMap.get(Characteristic.FATIGUE_POINTS).getValue();

		int basicSpeed = characteristicSpinnerMap.get(Characteristic.BASIC_SPEED).getValue();
		int basicMove = characteristicSpinnerMap.get(Characteristic.BASIC_MOVE).getValue();
		int sizeModifier = characteristicSpinnerMap.get(Characteristic.SIZE_MODIFIER).getValue();

		CharacterTemplateBuilder builder = new CharacterTemplateBuilder(nameTextField.getText());
		builder.setAttributes(strength, dexterity, intelligence, health);
		builder.setHitPointsModifier(hitPoints);
		builder.setWillModifier(will);
		builder.setPerceptionModifier(perception);
		builder.setFatiguePointsModifier(fatiguePoints);
		builder.setBasicSpeedModifier(basicSpeed);
		builder.setBasicMoveModifier(basicMove);
		builder.setSizeModifier(sizeModifier);

		for (SkillAndLevel skillAndLevel : skillAndLevels.values()) {
			builder.addSkill(skillAndLevel.getSkill(), skillAndLevel.relativeLevel);
		}

		CharacterTemplate template = builder.createCharacterTemplate();

		characterPointsValueLabel.setText(Integer.toString(costCalculator.calculate(template)));

		characteristicValueLabelMap.get(Attribute.STRENGTH).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.STRENGTH)));
		characteristicValueLabelMap.get(Attribute.DEXTERITY).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.DEXTERITY)));
		characteristicValueLabelMap.get(Attribute.INTELLIGENCE).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.INTELLIGENCE)));
		characteristicValueLabelMap.get(Attribute.HEALTH).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.HEALTH)));

		characteristicValueLabelMap.get(Characteristic.HIT_POINTS).setText(Integer.toString(hitPointsCalculator.calculate(template)));
		characteristicValueLabelMap.get(Attribute.WILL).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.WILL)));
		characteristicValueLabelMap.get(Attribute.PERCEPTION).setText(Integer.toString(attributeCalculator.calculate(template, Attribute.PERCEPTION)));
		characteristicValueLabelMap.get(Characteristic.FATIGUE_POINTS).setText(Integer.toString(fatiguePointsCalculator.calculate(template)));

		characteristicValueLabelMap.get(Characteristic.BASIC_LIFT).setText(String.format("%d kg", basicLiftCalculator.calculate(template)));
		characteristicValueLabelMap.get(Characteristic.BASIC_SPEED).setText(String.format("%.2f", basicSpeedCalculator.calculate(template)));
		characteristicValueLabelMap.get(Characteristic.BASIC_MOVE).setText(String.format("%d m/s", basicMoveCalculator.calculate(template)));

		// skills

		for (SkillAndLevel skillAndLevel : skillAndLevels.values()) {
			skillAndLevel.absoluteLevel = skillCalculator.calculateLevel(template, skillAndLevel.getSkill());
		}

		skillTable.getItems().clear();
		skillTable.getItems().addAll(FXCollections.observableArrayList(skillAndLevels.values()));

		// damage

		Damage thrustDamage = damageCalculator.calculateThrustDamage(template);
		Damage swingDamage = damageCalculator.calculateSwingDamage(template);

		characteristicValueLabelMap.get(Characteristic.DAMAGE).setText(String.format("%s/%s", thrustDamage.toString(), swingDamage.toString()));
	}


	public static void main(String[] args) {
		launch(args);
	}
}
