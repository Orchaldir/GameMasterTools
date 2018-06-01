package gm.tools.editor.character.attack.damage;

import gm.tools.editor.character.Character;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Damage implements IDamage {
	@Getter
	private final int dice;

	@Getter
	private final int modifier;

	@Getter
	private final DamageType type;

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public IDamage resolveFor(Character character) {
		return this;
	}

	@Override
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
