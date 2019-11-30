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

    // Request codes for module installation
    private static final int REQUEST_CODE = 101;
    private static int sessionId = 0;

    // Module manager
    private static SplitInstallManager manager;
    private static Listener listener;
    private static SplitInstallRequest request;
    private static List<String> dynamicModules;
    private static Set<String> installedModules;

    // Modules
    private static String module1;
    private static String module2;
    private static List<FrameLayout> frameList;
    private static List<TextView> textList;
    private static List<Button> buttonList;
    private static AlertDialog alert; // Alert for confirmation dialog
    // FrameLayouts
    private FrameLayout fl_module_1;
    private FrameLayout fl_module_2;
    // TextViews
    private TextView tv_module_1_status;
    private TextView tv_module_2_status;
    // Buttons
    private Button bt_module_1;
    private Button bt_module_2;
    // Progress Bar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = SplitInstallManagerFactory.create(this); // Manager for dynamic modules
        dynamicModules = new ArrayList<>();
        dynamicModules = getAllModules(dynamicModules); // Get all modules in app

        initLayout(); // Set app layout
        createButtonListeners(); // Initialize button listeners
    }

    @Override
    protected void onResume() {
        setFrameBackgrounds(frameList);
        manager.registerListener(listener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        manager.unregisterListener(listener);
        super.onPause();
    }

    private void initLayout() {
        // Enable dark mode in app
        AppCompatDelegate.setDefaultNightMode
                (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Instantiate frame layouts
        frameList = initFrameLayouts();
        setFrameBackgrounds(frameList);

        // Instantiate status labels
        textList = initTextViews();
        updateTextViews(textList);

        // Instantiate buttons
        buttonList = initButtons();
        updateButtons(buttonList);

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

    private List<TextView> initTextViews() {
        tv_module_1_status = findViewById(R.id.tv_module_1_status);
        tv_module_2_status = findViewById(R.id.tv_module_2_status);

        List<TextView> list = new ArrayList<>();
        list.add(tv_module_1_status);
        list.add(tv_module_2_status);

        return list;
    }

    private void updateTextViews(List<TextView> list) {
        list.forEach(t -> {
            if (verifyModuleInstallations(dynamicModules))
                t.setText(R.string.module_status_installed);
            else
                t.setText(R.string.module_status_not_installed);
        });
    }

    private List<Button> initButtons() {
        bt_module_1 = findViewById(R.id.bt_module_1);
        bt_module_2 = findViewById(R.id.bt_module_2);

        List<Button> list = new ArrayList<>();
        list.add(bt_module_1);
        list.add(bt_module_2);

        return list;
    }

    private void updateButtons(List<Button> list) {
        list.forEach(b -> {
            if (verifyModuleInstallations(dynamicModules))
                b.setText(R.string.button_open_module);
            else
                b.setText(R.string.button_install_module);
        });
    }

    private boolean verifyModuleInstallations(List<String> list) {
        installedModules = manager.getInstalledModules();

        for (String installedModule : installedModules) {
            for (String module : list) {
                return module.contains(installedModule);
            }
        }

        return false;
    }

    private List<String> getAllModules(List<String> modules) {
        modules.add(getString(R.string.package_module_1));
        modules.add(getString(R.string.package_module_2));

        return modules;
    }

    private boolean getModuleInstallation(String moduleName) {
        return manager.getInstalledModules().contains(moduleName);
    }

    private void createButtonListeners() {
        module1 = getString(R.string.package_module_1);
        module2 = getString(R.string.package_module_2);

        bt_module_1.setOnClickListener(view -> {
            if (getModuleInstallation(module1))
                createIntent(module1);
            else
                installModule(module1);
        });

        bt_module_2.setOnClickListener(view -> {
            if (getModuleInstallation(module2))
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
        // Build module request for installation
        request = SplitInstallRequest.newBuilder().addModule(moduleName).build();

        manager.startInstall(request)
                .addOnSuccessListener(session -> {
                    sessionId = session;
                })
                .addOnFailureListener(exception -> {
                    switch (((SplitInstallException) exception).getErrorCode()) {
                        case SplitInstallErrorCode.NETWORK_ERROR:
                            // Network error (connection)
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_network_error),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case SplitInstallErrorCode.MODULE_UNAVAILABLE:
                            // Module unavailable
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_module_unavailable),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case SplitInstallErrorCode.INSUFFICIENT_STORAGE:
                            // Insufficient storage space
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.toast_insufficient_storage),
                                    Toast.LENGTH_LONG).show();
                            break;

                        case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
                            // Active sessions exceeded (another download is in progress)
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
                        // Ask for user confirmation (module exceeding 10MB in size)
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
                        // Display download status
                        displayDownloadStatus(state);
                        break;

                    case SplitInstallSessionStatus.INSTALLING:
                        // Display installation status
                        displayDownloadStatus(state);
                        break;

                    case SplitInstallSessionStatus.INSTALLED:
                        // Module successfully installed
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
