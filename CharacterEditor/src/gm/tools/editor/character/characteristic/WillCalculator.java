package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;

public class WillCalculator {
	public int calculate(Character character) {
		return character.getIntelligence() + character.getWillModifier();
	}
}
