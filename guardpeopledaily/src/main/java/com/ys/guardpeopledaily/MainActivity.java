package com.ys.guardpeopledaily;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckBox isGuardApp = findViewById(R.id.guard);
        final EditText etPassword = findViewById(R.id.launcher_password);
        final EditText etGuardTime = findViewById(R.id.guard_time);

        final String packageName = Utils.getValueFromProp("persist.sys.guardApp");
        if ("com.simpleprezi.smartplayer".equals(packageName))
            isGuardApp.setChecked(true);

        isGuardApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Utils.setValueToProp("persist.sys.guardApp", "com.simpleprezi.smartplayer");
                else
                    Utils.setValueToProp("persist.sys.guardApp", "");
            }
        });

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                if (!"".equals(password)) {
                    Utils.setValueToProp("persist.sys.enterPassword", password);
                    Toast.makeText(MainActivity.this, "成功设置密码" + password, Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.confirm1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guardTime = etGuardTime.getText().toString().trim();
                if (!"".equals(guardTime)) {
                    Utils.setValueToProp("persist.sys.guardTime", guardTime);
                    Toast.makeText(MainActivity.this, "成功设置进程守护时间" + guardTime + "s", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
