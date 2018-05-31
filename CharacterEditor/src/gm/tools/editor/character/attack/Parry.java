package gm.tools.editor.character.attack;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class Parry {
	private static final String NO_SYMBOL = "No";
	private static final String FENCING_SYMBOL = "F";
	private static final String UNBALANCED_SYMBOL = "U";

	public enum ParryType {
		NO_PARRY,
		NORMAL,
		FENCING,
		UNBALANCED
	}

	private final ParryType type;
	private final int modifier;

	public Parry(ParryType type) {
		this(type, 0);
	}

	@Override
	public String toString() {
		switch (type) {
			case NO_PARRY:
				return NO_SYMBOL;
			case NORMAL:
				return Integer.toString(modifier);
			case FENCING:
				return String.format("%d%s", modifier, FENCING_SYMBOL);
			case UNBALANCED:
				return String.format("%d%s", modifier, UNBALANCED_SYMBOL);
			default:
				return "";
		}
	}

	public static Optional<Parry> fromString(String string) {
		String withoutSpaces = string.replaceAll("\\s+", "");

		if (withoutSpaces.compareTo(NO_SYMBOL) == 0) {
			return Optional.of(new Parry(ParryType.NO_PARRY));
		} else if (withoutSpaces.endsWith(FENCING_SYMBOL)) {
			return Optional.of(new Parry(ParryType.FENCING, getModifier(withoutSpaces)));
		} else if (withoutSpaces.endsWith(UNBALANCED_SYMBOL)) {
			return Optional.of(new Parry(ParryType.UNBALANCED, getModifier(withoutSpaces)));
		}

		return Optional.of(new Parry(ParryType.NORMAL, Integer.parseInt(withoutSpaces)));
	}

	private static int getModifier(String text) {
		text = text.substring(0, text.length() - 1);
		return Integer.parseInt(text);
	}
}
