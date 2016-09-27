package com.example.aashishssg.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.aashishssg.myapplication.models.TempModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Aashish SSG on 4/22/2016.
 */
public class JSONTask extends AsyncTask<String, String, String> {
    private HashMap<String, TempModel> data = new HashMap<String, TempModel>();
    String City = "Chicago";
    String Unit = "metric";

    public boolean isApproved() {
        return approved;
    }

    private boolean approved = true;

    public void setCity(String city){
        city = this.City;
    }

    public String getCity(){
        return City;
    }

    public String getUnit(){
        return City;
    }

    public void setUnit(String unit){
        unit = this.Unit;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        //System.out.println(params[0]);

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buff = new StringBuffer();

            String line = "";

            while((line = reader.readLine())!=null){
                buff.append(line);
            }

            /////System.out.println(line);

            //http://api.openweathermap.org/data/2.5/weather\?q=[A-Za-z]+&APPID=3b95327ed692418ceb789a06365d5adc&units=metric
            String stringedJson = buff.toString();

            JSONObject parentObject = new JSONObject(stringedJson);

            if(Integer.parseInt(parentObject.getString("cod"))!=200){
                approved = false;
                System.out.print("GETS HERE");
                return "";
            }
            //System.out.println(stringedJson);

            if(params[0].matches("http://api.openweathermap.org/data/2.5/weather\\?q=.+&APPID=3b95327ed692418ceb789a06365d5adc&units=[a-zA-Z]+")){
                //System.out.print("GETS HERE..............................................");
                //JSONObject parentObject = new JSONObject(stringedJson);

                JSONObject locationArea = new JSONObject(parentObject.get("sys").toString());

                JSONArray parentArray = parentObject.getJSONArray("weather");

                JSONObject tempstats = parentObject.getJSONObject("main");
                TempModel firstPage = new TempModel();

                JSONObject weather = new JSONObject(parentObject.getJSONArray("weather").get(0).toString());

                firstPage.setDescription(weather.get("main").toString());
                firstPage.setIcon(weather.get("icon").toString());
                firstPage.setTemp(tempstats.getInt("temp"));
                firstPage.setHumidity(tempstats.getInt("humidity"));
                firstPage.setPressure(tempstats.getInt("pressure"));
                firstPage.setTempmax(tempstats.getInt("temp_max"));
                firstPage.setTempmin(tempstats.getInt("temp_min"));
                firstPage.setLocation(locationArea.getString("country").toString()+", "+parentObject.getString("name"));

                Calendar today = Calendar.getInstance();
                data.put(dayDecider(today.get(Calendar.DAY_OF_WEEK)), firstPage);
                //System.out.println(data.size());
            } else {
                //JSONObject parentObject = new JSONObject(stringedJson);

                System.out.println(params[0]);

                JSONObject locationArea = new JSONObject(parentObject.get("city").toString());


                JSONArray parentArray = parentObject.getJSONArray("list");
                for (int i = 0; i < parentArray.length(); i++) {

                    TempModel RemPages = new TempModel();

                    Calendar today = Calendar.getInstance();

                    JSONObject finalObject = parentArray.getJSONObject(i);

                    JSONObject weather = new JSONObject(finalObject.getJSONArray("weather").get(0).toString());

                    JSONObject tempstats = finalObject.getJSONObject("temp");

                    int AverageTemp = tempstats.getInt("day")+ tempstats.getInt("night")+ tempstats.getInt("eve") +tempstats.getInt("morn");

                    RemPages.setTemp(AverageTemp/4);
                    RemPages.setDescription(weather.get("main").toString());
                    RemPages.setIcon(weather.get("icon").toString());
                    RemPages.setHumidity(finalObject.getInt("humidity"));
                    RemPages.setPressure(finalObject.getInt("pressure"));
                    RemPages.setTempmax(tempstats.getInt("max"));
                    RemPages.setTempmin(tempstats.getInt("min"));
                    RemPages.setLocation(locationArea.getString("name")+", "+locationArea.getString("country"));

                    today.add(Calendar.DATE, i+1);

                    data.put(dayDecider(today.get(Calendar.DAY_OF_WEEK)), RemPages);
                }
            }
            return buff.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<String, TempModel> getData(){
        return data;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

    public String dayDecider(int i){
        if(i>7) i-=7;
        switch(i){
            case 1:
                return "SUN";
            case 2:
                return "MON";
            case 3:
                return "TUE";
            case 4:
                return "WED";
            case 5:
                return "THU";
            case 6:
                return "FRI";
            case 7:
                return "SAT";
        }
        return null;
    }


}