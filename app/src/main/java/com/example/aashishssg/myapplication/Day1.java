package com.example.aashishssg.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aashishssg.myapplication.models.TempModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;


public class Day1 extends Fragment {
    JSONTask k;
    HashMap<String, TempModel> data;
    CharSequence sT = null;
    CharSequence lT = null;
    String iconString = null;
    String description = null;
    ImageView icon;
    TextView smallText;
    TextView largeText;
    TextView AreaText;
    View view;
    TempModel temp;
    private String city = "Chicago";
    private boolean created = false;
    private String unit = "metric";
    public boolean cityChanged = false;
    private String locationArea;



    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setCity(String city) {
        cityChanged = true;
        this.city = city;
    }

    public TextView getSmallText() {
        return smallText;
    }

    public TextView getLargeText() {
        return largeText;
    }

    public HashMap<String, TempModel> obtainData(){
        return data;
    }
    boolean state = false;

    public Day1() {
        // Required empty public constructor
        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.getString("location","Chicago");
        preferences.getBoolean("imperial", false);
        preferences.getBoolean("metric", true);*/
        acquireData();
    }

    public boolean isCreated() {
        return created;
    }

    public synchronized void acquireData(){
        k =new JSONTask(); ///CURRENT WEATHER

        city = city.replace(" ", "");

        StringBuffer k1 = new StringBuffer();
        k1.append("http://api.openweathermap.org/data/2.5/weather?q=");
        k1.append(city);
        k1.append("&APPID=3b95327ed692418ceb789a06365d5adc&units=");
        k1.append(unit);


        String url1= "http://api.openweathermap.org/data/2.5/weather?q="+city.toString()+"&APPID=3b95327ed692418ceb789a06365d5adc&units=metric";
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric";
        System.out.println(url.equals("http://api.openweathermap.org/data/2.5/weather?q="+city.toString()+"&APPID=3b95327ed692418ceb789a06365d5adc&units=metric"));
        k.execute(k1.toString()); ///CURRENT WEATHER;
        try {
            k.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        data = k.getData();
    }
    public Day1 getThis(){
        return this;
    }

    @Override
    public synchronized void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            lT=null;
            sT=null;
            iconString = null;
            description = null;
            locationArea = null;
        }
        else{
            lT = savedInstanceState.getCharSequence("actualtemp");
            sT = savedInstanceState.getCharSequence("info");
            iconString = savedInstanceState.getString("icon");
            description = savedInstanceState.getString("desc");
            locationArea = savedInstanceState.getString("locate");
        }
    }

    @Override
    public synchronized void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if(unit.equals("metric")){
            outState.putCharSequence("actualtemp", temp.getTemp() + " °C\n"+temp.getDescription() );
            outState.putCharSequence("info", "Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "\t\t\tHumidity: " + temp.getHumidity() + "%\n" + "\t\tPressure: " + temp.getPressure());
        }

        else if(unit.equals("imperial")){
            outState.putCharSequence("actualtemp", temp.getTemp() + " °F\n"+temp.getDescription() );
            outState.putCharSequence("info", "Temperature Max: " + temp.getTempmax() + "°F\n" + " Temperature Min: " + temp.getTempmin() + "°F\n" + "\t\t\tHumidity: " + temp.getHumidity() + "%\n" + "\t\tPressure: " + temp.getPressure());
        }

        outState.putString("icon", temp.getIcon());
        outState.putString("desc", temp.getDescription());
        outState.putString("locate", temp.getLocation());
    }

    @Override
    public synchronized View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_day1, null);
        largeText = (TextView) v.findViewById(R.id.bigtextPage1);
        smallText = (TextView) v.findViewById(R.id.textPage1);
        icon = (ImageView) v.findViewById(R.id.imagePage1);
        AreaText = (TextView) v.findViewById(R.id.locationPage1);
        created = true;
        view = v;

        if(cityChanged){
            refreshSystem();
            largeText.setTextSize(40);
        }

        if(lT==null && sT==null) {
            Calendar today = Calendar.getInstance();

            temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));

            int tempInt = temp.getTemp();

            String uri = "@drawable/d"+temp.getIcon();  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
            @SuppressWarnings("deprecation")
            Drawable res = getResources().getDrawable(imageResource);
            icon.setImageDrawable(res);

            if(unit.equals("metric")){
                largeText.setText(tempInt + " °C\n"+temp.getDescription());
                largeText.setTextSize(40);
                smallText.setText("Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
                AreaText.setText(temp.getLocation());
            }
            else if(unit.equals("imperial")){
                largeText.setText(tempInt + " °F\n"+temp.getDescription());
                largeText.setTextSize(40);
                smallText.setText("Temperature Max: " + temp.getTempmax() + "°F\n" + " Temperature Min: " + temp.getTempmin() + "°F\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
                AreaText.setText(temp.getLocation());
            }
        } else {
            largeText.setText(lT);
            largeText.setTextSize(40);
            smallText.setText(sT);
            AreaText.setText(locationArea);

            String uri = "@drawable/d"+iconString;  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
            @SuppressWarnings("deprecation")
            Drawable res = getResources().getDrawable(imageResource);
            icon.setImageDrawable(res);
        }
        return v;
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

    public void refreshSystem(){
        largeText.setText("");
        smallText.setText("");

        StringBuffer k1 = new StringBuffer();
        k1.append("http://api.openweathermap.org/data/2.5/weather?q=");
        k1.append(city);
        k1.append("&APPID=3b95327ed692418ceb789a06365d5adc&units=");
        k1.append(unit);

        acquireData();

        data = k.getData();

        Calendar today = Calendar.getInstance();

        temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));

        if(temp==null){
            city = "Chicago";
            acquireData();
            temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));
        }

        if(unit.equals("metric")){
            lT=(temp.getTemp() + " °C\n"+temp.getDescription());
            sT=("Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());}
        else if(unit.equals("imperial")){
            lT=(temp.getTemp() + " °F\n"+temp.getDescription());
            sT=("Temperature Max: " + temp.getTempmax() + "°F\n" + " Temperature Min: " + temp.getTempmin() + "°F\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
        }

        //lT = temp.getTemp() + "°C\n"+temp.getDescription();
        //sT = "Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "\t\t\tHumidity: " + temp.getHumidity() + "%\n" + "\t\tPressure: " + temp.getPressure();
        iconString = temp.getIcon();
        locationArea = temp.getLocation();

        largeText.setText(lT);
        smallText.setText(sT);
        AreaText.setText(locationArea);

        String uri = "@drawable/d"+temp.getIcon();  // where myresource (without the extension) is the file

        int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
        @SuppressWarnings("deprecation")
        Drawable res = getResources().getDrawable(imageResource);
        icon.setImageDrawable(res);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        int id = menu.getItemId();
        switch (id){

            case R.id.refresh:
                largeText.setText("");
                smallText.setText("");

                acquireData();

                data = k.getData();

                Calendar today = Calendar.getInstance();

                temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));

                if(temp==null){
                    city = "Chicago";
                    acquireData();
                    temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));
                }

                if(unit.equals("metric")){
                    lT=(temp.getTemp() + " °C\n"+temp.getDescription());
                    sT=("Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());}
                else if(unit.equals("imperial")){
                    lT=(temp.getTemp() + " °F\n"+temp.getDescription());
                    sT=("Temperature Max: " + temp.getTempmax() + "°F\n" + " Temperature Min: " + temp.getTempmin() + "°F\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
                }

                iconString = temp.getIcon();
                locationArea = temp.getLocation();

                largeText.setText(lT);
                smallText.setText(sT);
                AreaText.setText(locationArea);

                String uri = "@drawable/d"+temp.getIcon();  // where myresource (without the extension) is the file

                int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
                @SuppressWarnings("deprecation")
                Drawable res = getResources().getDrawable(imageResource);
                icon.setImageDrawable(res);

                System.out.println("This has been selected");
                break;
            case R.id.differentsettings:
                break;
            }
        return super.onOptionsItemSelected(menu);
    }
}
