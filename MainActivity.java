package com.elder.v100;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class MainActivity extends Activity {
    TextView status, matches, output;
    @Override public void onCreate(Bundle b) { super.onCreate(b); setContentView(R.layout.activity_main);
        status=findViewById(R.id.status); matches=findViewById(R.id.matches); output=findViewById(R.id.output);
        findViewById(R.id.scan).setOnClickListener(v -> scan());
        findViewById(R.id.builder).setOnClickListener(v -> builder());
    }
    void scan(){ status.setText("Scanner complete • Demo fixtures loaded"); matches.setText("\nTODAY'S DEMO FIXTURES\n\nManchester United vs Tottenham\nArsenal vs Brighton\nBarcelona vs Sevilla\n\nSelect a fixture in the full-data version to run the V100 analysis engine."); }
    void builder(){ output.setText("\nBET BUILDER\n\nSAFE\n• Goals: Over 1.5\n• Team market: Demo selection\n• Risk: Lower model-risk profile\n\nBALANCED\n• Goals + corners + cards\n• Correlation checked\n\nHIGH ODDS\n• Multiple correlated markets\n\nCombined odds are calculated by multiplying individual decimal odds. Demo selections are illustrative only."); }
}
