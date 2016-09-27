package com.example.aashishssg.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aashishssg.myapplication.models.TempModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String[] daysavailable = {"Monday","Tuesday","Wednesday","Thursday","Friday", "Saturday", "Sunday"};
    private HashMap<String, TempModel> data = new HashMap<String, TempModel>();
    public static String k = "";
    public Day1 firstDay;
    public Day2 secondDay;
    public Day3 thirdDay;
    public Day4 fourthDay;
    public Day5 fifthDay;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new JSONTask().execute("http://api.openweathermap.org/data/2.5/weather?q=Chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric"); ///CURRENT WEATHER
        //new JSONTask().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric&cnt=6"); //16 DAY WEATHER

        System.out.println(data.size());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Calendar today = Calendar.getInstance();

        firstDay = new Day1();
        secondDay = new Day2();
        thirdDay = new Day3();
        fourthDay = new Day4();
        fifthDay = new Day5();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        firstDay.setCity(pref.getString("location", "Chicago"));
        secondDay.setCity(pref.getString("location", "Chicago"));
        thirdDay.setCity(pref.getString("location", "Chicago"));
        fourthDay.setCity(pref.getString("location", "Chicago"));
        fifthDay.setCity(pref.getString("location", "Chicago"));

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //if("http://api.openweathermap.org/data/2.5/weather?q="+ pref.getString("location", "Chicago")+"&APPID=3b95327ed692418ceb789a06365d5adc&units=metric"){
                boolean metric = pref.getBoolean("metric", true);
                boolean imperial = pref.getBoolean("imperial", false);

                if (metric == true || (metric == true && imperial == true)) {

                    firstDay.setUnit("metric");
                    secondDay.setUnit("metric");
                    thirdDay.setUnit("metric");
                    fourthDay.setUnit("metric");
                    fifthDay.setUnit("metric");
                } else {
                    firstDay.setUnit("imperial");
                    secondDay.setUnit("imperial");
                    thirdDay.setUnit("imperial");
                    fourthDay.setUnit("imperial");
                    fifthDay.setUnit("imperial");
                }
                JSONTask task = new JSONTask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ pref.getString("location", "Chicago")+"&APPID=3b95327ed692418ceb789a06365d5adc&units=metric");
                try {
                    task.get();
                    System.out.println("THE TASK WAS APPROVED?: "+task.isApproved());
                    if(!task.isApproved()){
                        Toast.makeText(getApplicationContext(),"Error: City not found.", Toast.LENGTH_LONG).show();
                        pref.edit().putString("location", "Chicago");
                        firstDay.cityChanged = false;
                        secondDay.cityChanged = false;
                        thirdDay.cityChanged = false;
                        fourthDay.cityChanged = false;
                        fifthDay.cityChanged = false;

                    } else {
                        firstDay.setCity(pref.getString("location", "Chicago"));
                        secondDay.setCity(pref.getString("location", "Chicago"));
                        thirdDay.setCity(pref.getString("location", "Chicago"));
                        fourthDay.setCity(pref.getString("location", "Chicago"));
                        fifthDay.setCity(pref.getString("location", "Chicago"));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                //}

                if(firstDay.isCreated()) firstDay.onOptionsItemSelected(item);
                if(secondDay.isCreated()) secondDay.onOptionsItemSelected(item);
                if(thirdDay.isCreated()) thirdDay.onOptionsItemSelected(item);
                if(fourthDay.isCreated()) fourthDay.onOptionsItemSelected(item);
                if(fifthDay.isCreated()) fifthDay.onOptionsItemSelected(item);
                break;

            case R.id.differentsettings:

                /*if(firstDay.isCreated()) firstDay.refreshSystem();
                if(secondDay.isCreated()) secondDay.refreshSystem();
                if(thirdDay.isCreated()) thirdDay.refreshSystem();
                if(fourthDay.isCreated()) fourthDay.refreshSystem();
                if(fifthDay.isCreated()) fifthDay.refreshSystem();*/

                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                //if(secondDay.isCreated()) secondDay.onOptionsItemSelected(item);
                //if(thirdDay.isCreated()) thirdDay.onOptionsItemSelected(item);
                //if(fourthDay.isCreated()) fourthDay.onOptionsItemSelected(item);
                //if(fifthDay.isCreated()) fifthDay.onOptionsItemSelected(item);
                //System.out.println(d2);
                break;
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0)return firstDay;
            if(position == 1) return secondDay;
            if(position == 2) return thirdDay;
            if(position == 3) return fourthDay;
            if(position == 4) return fifthDay;

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar today = Calendar.getInstance();
            switch (position) {
                case 0:
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 1:
                    today.add(Calendar.DATE, 1);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 2:
                    today.add(Calendar.DATE, 2);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 3:
                    today.add(Calendar.DATE, 3);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 4:
                    today.add(Calendar.DATE, 4);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 5:
                    today.add(Calendar.DATE, 5);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
                case 6:
                    today.add(Calendar.DATE, 6);
                    return dayDecider(today.get(Calendar.DAY_OF_WEEK));
            }
            return null;
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

    /*public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

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

                String stringedJson = buff.toString();

                if(params[0]=="http://api.openweathermap.org/data/2.5/weather?q=Chicago&APPID=3b95327ed692418ceb789a06365d5adc&units=metric"){
                    JSONObject parentObject = new JSONObject(stringedJson);
                    JSONArray parentArray = parentObject.getJSONArray("weather");

                    JSONObject tempstats = parentObject.getJSONObject("main");
                    TempModel firstPage = new TempModel();

                    firstPage.setTemp(tempstats.getInt("temp"));
                    firstPage.setHumidity(tempstats.getInt("humidity"));
                    firstPage.setPressure(tempstats.getInt("pressure"));
                    firstPage.setTempmax(tempstats.getInt("temp_max"));
                    firstPage.setTempmin(tempstats.getInt("temp_min"));

                    Calendar today = Calendar.getInstance();
                    data.put(dayDecider(today.get(Calendar.DAY_OF_WEEK)), firstPage);
                }

                else {
                    JSONObject parentObject = new JSONObject(stringedJson);
                    JSONArray parentArray = parentObject.getJSONArray("list");
                    for (int i = 0; i < parentArray.length(); i++) {

                        TempModel RemPages = new TempModel();

                        Calendar today = Calendar.getInstance();

                        JSONObject finalObject = parentArray.getJSONObject(i);
                        JSONObject tempstats = finalObject.getJSONObject("temp");

                        int AverageTemp = tempstats.getInt("day")+ tempstats.getInt("night")+ tempstats.getInt("eve") +tempstats.getInt("morn");

                        RemPages.setTemp(AverageTemp/4);
                        RemPages.setHumidity(finalObject.getInt("humidity"));
                        RemPages.setPressure(finalObject.getInt("pressure"));
                        RemPages.setTempmax(tempstats.getInt("max"));
                        RemPages.setTempmin(tempstats.getInt("min"));

                        today.add(Calendar.DATE, i+1);

                        data.put(dayDecider(today.get(Calendar.DAY_OF_WEEK)), RemPages);
                        //System.out.println(data.size());
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

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            k = result;
            //System.out.println(k);
        }

    }*/
}
