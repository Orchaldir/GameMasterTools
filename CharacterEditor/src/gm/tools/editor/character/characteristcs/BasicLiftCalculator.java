package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.Character;

public class BasicLiftCalculator {
	public int calculate(Character character) {
		return (int) Math.round(character.getStrength() * character.getStrength() / 10.0);
	}
}
