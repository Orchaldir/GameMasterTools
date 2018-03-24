package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.skill.Difficulty;
import gm.tools.editor.character.skill.Skill;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterTemplateSaverTest {
	private Skill skill0 = new Skill("skill0", Attribute.DEXTERITY.STRENGTH, Difficulty.VERY_HARD);
	private CharacterTemplate template;
	private CharacterTemplateSaver saver;

	@Before
	public void setUp() throws Exception {
		CharacterTemplateBuilder builder = new CharacterTemplateBuilder("test");
		builder.setStrength(11);
		builder.addSkill(skill0, 2);

		template = builder.createCharacterTemplate();

		saver = new CharacterTemplateSaver();
	}

	@Test
	public void testToJason() {
		String json = saver.toJason(template);
		System.out.println(json);
	}
}