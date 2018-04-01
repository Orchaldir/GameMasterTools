package gm.tools.editor.character.skill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import gm.tools.editor.character.characteristic.Attribute;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class SkillManagerWithJson implements SkillManager {
	private static final Skill DEFAULT = new Skill("DEFAULT", Attribute.DEXTERITY, Difficulty.VERY_HARD);
	private final Map<String, Skill> skills = new HashMap<>();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	private final JsonParser parser = new JsonParser();

	@Override
	public void add(Skill skill) {
		skills.put(skill.getName(), skill);
	}

	@Override
	public Skill get(String name) {
		Skill skill = skills.get(name);

		if (skill == null) {
			return DEFAULT;
		}

		return skill;
	}

	@Override
	public Collection<String> getSkillNames() {
		return skills.keySet();
	}

	@Override
	public Collection<String> getSortedSkillNames() {
		List<String> list = new ArrayList<>(skills.keySet());
		Collections.sort(list);
		return list;
	}

	// saving

	@Override
	public void save(String filename) throws IOException {
		File file = new File(filename);
		file.createNewFile();

		FileWriter writer = new FileWriter(file);

		writer.write(saveToJson());
		writer.flush();
		writer.close();
	}

	public String saveToJson() {
		List<Skill> list = new ArrayList<>(skills.values());
		Collections.sort(list, Comparator.comparing(Skill::getName));
		return gson.toJson(list);
	}

	// loading

	@Override
	public void load(String filename) throws IOException {
		File file = new File(filename);
		String json = FileUtils.readFileToString(file, "UTF-8");
		loadFromJson(json);
	}

	public void loadFromJson(String json) {
		Type listType = new TypeToken<ArrayList<Skill>>() {
		}.getType();
		List<Skill> skillList = new Gson().fromJson(json, listType);

		skills.clear();

		for (Skill skill : skillList) {
			add(skill);
		}
	}
}
