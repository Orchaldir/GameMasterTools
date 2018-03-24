package gm.tools.editor.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;

public class CharacterTemplateSaver {
	private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	// properties
	private final static String NAME = "name";
	private final static String ATTRIBUTES = "attributes";
	private final static String CHARACTERISTICS = "characteristics";
	private final static String SKILLS = "skills";

	// save

	public String toJason(Character template) {
		JsonObject templateJson = new JsonObject();
		templateJson.addProperty(NAME, template.getName());

		convertAttributesToJson(template, templateJson);
		convertCharacteristicsToJson(template, templateJson);
		convertSkillsToJson(template, templateJson);

		return gson.toJson(templateJson);
	}

	private void convertAttributesToJson(Character template, JsonObject templateJson) {
		JsonObject attributesJson = new JsonObject();

		for (Attribute attribute : Attribute.values()) {
			attributesJson.addProperty(attribute.toString(), template.getAttributeModifier(attribute));
		}

		templateJson.add(ATTRIBUTES, attributesJson);
	}

	private void convertCharacteristicsToJson(Character template, JsonObject templateJson) {
		JsonObject characteristicsJson = new JsonObject();

		for (Characteristic characteristic : Characteristic.MODIFIERS) {
			characteristicsJson.addProperty(characteristic.toString(), template.getCharacteristicModifier(characteristic));
		}

		templateJson.add(CHARACTERISTICS, characteristicsJson);
	}

	private void convertSkillsToJson(Character template, JsonObject templateJson) {
		JsonObject skillsJson = new JsonObject();

		for (Skill skill : template.getSkills()) {
			int level = template.getRelativeSkillLevel(skill);
			skillsJson.addProperty(skill.getName(), level);
		}

		templateJson.add(SKILLS, skillsJson);
	}
}
