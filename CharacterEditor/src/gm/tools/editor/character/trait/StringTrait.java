package gm.tools.editor.character.trait;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StringTrait implements Trait {
	private final String name;
	private final int cost;
}
