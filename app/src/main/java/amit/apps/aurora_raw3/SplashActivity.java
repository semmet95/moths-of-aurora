package amit.apps.aurora_raw3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class SplashActivity extends AppCompatActivity implements Serializable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent=new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("caller", this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 0);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }
}
