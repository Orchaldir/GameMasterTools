package gm.tools.editor.character.characteristic;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum Characteristic {
	BASIC_LIFT,
	BASIC_MOVE,
	BASIC_SPEED,
	DAMAGE,
	FATIGUE_POINTS,
	HIT_POINTS,
	SIZE_MODIFIER;

	public final static Set<Characteristic> MODIFIERS = EnumSet.of(BASIC_MOVE, BASIC_SPEED, FATIGUE_POINTS, HIT_POINTS, SIZE_MODIFIER);
}
