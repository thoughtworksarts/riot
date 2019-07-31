package io.thoughtworksarts.riot.utilities;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;


import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EyeTrackingClientTest {

    @Test
    void shouldCallStartEyeTrackingSuccessfully() throws IOException {



        HttpClient httpClient = mock(HttpClient.class);
        HttpGet httpGet = mock(HttpGet.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        //and:
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(httpGet)).thenReturn(httpResponse);

        //and:
        EyeTrackingClient client = new EyeTrackingClient(httpClient, httpGet);

        //when:
        int status = client.startEyeTracking();

        //then:

        //do a tru/e false
        Assert.assertEquals(200, status);



    }

}