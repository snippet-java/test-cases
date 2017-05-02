import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

public class test05VisualRecognition {

	public String parameters = "{\"url\":\"https://www.whitehouse.gov/sites/whitehouse.gov/files/images/first-family/44_barack_obama%5B1%5D.jpg\"}";
	
	public static void main(String[] args) {
		test05VisualRecognition test = new test05VisualRecognition();
		JsonObject arg = new JsonParser().parse(test.parameters).getAsJsonObject();
		System.out.println(test05VisualRecognition.main(arg));
	}
	
	public static JsonObject main(JsonObject args) {
		
		JsonParser parser = new JsonParser(); 
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		
		//Get from watson_cred
        JsonObject cred = new JsonParser().parse(System.getProperty("env")).getAsJsonObject()
        		.getAsJsonArray("watson_vision_combined").get(0).getAsJsonObject().getAsJsonObject("credentials");
        service.setApiKey(cred.getAsJsonPrimitive("api_key").getAsString()); 
	
        File fileA = new File("test.jpg"); 
	    URL url = null;
		try {
			url = new URL(args.get("url").getAsString());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "NING/1.0");
		    FileUtils.copyInputStreamToFile(conn.getInputStream(), fileA);
		} catch (IOException e) {
			e.printStackTrace();
		}     
	    VisualRecognitionOptions voptoins = new VisualRecognitionOptions.Builder().images(fileA).build();
		DetectedFaces result = service.detectFaces(voptoins).execute();
		
        JsonObject response = parser.parse(result.toString()).getAsJsonObject();
        
        return response;
	}
}