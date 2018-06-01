package gm.tools.editor.character.attack.damage;

import gm.tools.editor.character.Character;

public interface IDamage {
	int getDice();

	int getModifier();

	DamageType getType();

	boolean isResolved();

	IDamage resolveFor(Character character);
}
