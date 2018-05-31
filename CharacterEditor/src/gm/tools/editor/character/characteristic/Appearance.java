package gm.tools.editor.character.characteristic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Appearance {
	HORRIFIC(-24, -6),
	MONSTROUS(-20, -5),
	HIDEOUS(-16, -4),
	UGLY(-8, -2),
	UNATTRACTIVE(-4, -1),
	AVERAGE(0, 0),
	ATTRACTIVE(4, 1),
	BEAUTIFUL(12, 4),
	VERY_BEAUTIFUL(16, 6),
	TRANSCENDENT(20, 8);

	@Getter
	private final int cost;

	@Getter
	private final int reaction;
}
