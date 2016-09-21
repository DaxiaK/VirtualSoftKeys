package tw.com.daxia.virtualsoftkeys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tw.com.daxia.virtualsoftkeys.Service.ServiceFloating;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, ServiceFloating.class));
//        stopService(new Intent(MainActivity.this, ServiceFloating.class));

    }


}
