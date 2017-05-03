import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

public class test04Translator {

	public String parameters = "{\"text\":\"hello my friend\",\"fromLanguage\":\"ENGLISH\",\"toLanguage\":\"SPANISH\"}";
	
	public static void main(String[] args) {
		test04Translator test = new test04Translator();
		JsonObject arg = new JsonParser().parse(test.parameters).getAsJsonObject();
		System.out.println(test04Translator.main(arg));
	}
	
	public static JsonObject main(JsonObject args) {

		JsonParser parser = new JsonParser(); 
		LanguageTranslator service = new LanguageTranslator();
		
		//Get from watson_cred
        JsonObject cred = new JsonParser().parse(System.getProperty("env")).getAsJsonObject()
        		.getAsJsonArray("language_translator").get(0).getAsJsonObject().getAsJsonObject("credentials");
        service.setUsernameAndPassword(cred.getAsJsonPrimitive("username").getAsString(), cred.getAsJsonPrimitive("password").getAsString());
	
        TranslationResult translationResult = service.translate(
        		args.get("text").getAsString(),
				Language.valueOf(args.get("fromLanguage").getAsString().toUpperCase()), 
				Language.valueOf(args.get("toLanguage").getAsString().toUpperCase()))
        		.execute();
		
        JsonObject response = parser.parse(translationResult.toString()).getAsJsonObject();
        
        return response;
	}
}