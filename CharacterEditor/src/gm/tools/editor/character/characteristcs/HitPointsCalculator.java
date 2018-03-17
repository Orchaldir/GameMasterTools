package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.Character;

public class HitPointsCalculator {
	public int calculate(Character character) {
		return character.getStrength() + character.getHitPointsModifier();
	}
}
