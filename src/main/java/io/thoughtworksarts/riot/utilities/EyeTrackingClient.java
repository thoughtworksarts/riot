package io.thoughtworksarts.riot.utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;

public class EyeTrackingClient {

    private HttpClient httpClient;
    private HttpGet httpGet;

    public EyeTrackingClient(HttpClient httpClient, HttpGet httpGet){

        this.httpClient = httpClient;
        this.httpGet = httpGet;
    }

    public int startEyeTracking() {
        try {

            HttpResponse response = this.httpClient.execute(this.httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode != 200) {
               throw new HttpResponseException(responseCode, "Status code is not 200");
            }
            return responseCode;

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e){
            e.printStackTrace();
        }

        return 0;
    }



}
