package gm.tools.editor.gui;

import gm.tools.editor.character.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SkillAndLevel {
	private final Skill skill;
	public int relativeLevel;
	public int absoluteLevel;

	public String getName() {
		return skill.getName();
	}

	public String getAttributeText() {
		return skill.getControllingAttribute().getText();
	}
}
