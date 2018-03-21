package gm.tools.editor.character.damage;

public class Damage {
	private final int dice;
	private final int modifier;

	public Damage(int dice, int modifier) {
		this.dice = dice;
		this.modifier = modifier;
	}

	public int getDice() {
		return dice;
	}

	public int getModifier() {
		return modifier;
	}
}
