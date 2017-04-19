import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HelloWorld {

	private static String parameters = 
		"{\"name\":\"World\"}";
	
	public static void main(String[] args) {
		JsonObject arg = new JsonObject();
		arg.addProperty("version", "local-version");
		
		//construct a alexa-like request
		JsonObject request = new JsonObject();
		JsonObject intent = new JsonObject();
		JsonObject slots = new JsonObject();
		JsonObject text = new JsonObject();
		JsonParser parser = new JsonParser();
		text.addProperty("value", 
				parser.parse(parameters).getAsJsonObject().getAsJsonPrimitive("name").getAsString());
		slots.add("Text", text);
		intent.add("slots", slots);
		request.add("intent", intent);
		arg.add("request", request);
		
		JsonObject response = HelloWorld.main(arg);
		System.out.println(response.getAsJsonObject("response").getAsJsonObject("outputSpeech")
				.getAsJsonPrimitive("text").getAsString());
	}
	
	public static JsonObject main(JsonObject args) {
		    
        JsonObject response = new JsonObject();
        String name = args.getAsJsonObject("request").getAsJsonObject("intent")
				.getAsJsonObject("slots").getAsJsonObject("Text").getAsJsonPrimitive("value").getAsString();
        String speechText = "Hello " + name + ". Welcome to the Openwhisk Alexa program";

        response.addProperty("version", args.getAsJsonPrimitive("version").getAsString());
        
        JsonObject resp = new JsonObject();
        JsonObject outputSpeech = new JsonObject();
        outputSpeech.addProperty("type", "PlainText");
        outputSpeech.addProperty("text", speechText);
        resp.add("outputSpeech", outputSpeech);
        
        JsonObject card = new JsonObject();
        card.addProperty("content", speechText);
        card.addProperty("title", "Session");
        card.addProperty("type", "Simple");
        resp.add("card", card);
        
        JsonObject rePrompt = new JsonObject();
        rePrompt.add("outputSpeech", outputSpeech);
        resp.add("reprompt", rePrompt);
        
        resp.addProperty("shouldEndSession", false);
        
        response.add("response", resp);
        response.add("sessionAttributes", new JsonObject());
        return response;
    }

}
