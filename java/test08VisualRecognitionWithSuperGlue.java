import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

public class test08VisualRecognitionWithSuperGlue {

	private final String htmlGistUrl = "https://gist.githubusercontent.com/snippet-java/fd3aa1c2ab893bf8e1bcd90073ceab99/raw";
	
	public String parameters = "{\"api_key\":\"\","
			+ "\"url\":\"https://www.whitehouse.gov/sites/whitehouse.gov/files/images/first-family/44_barack_obama%5B1%5D.jpg\"}";
	
	public static void main(String[] args) {
		test08VisualRecognitionWithSuperGlue vr = new test08VisualRecognitionWithSuperGlue();
        System.out.println(vr.process(vr.parameters));
	}
	
	public static JsonObject main(JsonObject args) throws IllegalArgumentException, IllegalAccessException {
		JsonObject response = new JsonObject();
		test08VisualRecognitionWithSuperGlue vr = new test08VisualRecognitionWithSuperGlue();
		if(args.getAsJsonPrimitive("__ow_method").getAsString().equalsIgnoreCase("get"))
			response.addProperty("html", vr.generateHTMLForm().toString());
		else 
			response.addProperty("output", vr.process(args.toString()).toString());
        
        return response;
	}
	
	private JsonObject process(String jsonString) {

		JsonParser parser = new JsonParser(); 
		JsonObject mybean = parser.parse(jsonString).getAsJsonObject();

		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		
		service.setApiKey(mybean.getAsJsonPrimitive("api_key").getAsString()); 
		
        File fileA = new File("test.jpg"); 
	    URL url = null;
		try {
			url = new URL(mybean.getAsJsonPrimitive("url").getAsString());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "NING/1.0");
		    FileUtils.copyInputStreamToFile(conn.getInputStream(), fileA);
		} catch (IOException e) {
			e.printStackTrace();
		}     
	    VisualRecognitionOptions voptoins = new VisualRecognitionOptions.Builder().images(fileA).build();
		DetectedFaces result = service.detectFaces(voptoins).execute();
		
        JsonObject output = parser.parse(result.toString()).getAsJsonObject();
        
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
