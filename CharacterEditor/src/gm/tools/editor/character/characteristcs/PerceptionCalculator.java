package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.Character;

public class PerceptionCalculator {
	public int calculate(Character character) {
		return character.getIntelligence() + character.getPerceptionModifier();
	}
}
