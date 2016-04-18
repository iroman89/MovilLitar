package cl.softmedia.movillitar.http;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by iroman on 27/10/2015.
 */
public class Request {

    public JSONArray connect(String sConexion, HashMap<String, String> parameters) throws Exception {

        JSONArray json = null;
        String data = downloadHttp(new URL(sConexion), parameters);
        try {
            json = new JSONArray(data);
        } catch (Exception e) {
            throw new Exception(data.replace("\"", ""));
        }
        return json;
    }

    public String connectToString(String sConexion, HashMap<String, String> parameters) throws Exception {
        return downloadHttp(new URL(sConexion), parameters);
    }


    private String downloadHttp(URL url, HashMap<String, String> hashParam) throws Exception {

        String response = "";
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(60 * 1000);
        connection.setConnectTimeout(60 * 1000);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.connect();

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(hashParam));

        writer.flush();
        writer.close();
        os.close();
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
        }
        return response;
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        if (params == null)
            return "";
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(entry.getKey());//URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(entry.getValue());//URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
