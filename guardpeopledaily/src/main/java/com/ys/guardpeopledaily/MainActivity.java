package com.ys.guardpeopledaily;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etPassword;
    private EditText etCurPassword;
    private static boolean isRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckBox isGuardApp = findViewById(R.id.guard);
        etPassword = findViewById(R.id.launcher_password);
        etCurPassword = findViewById(R.id.current_launcher_password);
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

        etCurPassword.addTextChangedListener(mTextWatcher);

        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRight) {
                    Toast.makeText(MainActivity.this,"请先验证当前密码！",Toast.LENGTH_LONG).show();
                    return;
                }
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

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = Utils.getValueFromProp("persist.sys.enterPassword");
            if (temp.length() == 6) {
                if (etCurPassword.getText().toString().equals(password)) {
                   isRight = true;
                   Toast.makeText(MainActivity.this,"当前密码输入正确，请设置新的密码",Toast.LENGTH_LONG).show();
                } else {
                   isRight = false;
                   Toast.makeText(MainActivity.this,"当前密码输入错误，请重新输入",Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isRight = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRight = false;
    }
}
