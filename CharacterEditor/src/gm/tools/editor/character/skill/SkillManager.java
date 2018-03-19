package gm.tools.editor.character.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SkillManager {
	private final Map<String, Skill> skills = new HashMap<>();

	public void add(Skill skill) {
		skills.put(skill.getName(), skill);
	}

	public Optional<Skill> get(String name) {
		return Optional.ofNullable(skills.get(name));
	}

	public Set<String> getSkillNames() {
		return skills.keySet();
	}

}
