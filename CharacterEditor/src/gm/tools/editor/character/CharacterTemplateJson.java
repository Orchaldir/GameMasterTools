package gm.tools.editor.character;

import com.google.gson.*;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.skill.SkillManager;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CharacterTemplateJson {
	private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	private final JsonParser parser = new JsonParser();

	private final SkillManager skillManager;

	// properties
	private final static String NAME = "name";
	private final static String ATTRIBUTES = "attributes";
	private final static String CHARACTERISTICS = "characteristics";
	private final static String SKILLS = "skills";

	// save

	public String saveToJason(Character template) {
		JsonObject templateJson = new JsonObject();
		templateJson.addProperty(NAME, template.getName());

		saveAttributesToJson(template, templateJson);
		saveCharacteristicsToJson(template, templateJson);
		saveSkillsToJson(template, templateJson);

		return gson.toJson(templateJson);
	}

	private void saveAttributesToJson(Character template, JsonObject templateJson) {
		JsonObject attributesJson = new JsonObject();

		for (Attribute attribute : Attribute.values()) {
			attributesJson.addProperty(attribute.toString(), template.getAttributeModifier(attribute));
		}

		templateJson.add(ATTRIBUTES, attributesJson);
	}

	private void saveCharacteristicsToJson(Character template, JsonObject templateJson) {
		JsonObject characteristicsJson = new JsonObject();

		for (Characteristic characteristic : Characteristic.MODIFIERS) {
			characteristicsJson.addProperty(characteristic.toString(), template.getCharacteristicModifier(characteristic));
		}

		templateJson.add(CHARACTERISTICS, characteristicsJson);
	}

	private void saveSkillsToJson(Character template, JsonObject templateJson) {
		JsonObject skillsJson = new JsonObject();

		for (Skill skill : template.getSkills()) {
			int level = template.getRelativeSkillLevel(skill);
			skillsJson.addProperty(skill.getName(), level);
		}

		templateJson.add(SKILLS, skillsJson);
	}

	// load

	public CharacterTemplate loadFromJason(String json) {
		JsonElement element = parser.parse(json);

		if (element.isJsonObject()) {
			JsonObject templateJson = element.getAsJsonObject();
			CharacterTemplateBuilder builder = new CharacterTemplateBuilder(templateJson.get(NAME).getAsString());

			loadAttributesFromJson(builder, templateJson);
			loadCharacteristicsFromJson(builder, templateJson);
			loadSkillsFromJson(builder, templateJson);

			return builder.createCharacterTemplate();
		}

		return null;
	}

	private void loadAttributesFromJson(CharacterTemplateBuilder builder, JsonObject templateJson) {
		JsonObject attributesJson = templateJson.getAsJsonObject(ATTRIBUTES);

		for (Attribute attribute : Attribute.values()) {
			JsonElement element = attributesJson.get(attribute.toString());

			if (element != null) {
				builder.setAttribute(attribute, element.getAsInt());
			}
		}
	}

	private void loadCharacteristicsFromJson(CharacterTemplateBuilder builder, JsonObject templateJson) {
		JsonObject characteristicsJson = templateJson.getAsJsonObject(CHARACTERISTICS);

		for (Characteristic characteristic : Characteristic.MODIFIERS) {
			JsonElement element = characteristicsJson.get(characteristic.toString());

			if (element != null) {
				builder.setCharacteristicModifier(characteristic, element.getAsInt());
			}
		}
	}

	private void loadSkillsFromJson(CharacterTemplateBuilder builder, JsonObject templateJson) {
		JsonObject skillsJson = templateJson.getAsJsonObject(SKILLS);

		for (Map.Entry<String, JsonElement> entry : skillsJson.entrySet()) {
			Optional<Skill> skill = skillManager.get(entry.getKey());

			skill.ifPresent(s -> builder.addSkill(s, entry.getValue().getAsInt()));
		}
	}
}
