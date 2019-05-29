package edu.hun.restfulapi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

public class HttpURLRequest {
	/*
	 * GET Request
	 */
	public static String doGet(String targetURL)
	{
		String result = null;
		try {
			URL restServiceURL = new URL(targetURL);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if (httpConnection.getResponseCode() != 200){
                   return null;
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                  (httpConnection.getInputStream())));
            String output = responseBuffer.readLine();
            result = output;
            while ((output = responseBuffer.readLine()) != null) {
                   result = result.concat(output);
            }
            httpConnection.disconnect();
       } catch (MalformedURLException e) {
            return null;
       } catch (IOException e) {
            return null;
       }
		return result;
	}
	/*
	 * POST Request
	 */
	public static int doPOST(String targetURL, String input)
	{
		try{
		    URL targetUrl = new URL(targetURL);
		    HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
		    httpConnection.setDoOutput(true);
		    httpConnection.setRequestMethod("POST");
		    httpConnection.setRequestProperty("Content-Type", "application/json");
		    OutputStream outputStream = httpConnection.getOutputStream();
		    outputStream.write(input.getBytes());
		    outputStream.flush();
		    if (httpConnection.getResponseCode() != 200) {
		         return -1;
		    }
		    httpConnection.disconnect();
		    }catch(MalformedURLException e){	  
		       return -1;
		    }catch(IOException e){
		       return -1;
		    }
		 return 1;
	}
	/*
	 * GET Request
	 */
	public static int doDelete(String targetURL, String input)
	{
		try {
			ApacheHttpClient client = ApacheHttpClient.create();
			URI uri = new URI(targetURL);
			WebResource r = client.resource(uri);
			ClientResponse response = 
			    r.accept(MediaType.APPLICATION_XML_TYPE,MediaType.TEXT_PLAIN_TYPE)
			    .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).delete(ClientResponse.class, input);
			System.out.println(response.getEntity(String.class));
			client.destroy();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
		return 1;
	}
}