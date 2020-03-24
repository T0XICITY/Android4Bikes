package de.thu.tpro.android4bikes.activities.info;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class ActivityInfoMode extends AppCompatActivity {
    private TextView tv_Test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mode);
        GlobalContext.setContext(getApplicationContext());
        determineAllViews();
        HazardAlert hazardAlert = new HazardAlert(HazardAlert.HazardType.ICY_ROAD);
        tv_Test.setText(hazardAlert.getType());
        a();
        b();
    }

    private void determineAllViews() {
        tv_Test = findViewById(R.id.tv_Test);
    }

    private void a(){

    }

    private void b(){

    }
}
