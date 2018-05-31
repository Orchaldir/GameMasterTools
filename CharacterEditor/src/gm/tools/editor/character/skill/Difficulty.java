package gm.tools.editor.character.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Difficulty {
	EASY(-1),
	AVERAGE(-2),
	HARD(-3),
	VERY_HARD(-4);

	@Getter
	private final int startLevel;
}
