package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BasicSpeedCalculator {
	private final AttributeCalculator attributeCalculator;

	public double calculate(Character character) {
		int dexterity = attributeCalculator.calculate(character, Attribute.DEXTERITY);
		int health = attributeCalculator.calculate(character, Attribute.HEALTH);
		int sum = dexterity + health + character.getBasicSpeedModifier();
		return sum / 4.0;
	}
}
