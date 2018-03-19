package gm.tools.editor.gui;

public class SkillAndLevel {
	public final String name;
	public final int relativeLevel;
	public final int absoluteLevel;

	public SkillAndLevel(String name, int relativeLevel, int absoluteLevel) {
		this.name = name;
		this.relativeLevel = relativeLevel;
		this.absoluteLevel = absoluteLevel;
	}

	public String getName() {
		return name;
	}

	public int getRelativeLevel() {
		return relativeLevel;
	}

	public int getAbsoluteLevel() {
		return absoluteLevel;
	}
}
