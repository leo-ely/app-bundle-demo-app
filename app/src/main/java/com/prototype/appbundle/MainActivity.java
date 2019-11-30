package com.prototype.appbundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static int sessionId = 0;

    private static SplitInstallManager manager;
    private static Listener listener;
    private static SplitInstallRequest request;
    private static Set<String> installedModules;
    private static String module1;
    private static String module2;

    private static List<FrameLayout> frameList;
    private static AlertDialog alert;
    private FrameLayout fl_module_1;
    private FrameLayout fl_module_2;
    private TextView tv_module_1_status;
    private TextView tv_module_2_status;
    private Button bt_module_1;
    private Button bt_module_2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = SplitInstallManagerFactory.create(this);

        initLayout();
        createButtonListeners();
    }

    @Override
    protected void onResume() {
        setFrameBackgrounds(frameList);
        manager.registerListener(listener);
        updateTextViews();
        updateButtons();
        super.onResume();
    }

    @Override
    protected void onPause() {
        manager.unregisterListener(listener);
        super.onPause();
    }

    private void initLayout() {
        installedModules = manager.getInstalledModules();
        module1 = getString(R.string.package_module_1);
        module2 = getString(R.string.package_module_2);

        AppCompatDelegate.setDefaultNightMode
                (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        frameList = initFrameLayouts();
        setFrameBackgrounds(frameList);

        initTextViews();
        updateTextViews();

        initButtons();
        updateButtons();

        progressBar = findViewById(R.id.progressBar);
        listener = new Listener();
    }

    private List<FrameLayout> initFrameLayouts() {
        fl_module_1 = findViewById(R.id.fl_module_1);
        fl_module_2 = findViewById(R.id.fl_module_2);

        List<FrameLayout> list = new ArrayList<>();
        list.add(fl_module_1);
        list.add(fl_module_2);

        return list;
    }

    private void setFrameBackgrounds(List<FrameLayout> list) {
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            list.forEach(f -> {
                f.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorBackgroundCardDark));
            });
        } else {
            list.forEach(f -> {
                f.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.colorBackgroundCard));
            });
        }
    }

    private void initTextViews() {
        tv_module_1_status = findViewById(R.id.tv_module_1_status);
        tv_module_2_status = findViewById(R.id.tv_module_2_status);
    }

    private void updateTextViews() {
        tv_module_1_status.setText(isModuleInstalled(module1) ?
                R.string.module_status_installed :
                R.string.module_status_not_installed);
        tv_module_2_status.setText(isModuleInstalled(module2) ?
                R.string.module_status_installed :
                R.string.module_status_not_installed);
    }

    private void initButtons() {
        bt_module_1 = findViewById(R.id.bt_module_1);
        bt_module_2 = findViewById(R.id.bt_module_2);
    }

    private void updateButtons() {
        bt_module_1.setText(isModuleInstalled(module1) ?
                R.string.button_open_module :
                R.string.button_install_module);
        bt_module_2.setText(isModuleInstalled(module2) ?
                R.string.button_open_module :
                R.string.button_install_module);
    }

    private boolean isModuleInstalled(String moduleName) {
        return installedModules.contains(moduleName);
    }

    private void createButtonListeners() {
        bt_module_1.setOnClickListener(view -> {
            if (isModuleInstalled(module1))
                createIntent(module1);
            else
                installModule(module1);
        });

        bt_module_2.setOnClickListener(view -> {
            if (isModuleInstalled(module2))
                createIntent(module2);
            else
                installModule(module2);
        });
    }

    private void createIntent(String moduleName) {
        Intent i;

        try {
            i = new Intent(this,
                    Class.forName("com.prototype.dynamicfeature"
                            + moduleName.charAt(moduleName.length() - 1)
                            + ".Activity"));
            startActivity(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            startActivity(getIntent());
            Toast.makeText(getApplicationContext(),
                    getString(R.string.toast_intent_start_failed),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void installModule(String moduleName) {
        request = SplitInstallRequest.newBuilder().addModule(moduleName).build();

        manager.startInstall(request)
                .addOnSuccessListener(session -> {
                    sessionId = session;
                })
                .addOnFailureListener(exception -> {
                    switch (((SplitInstallException) exception).getErrorCode()) {
                        case SplitInstallErrorCode.NETWORK_ERROR:
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_network_error),
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SplitInstallErrorCode.MODULE_UNAVAILABLE:
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_module_unavailable),
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SplitInstallErrorCode.INSUFFICIENT_STORAGE:
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_insufficient_storage),
                                    Toast.LENGTH_LONG).show();
                            break;
                        case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_active_sessions_exceeded),
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                    manager.cancelInstall(sessionId);
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            sessionId = requestCode;
        } else {
            sessionId = 0;
            finish();
            startActivity(getIntent());
        }
    }

    private void displayDownloadStatus(SplitInstallSessionState state) {
        showProgressVisibility(true);
        progressBar.setMax((int) state.totalBytesToDownload());
        progressBar.setProgress((int) state.bytesDownloaded());
    }

    private void showProgressVisibility(boolean status) {
        if (status)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    public void createConfirmationDialog(String moduleName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.dialog_install_module));
        dialog.setPositiveButton(getString(R.string.dialog_button_yes),
                (dialogInterface, i) -> {
                    installModule(moduleName);
                    dialogInterface.dismiss();
                });
        dialog.setNegativeButton(getString(R.string.dialog_button_no),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        alert = dialog.create();
        alert.show();
    }

    class Listener implements SplitInstallStateUpdatedListener {
        @Override
        public void onStateUpdate(SplitInstallSessionState state) {
            for (String moduleName : state.moduleNames()) {
                if (state.status() == SplitInstallSessionStatus.FAILED
                        && state.errorCode() == SplitInstallErrorCode.SERVICE_DIED) {
                    createConfirmationDialog(moduleName);
                    return;
                }

                switch (state.status()) {
                    case SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION:
                        try {
                            startIntentSender(state.resolutionIntent().getIntentSender(),
                                    null, 0, 0, 0);
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_module_installing),
                                    Toast.LENGTH_SHORT).show();
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(moduleName, e.toString());
                        }
                        break;
                    case SplitInstallSessionStatus.DOWNLOADING:
                        displayDownloadStatus(state);
                        break;
                    case SplitInstallSessionStatus.INSTALLING:
                        displayDownloadStatus(state);
                        break;
                    case SplitInstallSessionStatus.INSTALLED:
                        showProgressVisibility(false);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_module_installed),
                                Toast.LENGTH_SHORT).show();
                        sessionId = 0;

                        finish();
                        startActivity(getIntent());
                        break;
                }
            }
        }
    }
}
