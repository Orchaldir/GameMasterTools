package gm.tools.editor.character.damage;

import lombok.Getter;

public class Damage {
	@Getter
	private final int dice;

	@Getter
	private final int modifier;

	public Damage(int dice, int modifier) {
		this.dice = dice;
		this.modifier = modifier;
	}

	public String toString() {
		String string = "";

		if (dice != 0) {
			string += String.format("%dd", dice);
		}

		if (modifier != 0) {
			string += String.format("%+d", modifier);
		}

		return string;
	}
}
