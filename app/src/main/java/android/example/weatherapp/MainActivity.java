
package android.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Declairing all the objects
    private ProgressBar loadingPB;
    private TextView cityNameTV,tempratureTV,conditionTV;
    private ImageView backIV,iconIV,searchIV;
    private RelativeLayout homeRL;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModel> weatherRVAdapterArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private TextInputEditText cityEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing all the objects
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        tempratureTV = findViewById(R.id.idTVTemprature);
        conditionTV = findViewById(R.id.idTVCondition);
        backIV = findViewById(R.id.idVIBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        homeRL = findViewById(R.id.idRLHome);
        weatherRV = findViewById(R.id.idRVWeather);
         cityEdt=findViewById(R.id.idEdtCity);
        //Setting array adapter to the recycle view
        weatherRVAdapterArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this,weatherRVAdapterArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //To check weather the internet and the location are turned ON, if any one of them is OFF then user will get a request to turn it ON.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        if (cityName == null) cityName = "Bengaluru";
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(v -> {
            String city = cityEdt.getText().toString();
            if(city.isEmpty()){
                Toast.makeText(MainActivity.this, "Please Enter A City Name", Toast.LENGTH_SHORT).show();
                city = null;
            }
            cityNameTV.setText(cityName);
            getWeatherInfo(city);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this,"Please Provide the Permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Returns a city name if both longitude and latitude are provided
    private String getCityName(double longitude, double latitude){
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr:addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) cityName=city;
                    else {
                        Log.d("TAG","City Not Found!!");
//                        Toast.makeText(this, "User City Not Found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    //Here the API is called by the city name and all the weather details are loaded into the app
    private void getWeatherInfo(String cityName){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=5ec313f52940406d9eb83610211410&q=+"+cityName+"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            weatherRVAdapterArrayList.clear();
            try {
                String temperature = response.getJSONObject("current").getString("temp_c");
                temperature+="°c";
                tempratureTV.setText(temperature);
                int isDay = response.getJSONObject("current").getInt("is_day");
                String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                Picasso.get().load("http//:".concat(conditionIcon)).into(iconIV);
                conditionTV.setText(condition);
                if(isDay==1){
                    Picasso.get().load("https://images.unsplash.com/photo-1520985244272-9d0b8067a4ff?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=387&q=80").into(backIV);
                }else {
                    Picasso.get().load("https://images.unsplash.com/photo-1536746803623-cef87080bfc8?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=385&q=80").into(backIV);
                }
              //  JSONObject responset = response.getJSONObject("forecast");
                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                JSONArray hourArray = forecastO.getJSONArray("hour");
                for(int i=0;i<hourArray.length();i++){
                    JSONObject hourObj = hourArray.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temp = hourObj.getString("temp_c");
                    String img = hourObj.getJSONObject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherRVAdapterArrayList.add(new WeatherRVModel(time,temp,img,wind));
                }
                weatherRVAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(MainActivity.this, "Please Enter A Valid City Name!!", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }
}