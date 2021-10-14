package android.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ProgressBar lodingPB;
    private TextView cityNameTV,tempratureTV,conditionTV;
    private ImageView backIV,iconIV,searchIV;
    private RelativeLayout homeRL;
    private RecyclerView weatherRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lodingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        tempratureTV = findViewById(R.id.idTVTemprature);
        conditionTV = findViewById(R.id.idTVCondition);
        backIV = findViewById(R.id.idVIBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        homeRL = findViewById(R.id.idRLHome);
        weatherRV = findViewById(R.id.idRVWeather);


    }
}