package gm.tools.editor.gui;

import gm.tools.editor.character.skill.Skill;

public class SkillAndLevel {
	private final Skill skill;
	public int relativeLevel;
	public int absoluteLevel;

	public SkillAndLevel(Skill skill, int relativeLevel, int absoluteLevel) {
		this.skill = skill;
		this.relativeLevel = relativeLevel;
		this.absoluteLevel = absoluteLevel;
	}

	public Skill getSkill() {
		return skill;
	}

	public String getName() {
		return skill.getName();
	}

	public int getRelativeLevel() {
		return relativeLevel;
	}

	public int getAbsoluteLevel() {
		return absoluteLevel;
	}
}
