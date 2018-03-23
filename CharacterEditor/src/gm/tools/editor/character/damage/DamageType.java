package gm.tools.editor.character.damage;

import lombok.Getter;

public enum DamageType {
	BURNING("burn"),
	CORROSION("cor"),
	CRUSHING("cr"),
	CUTTING("cur"),
	FATIGUE("fat"),
	IMPALING("imp"),
	SMALL_PIERCING("pi-"),
	PIERCING("pi"),
	lARGE_PIERCING("pi+"),
	HUGE_PIERCING("pi++"),
	TOXIC("tox");

	@Getter
	private final String abbreviation;

	DamageType(String abbreviation) {
		this.abbreviation = abbreviation;
	}

}
