package gm.tools.editor.character.skill;

import gm.tools.editor.character.characteristic.Attribute;
import lombok.Data;

@Data
public class Skill {
	private final String name;
	private final Attribute controllingAttribute;
	private final Difficulty difficulty;
}
