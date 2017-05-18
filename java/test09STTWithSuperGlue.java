import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

public class test09STTWithSuperGlue {
	
	private final String htmlGistUrl = "https://gist.githubusercontent.com/snippet-java/fd3aa1c2ab893bf8e1bcd90073ceab99/raw";
	
	public String parameters = "{\"username\":\"\","
			+ "\"password\":\"\","
			+ "\"url\":\"https://github.com/snippet-java/test-cases-resources/raw/master/STTInput.wav\"}";
	
	public static void main(String[] args) {
		test09STTWithSuperGlue stt = new test09STTWithSuperGlue();
        System.out.println(stt.process(stt.parameters));
	}
	
	public static JsonObject main(JsonObject args) throws IllegalArgumentException, IllegalAccessException {
		JsonObject response = new JsonObject();
		test09STTWithSuperGlue stt = new test09STTWithSuperGlue();
		if(args.getAsJsonPrimitive("__ow_method").getAsString().equalsIgnoreCase("get"))
			response.addProperty("html", stt.generateHTMLForm().toString());
		else 
			response.addProperty("output", stt.process(args.toString()).toString());
        
        return response;
	}

	
	private JsonObject process(String jsonString) {

		JsonParser parser = new JsonParser(); 
		JsonObject mybean = parser.parse(jsonString).getAsJsonObject();
		
		SpeechToText service = new SpeechToText();
		service.setUsernameAndPassword(mybean.getAsJsonPrimitive("username").getAsString(), 
				mybean.getAsJsonPrimitive("password").getAsString());
		
		JsonObject output = new JsonObject();
		File audio;
		try {
			audio = downloadZipFile(mybean.getAsJsonPrimitive("url").getAsString());
			SpeechResults transcript = service.recognize(audio).execute();

	        output = parser.parse(transcript.toString()).getAsJsonObject();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return output;
	}
	
	private File downloadZipFile(String url) throws ClientProtocolException, IOException {	
		System.out.println("Downloading audio file");
	    HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(httpget);
        HttpEntity entity = response.getEntity();
        
        InputStream is = entity.getContent();
        File audioFile = new File("sampleaudio.wav");
        FileOutputStream fos = new FileOutputStream(audioFile);
        int inByte;
        while((inByte = is.read()) != -1)
             fos.write(inByte);
        is.close();
        fos.close();
        
		System.out.println("Audio File downloaded");
		
		
		return audioFile;
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
