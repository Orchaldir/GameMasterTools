package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;

public class BasicSpeedCalculator {
	public double calculate(Character character) {
		int sum = character.getDexterity() + character.getHealth() + character.getBasicSpeedModifier();
		return sum / 4.0;
	}
}
