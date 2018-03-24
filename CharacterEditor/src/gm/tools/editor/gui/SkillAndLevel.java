package gm.tools.editor.gui;

import gm.tools.editor.character.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class SkillAndLevel {
	private final Skill skill;
	public final int relativeLevel;
	public final int absoluteLevel;
}
