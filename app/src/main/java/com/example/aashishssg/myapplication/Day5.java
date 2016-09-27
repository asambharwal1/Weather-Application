package com.example.aashishssg.myapplication;


import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aashishssg.myapplication.models.TempModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Day5 extends Fragment {
    private JSONTask k;
    HashMap<String, TempModel> data;
    private TextView largeText;
    private TextView smallText;
    private CharSequence lT=null;
    private CharSequence sT=null;
    private ImageView icon;
    private TempModel temp;
    private String iconString=null;
    String description = null;
    private boolean created;
    private String city = "Chicago";
    private String unit = "metric";
    public boolean cityChanged;

    public boolean isCreated() {
        return created;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Day5() {

        acquireData();
        // Required empty public constructor
    }

    public void setCity(String city) {
        cityChanged = true;
        this.city = city;
    }

    public synchronized void acquireData(){
        k =new JSONTask(); ///CURRENT WEATHER

        city = city.replace(" ", "");

        StringBuffer k1 = new StringBuffer();
        k1.append("http://api.openweathermap.org/data/2.5/forecast/daily?q=");
        k1.append(city);
        k1.append("&APPID=3b95327ed692418ceb789a06365d5adc&units=");
        k1.append(unit);
        k1.append("&cnt=6");
        ////"http://api.openweathermap.org/data/2.5/forecast/daily?q=chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric&cnt=6"
        k.execute(k1.toString()); //16 DAY WEATHER
        //k.execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric&cnt=6"); //16 DAY WEATHER
        try {
            k.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        data = k.getData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            lT=null;
            sT=null;
            iconString = null;
            description = null;
        }
        else{
            lT = savedInstanceState.getCharSequence("actualtemp");
            sT = savedInstanceState.getCharSequence("info");
            iconString = savedInstanceState.getString("icon");
            description = savedInstanceState.getString("desc");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_day5, null);
        setHasOptionsMenu(true);
        System.out.println("THE SIZE IS " + data.size());
        largeText = (TextView) v.findViewById(R.id.bigtextPage5);
        smallText = (TextView) v.findViewById(R.id.textPage5);
        icon = (ImageView) v.findViewById(R.id.imagePage5);


        if(cityChanged){
            refreshSystem();
            largeText.setTextSize(40);
        }

        if (lT == null && sT == null) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.DATE, 4);
            temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));

            if(temp==null){
                city = "Chicago";
                acquireData();
                temp = data.get(dayDecider(today.get(Calendar.DAY_OF_WEEK)));
            }

            String uri = "@drawable/d" + temp.getIcon();  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
            @SuppressWarnings("deprecation")
            Drawable res = getResources().getDrawable(imageResource);
            icon.setImageDrawable(res);

            int tempInt = temp.getTemp();
            if (unit.equals("metric")) {
                largeText.setText(tempInt + " °C\n" + temp.getDescription());
                largeText.setTextSize(40);
                smallText.setText("Temperature Max: " + temp.getTempmax() + "°C\n" + " Temperature Min: " + temp.getTempmin() + "°C\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
            } else if (unit.equals("imperial")) {
                largeText.setText(tempInt + " °F\n" + temp.getDescription());
                largeText.setTextSize(40);
                smallText.setText("Temperature Max: " + temp.getTempmax() + "°F\n" + " Temperature Min: " + temp.getTempmin() + "°F\n" + "Humidity: " + temp.getHumidity() + "%\n" + "Pressure: " + temp.getPressure());
            }
        } else {
            largeText.setText(lT);
            largeText.setTextSize(40);
            smallText.setText(sT);

            String uri = "@drawable/d"+iconString;  // where myresource (without the extension) is the file

            int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
            @SuppressWarnings("deprecation")
            Drawable res = getResources().getDrawable(imageResource);
            icon.setImageDrawable(res);
        }
        created = true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menu){
        int id = menu.getItemId();
        switch (id){

            case R.id.refresh:
                largeText.setText("");
                smallText.setText("");

                acquireData();

                Calendar today = Calendar.getInstance();
                today.add(Calendar.DATE, 4);
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

                largeText.setText(lT);
                largeText.setTextSize(40);
                smallText.setText(sT);

                String uri = "@drawable/d"+iconString;  // where myresource (without the extension) is the file

                int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
                @SuppressWarnings("deprecation")
                Drawable res = getResources().getDrawable(imageResource);
                icon.setImageDrawable(res);
                break;

        }
        return super.onOptionsItemSelected(menu);
    }

    public void refreshSystem(){
        largeText.setText("");
        smallText.setText("");


        acquireData();

        //System.out.println("DAY 5 WAS MODIFIED BUT THE CITY: "+city+" AND THE UNIT MEASUREMENT WAS "+unit);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 4);

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

        largeText.setText(lT);
        smallText.setText(sT);

        String uri = "@drawable/d"+temp.getIcon();  // where myresource (without the extension) is the file

        int imageResource = getResources().getIdentifier(uri, null, getActivity().getPackageName());
        @SuppressWarnings("deprecation")
        Drawable res = getResources().getDrawable(imageResource);
        icon.setImageDrawable(res);

    }
}
