package gm.tools.editor.character.attack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Damage {
	@Getter
	private final int dice;

	@Getter
	private final int modifier;

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
