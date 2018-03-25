package gm.tools.editor.gui;

import gm.tools.editor.character.skill.SkillManager;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import lombok.Getter;


public class DataTree {
	// gui

	private final static String SETTING = "Setting";

	private final Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/world.png")));
	private final Node groupIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/group.png")));
	private final Node folderIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/folder.png")));

	private final TreeItem<String> rootItem = new TreeItem<String>("Setting", rootIcon);
	private final TreeItem<String> skillsItem = new TreeItem<String>("Skills", folderIcon);
	private final TreeItem<String> templatesItem = new TreeItem<String>("Templates", groupIcon);

	@Getter
	private final TreeView<String> treeView;

	// data

	private final SkillManager skillManager;

	public DataTree(SkillManager skillManager) {
		this.skillManager = skillManager;

		rootItem.getChildren().add(skillsItem);
		rootItem.getChildren().add(templatesItem);

		rootItem.setExpanded(true);
		skillsItem.setExpanded(true);
		templatesItem.setExpanded(true);

		treeView = new TreeView<>(rootItem);
		treeView.setEditable(true);
		treeView.setShowRoot(false);
	}

	public void update() {
		updateSkills();
	}

	public void updateSkills() {
		skillsItem.getChildren().clear();

		for (String skill : skillManager.getSkillNames()) {
			TreeItem<String> skillItem = new TreeItem<>(skill);
			skillsItem.getChildren().add(skillItem);
		}
	}
}
