package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;

public class AttributeCalculator {
	public int calculate(Character character, Characteristic characteristic) {
		return 10 + character.getPerceptionModifier();
	}
}
