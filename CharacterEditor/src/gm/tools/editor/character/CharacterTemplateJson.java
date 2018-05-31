package gm.tools.editor.character;

import com.google.gson.*;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.skill.SkillManager;
import gm.tools.editor.character.trait.StringTrait;
import gm.tools.editor.character.trait.Trait;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
	private final static String TRAITS = "traits";

	// save

	public String saveToJson(Character template) {
		JsonObject templateJson = new JsonObject();
		templateJson.addProperty(NAME, template.getName());

		saveAttributesToJson(template, templateJson);
		saveCharacteristicsToJson(template, templateJson);
		saveSkillsToJson(template, templateJson);
		saveTraitsToJson(template, templateJson);

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
		List<Skill> list = new ArrayList<>(template.getSkills());
		list.sort(Comparator.comparing(Skill::getName));

		for (Skill skill : list) {
			int level = template.getRelativeSkillLevel(skill);
			skillsJson.addProperty(skill.getName(), level);
		}

		templateJson.add(SKILLS, skillsJson);
	}

	private void saveTraitsToJson(Character template, JsonObject templateJson) {
		JsonObject traitsJson = new JsonObject();
		List<Trait> list = new ArrayList<>(template.getTraits());
		list.sort(Comparator.comparing(Trait::getName));

		for (Trait trait : list) {
			traitsJson.addProperty(trait.getName(), trait.getCost());
		}

		templateJson.add(TRAITS, traitsJson);
	}

	// load

	public CharacterTemplate loadFromJson(String json) {
		JsonElement element = parser.parse(json);

		if (element.isJsonObject()) {
			JsonObject templateJson = element.getAsJsonObject();
			CharacterTemplateBuilder builder = new CharacterTemplateBuilder(templateJson.get(NAME).getAsString());

			loadAttributesFromJson(builder, templateJson);
			loadCharacteristicsFromJson(builder, templateJson);
			loadSkillsFromJson(builder, templateJson);
			loadTraitsFromJson(builder, templateJson);

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

		if (skillsJson == null) {
			System.out.print("loadSkillsFromJson(): Found no skills!");
			return;
		}

		for (Map.Entry<String, JsonElement> entry : skillsJson.entrySet()) {
			Skill skill = skillManager.get(entry.getKey());

			builder.addSkill(skill, entry.getValue().getAsInt());
		}
	}

	private void loadTraitsFromJson(CharacterTemplateBuilder builder, JsonObject templateJson) {
		JsonObject traitsJson = templateJson.getAsJsonObject(TRAITS);

		if (traitsJson == null) {
			System.out.print("loadTraitsFromJson(): Found no traits!");
			return;
		}

		for (Map.Entry<String, JsonElement> entry : traitsJson.entrySet()) {
			builder.addTrait(new StringTrait(entry.getKey(), entry.getValue().getAsInt()));
		}
	}
}
