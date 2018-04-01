package gm.tools.editor.character.skill;

import java.io.IOException;
import java.util.Collection;

public interface SkillManager {
	void add(Skill skill);

	Skill get(String name);

	Collection<String> getSkillNames();

	void save(String filename) throws IOException;

	void load(String filename) throws IOException;
}
