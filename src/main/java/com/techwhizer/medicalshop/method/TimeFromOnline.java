package com.techwhizer.medicalshop.method;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
public class TimeFromOnline {
    final String TIME_API_URL = "https://www.timeapi.io/api/Time/current/coordinate?latitude=22.5726&longitude=88.3639";
    public boolean getCurrentDate() {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(TIME_API_URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity respEntity = response.getEntity();
            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);
                JSONObject jsonObject = new JSONObject(content);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String onlineDate = jsonObject.getString("date");
                String deviceDate = sdf.format(date);
                return Objects.equals(onlineDate, deviceDate);
            }else { return false; }
        } catch (IOException e) {
            return false;
        }
    }
}
