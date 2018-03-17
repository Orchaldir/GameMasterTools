package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.Character;

public class BasicMoveCalculator {
	private final BasicSpeedCalculator basicSpeedCalculator;

	public BasicMoveCalculator(BasicSpeedCalculator basicSpeedCalculator) {
		this.basicSpeedCalculator = basicSpeedCalculator;
	}

	public int calculate(Character character) {
		double basicSpeed = basicSpeedCalculator.calculate(character);
		return (int) (Math.floor(basicSpeed) + character.getBasicMoveModifier());
	}
}
