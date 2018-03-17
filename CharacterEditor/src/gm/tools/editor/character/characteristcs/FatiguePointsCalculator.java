package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.Character;

public class FatiguePointsCalculator {
	public int calculate(Character character) {
		return character.getHealth() + character.getFatiguePointsModifier();
	}
}
