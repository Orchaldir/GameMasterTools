package gm.tools.editor.character.skill;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.PerceptionCalculator;
import gm.tools.editor.character.characteristic.WillCalculator;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SkillCalculatorTest {

	private CharacterTemplate template;
	private Skill skill0 = new Skill("skill0", Attribute.DEXTERITY.STRENGTH, Difficulty.VERY_HARD);
	private Skill skill1 = new Skill("skill1", Attribute.DEXTERITY, Difficulty.HARD);
	private Skill skill2 = new Skill("skill2", Attribute.INTELLIGENCE, Difficulty.AVERAGE);
	private Skill skill3 = new Skill("skill3", Attribute.HEALTH, Difficulty.EASY);
	private Skill skill4 = new Skill("skill4", Attribute.PERCEPTION, Difficulty.VERY_HARD);
	private Skill skill5 = new Skill("skill5", Attribute.WILL, Difficulty.HARD);
	private final static int SKILL_LEVEL0 = 8;
	private final static int SKILL_LEVEL1 = 11;
	private final static int SKILL_LEVEL2 = 14;
	private final static int SKILL_LEVEL3 = 17;
	private final static int SKILL_LEVEL4 = 16;
	private final static int SKILL_LEVEL5 = 19;
	private SkillCalculator calculator;

	@Before
	public void setUp() throws Exception {
		CharacterTemplateBuilder builder = new CharacterTemplateBuilder("test");
		builder.setAttributes(11, 12, 13, 14);
		builder.setPerceptionModifier(2);
		builder.setWillModifier(3);
		builder.addSkill(skill0, 1);
		builder.addSkill(skill1, 2);
		builder.addSkill(skill2, 3);
		builder.addSkill(skill3, 4);
		builder.addSkill(skill4, 5);
		builder.addSkill(skill5, 6);

		template = builder.createCharacterTemplate();
		calculator = new SkillCalculator(new PerceptionCalculator(), new WillCalculator());
	}

	@Test
	public void testCalculateLevel() {
		assertEquals(SKILL_LEVEL0, calculator.calculateLevel(template, skill0));
		assertEquals(SKILL_LEVEL1, calculator.calculateLevel(template, skill1));
		assertEquals(SKILL_LEVEL2, calculator.calculateLevel(template, skill2));
		assertEquals(SKILL_LEVEL3, calculator.calculateLevel(template, skill3));
		assertEquals(SKILL_LEVEL4, calculator.calculateLevel(template, skill4));
		assertEquals(SKILL_LEVEL5, calculator.calculateLevel(template, skill5));
	}

	@Test
	public void testCalculate() {
		Map<Skill, Integer> skills = calculator.calculate(template);
		assertEquals(SKILL_LEVEL0, (int) skills.get(skill0));
		assertEquals(SKILL_LEVEL1, (int) skills.get(skill1));
		assertEquals(SKILL_LEVEL2, (int) skills.get(skill2));
		assertEquals(SKILL_LEVEL3, (int) skills.get(skill3));
		assertEquals(SKILL_LEVEL4, (int) skills.get(skill4));
		assertEquals(SKILL_LEVEL5, (int) skills.get(skill5));
	}
}