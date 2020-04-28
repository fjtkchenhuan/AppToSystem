package com.ys.guardpeopledaily;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {
    private CheckBox isGuardApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGuardApp = findViewById(R.id.guard);

        String packageName = Utils.getValueFromProp("persist.sys.guardApp");
        if ("com.simpleprezi.smartplayer".equals(packageName))
            isGuardApp.setChecked(true);

        isGuardApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Utils.setValueToProp("persist.sys.guardApp","com.simpleprezi.smartplayer");
                else
                    Utils.setValueToProp("persist.sys.guardApp","");
            }
        });
    }
}
