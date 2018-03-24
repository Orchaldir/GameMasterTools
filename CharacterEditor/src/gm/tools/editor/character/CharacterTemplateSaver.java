package gm.tools.editor.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class CharacterTemplateSaver {
	private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

	public String toJason(Character template) {
		JsonObject templateJason = new JsonObject();
		//templateJason.addProperty("name", template.getName());
		return "";
	}
}
