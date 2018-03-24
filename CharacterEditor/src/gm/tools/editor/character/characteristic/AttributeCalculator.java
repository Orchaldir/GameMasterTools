package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;

public class AttributeCalculator {
	public int calculate(Character character, Attribute attribute) {
		return 10 + character.getAttributeModifier(attribute);
	}
}
