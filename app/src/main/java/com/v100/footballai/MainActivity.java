package com.v100.footballai;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
public class MainActivity extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
TextView tv = new TextView(this);
tv.setText("V100 Football AI\nBUILD SUCCESS!\nNext step: Add AI logic");
tv.setTextSize(22);
tv.setGravity(Gravity.CENTER);
tv.setPadding(40, 200, 40, 40);
setContentView(tv);
}
}
