package com.adagio.autotask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.adagio.autotask.databinding.ActivityMainBinding;
import com.adagio.autotask.service.FloatViewService;
import com.adagio.autotask.util.PermissionUtil;

//@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PermissionUtil.checkAccessibilityPermission(this)) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }

        if (!PermissionUtil.checkFloatPermission(this)) {
            PermissionUtil.requestSettingCanDrawOverlays(this);
        }

        // 电池优化
        if (!PermissionUtil.checkIgnoreBatteryOptimization(this)) {
            PermissionUtil.requestBatteryOptimization(this);
        }

        if (Settings.canDrawOverlays(MainActivity.this)) {
            Intent intent = new Intent(MainActivity.this, FloatViewService.class);
            Toast.makeText(MainActivity.this, "已开启悬浮窗", Toast.LENGTH_SHORT).show();
            startService(intent);
            finish();
        } else {
            //若没有权限，提示获取.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            Toast.makeText(MainActivity.this, "需要取得权限以使用悬浮窗", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}