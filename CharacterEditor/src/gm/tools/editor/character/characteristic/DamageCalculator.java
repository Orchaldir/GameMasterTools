package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import gm.tools.editor.character.attack.damage.Damage;
import gm.tools.editor.character.attack.damage.DamageType;
import gm.tools.editor.character.attack.damage.IDamage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DamageCalculator {
	private final AttributeCalculator attributeCalculator;

	public IDamage calculateThrustDamage(int strength) {
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

		return new Damage(dice, modifier, DamageType.CRUSHING);
	}

	public IDamage calculateThrustDamage(Character character) {
		int strength = attributeCalculator.calculate(character, Attribute.STRENGTH);
		return calculateThrustDamage(strength);
	}

	public IDamage calculateSwingDamage(int strength) {
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

		return new Damage(dice, modifier, DamageType.CRUSHING);
	}

	public IDamage calculateSwingDamage(Character character) {
		int strength = attributeCalculator.calculate(character, Attribute.STRENGTH);
		return calculateSwingDamage(strength);
	}
}
