import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

public class test06TranslatorWithSuperGlue extends SuperGlueV3 {

	public String parameters = "{\"username\":\"\",\"password\":\"\","
			+ "\"text\":\"hello my friend\","
			+ "\"fromLanguage\":\"ENGLISH\","
			+ "\"toLanguage\":\"SPANISH\"}";
	
	public static void main(String[] args) {
		test06TranslatorWithSuperGlue hello = new test06TranslatorWithSuperGlue();
        System.out.println(hello.process(hello.parameters));
	}
	
	public static JsonObject main(JsonObject args) throws IllegalArgumentException, IllegalAccessException {
		JsonObject response = new JsonObject();
		
		test06TranslatorWithSuperGlue hello = new test06TranslatorWithSuperGlue();
		response.addProperty("html", hello.generateResponse(args));
        
        return response;
	}

	@Override
	JsonObject getParameters() {
		return new JsonParser().parse(parameters).getAsJsonObject();
	}

	@Override
	JsonObject process(String jsonString) {

		JsonParser parser = new JsonParser(); 
		JsonObject mybean = parser.parse(jsonString).getAsJsonObject();
		
		LanguageTranslator service = new LanguageTranslator();
	
		service.setUsernameAndPassword(mybean.getAsJsonPrimitive("username").getAsString(), 
				mybean.getAsJsonPrimitive("password").getAsString());
		
        TranslationResult translationResult = service.translate(
        		mybean.get("text").getAsString(),
				Language.valueOf(mybean.get("fromLanguage").getAsString().toUpperCase()), 
				Language.valueOf(mybean.get("toLanguage").getAsString().toUpperCase()))
        		.execute();
		
        JsonObject output = parser.parse(translationResult.toString()).getAsJsonObject();
        
        return output;
	}
	
	
}
