import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

public class test07STTWithSuperGlue extends SuperGlueV3 {

	public String parameters = "{\"username\":\"\","
			+ "\"password\":\"\","
			+ "\"url\":\"https://github.com/snippet-java/test-cases-resources/raw/master/STTInput.wav\"}";
	
	public static void main(String[] args) {
		test07STTWithSuperGlue stt = new test07STTWithSuperGlue();
        System.out.println(stt.process(stt.parameters));
	}
	
	public static JsonObject main(JsonObject args) throws IllegalArgumentException, IllegalAccessException {
		JsonObject response = new JsonObject();
		
		test07STTWithSuperGlue hello = new test07STTWithSuperGlue();
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
}
