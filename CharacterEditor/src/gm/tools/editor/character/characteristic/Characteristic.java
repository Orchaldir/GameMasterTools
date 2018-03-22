package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.Character;

import java.util.EnumSet;

public enum Characteristic {
	// attributes
	DEXTERITY(CharacteristicType.ATTRIBUTE),
	HEALTH(CharacteristicType.ATTRIBUTE),
	INTELLIGENCE(CharacteristicType.ATTRIBUTE),
	PERCEPTION(CharacteristicType.ATTRIBUTE),
	STRENGTH(CharacteristicType.ATTRIBUTE),
	WILL(CharacteristicType.ATTRIBUTE),

	// secondary characteristics
	BASIC_LIFT(CharacteristicType.SECONDARY_CHARACTERISTIC),
	BASIC_MOVE(CharacteristicType.SECONDARY_CHARACTERISTIC),
	BASIC_SPEED(CharacteristicType.SECONDARY_CHARACTERISTIC),
	DAMAGE(CharacteristicType.SECONDARY_CHARACTERISTIC),
	FATIGUE_POINTS(CharacteristicType.SECONDARY_CHARACTERISTIC),
	HIT_POINTS(CharacteristicType.SECONDARY_CHARACTERISTIC),
	SIZE_MODIFIER(CharacteristicType.SECONDARY_CHARACTERISTIC);

	private final CharacteristicType type;

	Characteristic(CharacteristicType type) {
		this.type = type;
	}

	public boolean isAttribute() {
		return type == CharacteristicType.ATTRIBUTE;
	}

	public boolean isSecondaryCharacteristic() {
		return type == CharacteristicType.SECONDARY_CHARACTERISTIC;
	}
}
