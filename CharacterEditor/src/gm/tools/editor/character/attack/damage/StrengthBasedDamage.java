package gm.tools.editor.character.attack.damage;

import gm.tools.editor.character.Character;
import lombok.Data;
import lombok.Getter;

import java.util.Optional;

@Data
public class StrengthBasedDamage implements IDamage {

	@Getter
	private final StrengthBasedType strengthBasedType;

	@Getter
	private final int strengthBasedModifier;

	@Getter
	private final DamageType type;

	@Override
	public int getDice() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getModifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DamageType getType() {
		return type;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public IDamage resolveFor(Character character) {
		return null;
	}

	public static Optional<StrengthBasedDamage> fromString(String text) {
		String[] parts = text.split("\\s+");

		if (parts.length == 2) {
			String firstPart = parts[0];
			StrengthBasedType strengthBasedType;

			if (firstPart.startsWith(StrengthBasedType.SWINGING.getAbbreviation())) {
				strengthBasedType = StrengthBasedType.SWINGING;
			} else if (firstPart.startsWith(StrengthBasedType.THRUSTING.getAbbreviation())) {
				strengthBasedType = StrengthBasedType.THRUSTING;
			} else {
				return Optional.empty();
			}

			firstPart = firstPart.replaceFirst(strengthBasedType.getAbbreviation(), "");
			int modifier = 0;

			if (!firstPart.isEmpty()) {
				modifier = Integer.parseInt(firstPart);
			}

			DamageType type = DamageType.fromString(parts[1]);

			return Optional.of(new StrengthBasedDamage(strengthBasedType, modifier, type));
		}

		return Optional.empty();
	}

	@Override
	public String toString() {
		String string = strengthBasedType.getAbbreviation();

		if (strengthBasedModifier != 0) {
			string += String.format("%+d", strengthBasedModifier);
		}

		string += String.format(" %s", type.getAbbreviation());

		return string;
	}
}
