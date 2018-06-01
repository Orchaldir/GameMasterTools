package gm.tools.editor.character.attack.damage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StrengthBasedType {
	SWINGING("sw"),
	THRUSTING("thr");

	@Getter
	private final String abbreviation;
}
