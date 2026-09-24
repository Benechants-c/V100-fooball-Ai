package com.v100.footballai;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        TextView status = findViewById(R.id.status);
        status.setText("V100 Football AI - Ready");
    }
}
