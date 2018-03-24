package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BasicLiftCalculator {
	private final AttributeCalculator attributeCalculator;

	public int calculate(Character character) {
		int strength = attributeCalculator.calculate(character, Attribute.STRENGTH);
		return (int) Math.round(strength * strength / 10.0);
	}
}
