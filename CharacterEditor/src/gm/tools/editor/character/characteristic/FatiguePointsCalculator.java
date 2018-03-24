package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FatiguePointsCalculator {
	private final AttributeCalculator attributeCalculator;

	public int calculate(Character character) {
		int health = attributeCalculator.calculate(character, Attribute.HEALTH);
		return health + character.getFatiguePointsModifier();
	}
}
