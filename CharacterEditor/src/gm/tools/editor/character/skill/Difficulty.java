package gm.tools.editor.character.skill;

public enum Difficulty {
	EASY(-1),
	AVERAGE(-2),
	HARD(-3),
	VERY_HARD(-4);

	private final int startLevel;

	Difficulty(int startLevel) {
		this.startLevel = startLevel;
	}

	public int getStartLevel() {
		return startLevel;
	}
}
