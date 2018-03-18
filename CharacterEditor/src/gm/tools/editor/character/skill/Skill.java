package gm.tools.editor.character.skill;

import gm.tools.editor.character.characteristic.Characteristic;

public class Skill {
	private final String name;
	private final Characteristic controllingAttribute;
	private final Difficulty difficulty;

	public Skill(String name, Characteristic controllingAttribute, Difficulty difficulty) {
		this.name = name;
		this.controllingAttribute = controllingAttribute;

		if (!controllingAttribute.isAttribute()) {
			throw new IllegalArgumentException(String.format("Controlling attribute %s is not an attribute!", controllingAttribute));
		}

		this.difficulty = difficulty;
	}

	public String getName() {
		return name;
	}

	public Characteristic getControllingAttribute() {
		return controllingAttribute;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}
}
