package gm.tools.editor;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.CharacterTemplateJson;
import gm.tools.editor.character.CostCalculator;
import gm.tools.editor.character.characteristic.*;
import gm.tools.editor.character.damage.Damage;
import gm.tools.editor.character.skill.*;
import gm.tools.editor.character.trait.StringTrait;
import gm.tools.editor.character.trait.Trait;
import gm.tools.editor.gui.SkillAndLevel;
import gm.tools.editor.gui.TraitTable;
import gm.tools.editor.gui.TraitTableEntry;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CharacterEditor extends Application {
	// gui

	private BorderPane borderPane;
	private GridPane grid;
	private TextField nameTextField;
	private Map<Enum, Spinner<Integer>> characteristicSpinnerMap = new HashMap<>();
	private Map<Enum, Label> characteristicValueLabelMap = new HashMap<>();
	private Label characterPointsValueLabel;
	private TableView<SkillAndLevel> skillTable = new TableView<>();
	private Map<Skill, SkillAndLevel> skillAndLevels = new HashMap<>();
	private TraitTable traitTable = new TraitTable();
	private FileChooser fileChooser = new FileChooser();

	// calculators

	private static final String SKILL_FILE = "data/skills.json";
	private SkillManagerWithJson skillManager = new SkillManagerWithJson();

	private CostCalculator costCalculator = new CostCalculator();

	private AttributeCalculator attributeCalculator = new AttributeCalculator();
	private HitPointsCalculator hitPointsCalculator = new HitPointsCalculator(attributeCalculator);
	private FatiguePointsCalculator fatiguePointsCalculator = new FatiguePointsCalculator(attributeCalculator);

	private BasicLiftCalculator basicLiftCalculator = new BasicLiftCalculator(attributeCalculator);
	private BasicSpeedCalculator basicSpeedCalculator = new BasicSpeedCalculator(attributeCalculator);
	private BasicMoveCalculator basicMoveCalculator = new BasicMoveCalculator(basicSpeedCalculator);
	private DamageCalculator damageCalculator = new DamageCalculator(attributeCalculator);

	private SkillCalculator skillCalculator = new SkillCalculator(attributeCalculator);

	private CharacterTemplateJson characterTemplateJson = new CharacterTemplateJson(skillManager);

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
		primaryStage.setTitle("Character Editor");

		borderPane = new BorderPane();

		createMenuBar(primaryStage);

		// character

		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		borderPane.setCenter(grid);

		// character

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

		skillManager.load(SKILL_FILE);

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
		skillComboBox.setItems(FXCollections.observableArrayList(skillManager.getSortedSkillNames()));

		grid.add(skillComboBox, 6, 5, 2, 1);

		Spinner<Integer> skillSpinner = new Spinner<>();
		skillSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));

		grid.add(skillSpinner, 6, 6, 2, 1);

		Button addSkillButton = new Button("Add Skill");
		addSkillButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Skill skill = skillManager.get(skillComboBox.getValue());

				int relativeLevel = skillSpinner.getValue();
				SkillAndLevel skillAndLevel = new SkillAndLevel(skill, relativeLevel, 1);
				skillAndLevels.put(skill, skillAndLevel);
				readData();
			}
		});

		grid.add(addSkillButton, 6, 7, 2, 1);

		Button removeSkillButton = new Button("Remove Skill");
		removeSkillButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Skill skill = skillManager.get(skillComboBox.getValue());
				skillAndLevels.remove(skill);
				readData();
			}
		});

		grid.add(removeSkillButton, 6, 8, 2, 1);

		// traits

		grid.add(traitTable.getTraitTable(), 0, 9, 5, 1);

		// character  points

		Label characterPointsLabel = new Label("CP");
		grid.add(characterPointsLabel, 0, 10);

		characterPointsValueLabel = new Label("0");
		grid.add(characterPointsValueLabel, 1, 10);

		//

		primaryStage.setScene(new Scene(borderPane, 1000, 600));
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

	private void createMenuBar(Stage stage) {
		MenuBar menuBar = new MenuBar();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		// File

		Menu menuFile = new Menu("File");
		menuBar.getMenus().addAll(menuFile);

		// File -> Save

		MenuItem saveTemplateItem = new MenuItem("Save", new ImageView(new Image("/icons/save.png")));
		saveTemplateItem.setOnAction(t -> saveTemplateToFile(stage));
		menuFile.getItems().addAll(saveTemplateItem);

		// File -> Load

		MenuItem loadTemplateItem = new MenuItem("Load", new ImageView(new Image("/icons/load.png")));
		loadTemplateItem.setOnAction(t -> loadTemplateFromFile(stage));
		menuFile.getItems().addAll(loadTemplateItem);


		borderPane.setTop(menuBar);
	}

	private void saveTemplateToFile(Stage stage) {
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			try {
				FileUtils.writeStringToFile(file, characterTemplateJson.saveToJson(createTemplateFromGui()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadTemplateFromFile(Stage stage) {
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			try {
				CharacterTemplate template = characterTemplateJson.loadFromJson(FileUtils.readFileToString(file));
				setGuiToTemplate(template);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CharacterTemplate createTemplateFromGui() {
		CharacterTemplateBuilder builder = new CharacterTemplateBuilder(nameTextField.getText());

		for (Attribute attribute : Attribute.values()) {
			builder.setAttribute(attribute, characteristicSpinnerMap.get(attribute).getValue());
		}

		for (Characteristic characteristic : Characteristic.MODIFIERS) {
			builder.setCharacteristicModifier(characteristic, characteristicSpinnerMap.get(characteristic).getValue());
		}

		for (SkillAndLevel skillAndLevel : skillAndLevels.values()) {
			builder.addSkill(skillAndLevel.getSkill(), skillAndLevel.relativeLevel);
		}

		for (TraitTableEntry entry : traitTable.getTraitTableEntries()) {
			builder.addTrait(new StringTrait(entry.getName(), entry.getCost()));
		}

		return builder.createCharacterTemplate();
	}

	private void setGuiToTemplate(CharacterTemplate template) {
		nameTextField.setText(template.getName());

		for (Attribute attribute : Attribute.values()) {
			characteristicSpinnerMap.get(attribute).getValueFactory().setValue(template.getAttributeModifier(attribute));
		}

		for (Characteristic characteristic : Characteristic.MODIFIERS) {
			characteristicSpinnerMap.get(characteristic).getValueFactory().setValue(template.getCharacteristicModifier(characteristic));
		}

		for (Skill skill : template.getSkills()) {
			SkillAndLevel skillAndLevel = new SkillAndLevel(skill, template.getRelativeSkillLevel(skill), 1);
			skillAndLevels.put(skill, skillAndLevel);
		}

		traitTable.clear();

		for (Trait trait : template.getTraits()) {
			traitTable.add(trait.getName(), trait.getCost());
		}

		readData();
	}

	private void readData() {
		readData(createTemplateFromGui());
	}

	private void readData(CharacterTemplate template) {
		characterPointsValueLabel.setText(Integer.toString(costCalculator.calculate(template)));

		for (Attribute attribute : Attribute.values()) {
			characteristicValueLabelMap.get(attribute).setText(Integer.toString(attributeCalculator.calculate(template, attribute)));
		}

		characteristicValueLabelMap.get(Characteristic.HIT_POINTS).setText(Integer.toString(hitPointsCalculator.calculate(template)));
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
