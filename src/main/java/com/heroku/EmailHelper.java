package com.heroku;


import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class EmailHelper {

    public static void sendEmailViaMailGun(String senderEmail, String receiverEmail, String subject, String body) throws IOException, URISyntaxException, EncoderException {

        if (System.getenv("MAILGUN_API_KEY") == null) {
            throw new RuntimeException("MAILGUN_API_KEY environment variable not found");
        }

        if (System.getenv("MAILGUN_SMTP_LOGIN") == null) {
            throw new RuntimeException("MAILGUN_SMTP_LOGIN environment variable not found");
        }
        
        String mailGunApp = System.getenv("MAILGUN_SMTP_LOGIN").split("@")[1];

        DefaultHttpClient httpClient = new DefaultHttpClient();
        
        List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
        formPairs.add(new BasicNameValuePair("from", senderEmail));
        formPairs.add(new BasicNameValuePair("to", receiverEmail));
        formPairs.add(new BasicNameValuePair("subject", subject));
        formPairs.add(new BasicNameValuePair("text", body));

        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formPairs);

        HttpPost httpPost = new HttpPost("https://api.mailgun.net/v2/" + mailGunApp + "/messages");
        httpPost.setEntity(urlEncodedFormEntity);

        String encoding = new String(Base64.encodeBase64(StringUtils.getBytesUtf8("api:" + System.getenv("MAILGUN_API_KEY"))));
        httpPost.setHeader("Authorization", "Basic " + encoding);

        HttpResponse response = httpClient.execute(httpPost);
    }

}
