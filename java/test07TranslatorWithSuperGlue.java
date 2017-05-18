import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.language_translator.v2.model.TranslationResult;

public class test07TranslatorWithSuperGlue {

	private final String htmlGistUrl = "https://gist.githubusercontent.com/snippet-java/fd3aa1c2ab893bf8e1bcd90073ceab99/raw";
	
	public String parameters = "{\"username\":\"\",\"password\":\"\","
			+ "\"text\":\"hello my friend\","
			+ "\"fromLanguage\":\"ENGLISH\","
			+ "\"toLanguage\":\"SPANISH\"}";
	
	public static void main(String[] args) {
		test07TranslatorWithSuperGlue trans = new test07TranslatorWithSuperGlue();
        System.out.println(trans.process(trans.parameters));
	}
	
	public static JsonObject main(JsonObject args) throws IllegalArgumentException, IllegalAccessException {
		JsonObject response = new JsonObject();
		test07TranslatorWithSuperGlue trans = new test07TranslatorWithSuperGlue();
		if(args.getAsJsonPrimitive("__ow_method").getAsString().equalsIgnoreCase("get"))
			response.addProperty("html", trans.generateHTMLForm().toString());
		else 
			response.addProperty("output", trans.process(args.toString()).toString());
        
        return response;
	}
	
	private JsonObject process(String jsonString) {

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
	
	private StringBuilder generateHTMLForm() {
    	HttpClient client = HttpClientBuilder.create().build();
		
		HttpGet get = new HttpGet(htmlGistUrl);
		
		HttpResponse resp;
		StringBuilder result = new StringBuilder();
		try {
			resp = client.execute(get);
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(resp.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				
				//replace with dynamic fields for html form
				if(line.indexOf("{{inputs}}") >= 0) {
					ArrayList<String> htmlCode = generateDynamicHTMLCode(new JsonParser().parse(parameters).getAsJsonObject());
					for (String htmlLine : htmlCode) {
						result.append(htmlLine+"\n");
					}
				}
				else
					result.append(line+"\n");
			}
		} catch (ClientProtocolException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private ArrayList<String> generateDynamicHTMLCode(JsonObject jsonObj)
			throws IllegalArgumentException, IllegalAccessException {
		ArrayList<String> htmlCode = new ArrayList<String>();
		
		for (Map.Entry<String,JsonElement> entry : jsonObj.entrySet()) {
			
			String textFieldHTML = "";
			textFieldHTML = String.format("%s: <input type=\"text\" name=\"%s\" value=\"%s\" ><br><br>",
					entry.getKey(), entry.getKey(), entry.getValue().getAsString());
			htmlCode.add(textFieldHTML);
		}
		
		return htmlCode;
	}
	
}
