package gm.tools.editor.character.attack;

import lombok.Data;

import java.util.Optional;

@Data
public class Reach {
	private static final String AWKWARD_SYMBOL = "*";
	private static final String RANGE_SYMBOL = "-";

	public Reach(int minReach, int maxReach, boolean isAwkward) {
		if (minReach < 0) {
			throw new IllegalArgumentException("minReach is below 0!");
		} else if (maxReach < minReach) {
			throw new IllegalArgumentException("maxReach is below minReach!");
		}

		this.minReach = minReach;
		this.maxReach = maxReach;
		this.isAwkward = isAwkward;
	}

	public Reach(int reach) {
		this(reach, reach, false);
	}

	private final int minReach, maxReach;
	private final boolean isAwkward;

	public boolean isInReach(int distance) {
		if (distance < 0) {
			throw new IllegalArgumentException("Distance is below 0!");
		}
		return distance >= minReach && distance <= maxReach;
	}

	@Override
	public String toString() {
		String string = "";

		if (minReach != maxReach) {
			string += String.format("%d%s%d", minReach, RANGE_SYMBOL, maxReach);
		} else {
			string += String.format("%d", minReach);
		}

		if (isAwkward) {
			string += AWKWARD_SYMBOL;
		}

		return string;
	}

	public static Optional<Reach> fromString(String string) {
		String withoutSpaces = string.replaceAll("\\s+", "");
		boolean isAwkward = withoutSpaces.endsWith(AWKWARD_SYMBOL);

		if (isAwkward) {
			withoutSpaces = withoutSpaces.substring(0, withoutSpaces.length() - 1);
		}

		try {
			if (withoutSpaces.contains(RANGE_SYMBOL)) {
				String[] parts = withoutSpaces.split(RANGE_SYMBOL);

				if (parts.length == 2) {
					int minReach = Integer.parseInt(parts[0]);
					int maxReach = Integer.parseInt(parts[1]);
					return Optional.of(new Reach(minReach, maxReach, isAwkward));
				}
			} else {
				int reach = Integer.parseInt(withoutSpaces);
				return Optional.of(new Reach(reach));
			}
		} catch (NumberFormatException e) {

		}

		return Optional.empty();
	}
}
