package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import gm.tools.editor.character.damage.Damage;

public class DamageCalculator {

	public Damage calculateThrustDamage(int strength) {
		int dice = 0;
		int modifier = 0;

		if (strength < 11) {
			dice = 1;
			modifier = (int) Math.ceil(strength / 2.0) - 7;
		} else {
			int value = (strength - 11) / 2;
			dice = (value / 4) + 1;
			modifier = (value % 4) - 1;
		}

		return new Damage(dice, modifier);
	}

	public Damage calculateThrustDamage(Character character) {
		return calculateThrustDamage(character.getStrength());
	}

	public Damage calculateSwingDamage(int strength) {
		int dice = 0;
		int modifier = 0;

		if (strength < 9) {
			dice = 1;
			modifier = (int) Math.ceil(strength / 2.0) - 6;
		} else {
			int value = strength - 9;
			dice = (value / 4) + 1;
			modifier = (value % 4) - 1;
		}

		return new Damage(dice, modifier);
	}

	public Damage calculateSwingDamage(Character character) {
		return calculateSwingDamage(character.getStrength());
	}
}
