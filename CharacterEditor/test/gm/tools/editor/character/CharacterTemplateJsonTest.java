package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.skill.Difficulty;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.skill.SkillManagerWithJson;
import gm.tools.editor.character.trait.StringTrait;
import gm.tools.editor.character.trait.Trait;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CharacterTemplateJsonTest {
	private Skill skill0 = new Skill("skill0", Attribute.DEXTERITY, Difficulty.VERY_HARD);
	private Trait trait0 = new StringTrait("trait0", 22);
	private SkillManagerWithJson skillManager;
	private CharacterTemplate template;
	private CharacterTemplateJson characterTemplateJson;

	@Before
	public void setUp() throws Exception {
		skillManager = new SkillManagerWithJson();
		skillManager.add(skill0);

		CharacterTemplateBuilder builder = new CharacterTemplateBuilder("test");
		builder.setStrength(1);
		builder.setBasicSpeedModifier(3);
		builder.addSkill(skill0, 2);
		builder.addTrait(trait0);

		template = builder.createCharacterTemplate();

		characterTemplateJson = new CharacterTemplateJson(skillManager);
	}

	@Test
	public void test() {
		String json = characterTemplateJson.saveToJson(template);
		CharacterTemplate templateNew = characterTemplateJson.loadFromJson(json);

		assertEquals("test", templateNew.getName());
		assertEquals(1, templateNew.getStrengthModifier());
		assertEquals(3, templateNew.getBasicSpeedModifier());

		Collection<Skill> skills = templateNew.getSkills();

		assertNotNull(skills);
		assertEquals(1, skills.size());
		assertTrue(skills.contains(skill0));

		Collection<Trait> traits = templateNew.getTraits();

		assertNotNull(traits);
		assertEquals(1, traits.size());
		assertTrue(traits.contains(trait0));
	}
}