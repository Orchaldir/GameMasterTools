package gm.tools.editor.character.attack.damage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DamageType {
	BURNING("burn"),
	CORROSION("cor"),
	CRUSHING("cr"),
	CUTTING("cut"),
	FATIGUE("fat"),
	IMPALING("imp"),
	SMALL_PIERCING("pi-"),
	PIERCING("pi"),
	lARGE_PIERCING("pi+"),
	HUGE_PIERCING("pi++"),
	TOXIC("tox");

	@Getter
	private final String abbreviation;

	public static DamageType fromString(String text) {
		for (DamageType type : DamageType.values()) {
			if (text.equalsIgnoreCase(type.getAbbreviation())) {
				return type;
			}
		}

		throw new IllegalArgumentException("Unknown DamageType!");
	}
}
