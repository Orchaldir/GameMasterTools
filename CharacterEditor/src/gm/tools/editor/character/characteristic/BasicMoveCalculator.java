package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BasicMoveCalculator {
	private final BasicSpeedCalculator basicSpeedCalculator;

	public int calculate(Character character) {
		double basicSpeed = basicSpeedCalculator.calculate(character);
		return (int) (Math.floor(basicSpeed) + character.getBasicMoveModifier());
	}
}
