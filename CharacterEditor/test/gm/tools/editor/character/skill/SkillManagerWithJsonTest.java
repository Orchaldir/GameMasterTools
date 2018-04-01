package gm.tools.editor.character.skill;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.CharacterTemplateJson;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.trait.StringTrait;
import gm.tools.editor.character.trait.Trait;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;

public class SkillManagerWithJsonTest {
	private Skill skill0 = new Skill("skill0", Attribute.DEXTERITY, Difficulty.VERY_HARD);
	private Skill skill1 = new Skill("skill1", Attribute.INTELLIGENCE, Difficulty.AVERAGE);
	private Skill skill2 = new Skill("skill2", Attribute.PERCEPTION, Difficulty.EASY);
	private SkillManagerWithJson skillManager;

	@Before
	public void setUp() throws Exception {
		skillManager = new SkillManagerWithJson();
		skillManager.add(skill0);
		skillManager.add(skill1);
		skillManager.add(skill2);
	}

	@Test
	public void test() {
		String json = skillManager.saveToJson();

		SkillManagerWithJson newSkillManager = new SkillManagerWithJson();
		newSkillManager.loadFromJson(json);

		assertEquals(skill0, newSkillManager.get(skill0.getName()));
		assertEquals(skill1, newSkillManager.get(skill1.getName()));
		assertEquals(skill2, newSkillManager.get(skill2.getName()));
	}
}