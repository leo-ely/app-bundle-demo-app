package com.prototype.dynamicfeature2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.snackbar.Snackbar;
import com.prototype.dynamicfeature2.utils.NotaFiscal;

import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Scanner extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static String DEFAULT_URL =
            "https://www.sefaz.rs.gov.br/ASP/AAE_ROOT/NFE/SAT-WEB-NFE-NFC_QRCODE_1.asp?p=";

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;

    private NotaFiscal nota;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }

        cameraView = findViewById(R.id.camera_view);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission
                            (Scanner.this, Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    getContents(barcodes.valueAt(0).displayValue);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
        barcodeDetector.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        } else {
            Snackbar.make(findViewById(R.id.activity_scanner),
                    com.prototype.appbundle.R.string.snack_scanner_permission_error,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    public void getContents(String url) {
        String[] site = url.split("=");

        if (site.length >= 2) {
            Request request = new Request.Builder()
                    .url(DEFAULT_URL + site[1])
                    .build();

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try (Response response = client.newCall(request).execute()) {
                        parseResponse(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    Intent data = new Intent();
                    data.putExtra("nota_fiscal", nota);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }.execute();
        }
    }

    private void parseResponse(String response) {
        nota = new NotaFiscal();
        List<String> data = Arrays.asList
                (Jsoup.clean(response, Whitelist.none()).split("( {2,})"));

        if (data.size() > 10 || !data.stream().anyMatch(s -> s.contains("200"))) {
            for (String s : data) {
                if (s.contains("Data de")) {
                    String[] dados = s.split(" ");
                    nota.setNumeroNota(Integer.valueOf(dados[2]));
                    nota.setSerieNota(Integer.valueOf(dados[4]));
                    nota.setDataEmissao(dados[8]);
                }

                if (StringUtil.isNumeric(s.replaceAll(" ", ""))) {
                    nota.setChaveNota(s.replaceAll(" ", ""));
                }

                if (s.startsWith("Valor total")) {
                    String[] dados = s.split(" ");
                    nota.setValorNota(Float.valueOf(dados[3].replaceAll(",", ".")));
                }
            }
        }
    }

}
