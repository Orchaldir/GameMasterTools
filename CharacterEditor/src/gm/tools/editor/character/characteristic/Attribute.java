package gm.tools.editor.character.characteristic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Attribute {
	DEXTERITY("DX"),
	HEALTH("HT"),
	INTELLIGENCE("IQ"),
	PERCEPTION("Per"),
	STRENGTH("ST"),
	WILL("Will");

	@Getter
	private final String text;
}
