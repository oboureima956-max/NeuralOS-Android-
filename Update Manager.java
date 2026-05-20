package com.neuralos;

import android.app.DownloadManager;
import android.content.*;
import android.net.Uri;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.File;

public class UpdateManager {

    Context context;
    String apkUrl = "";
    int currentVersion = 1;

    public UpdateManager(Context ctx) {
        this.context = ctx;
    }

    public void checkForUpdate(Button btn) {

        new Thread(() -> {
            try {
                URL url = new URL("https://ton-serveur.com/version.json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                InputStream input = conn.getInputStream();
                String json = new Scanner(input).useDelimiter("\\A").next();

                JSONObject obj = new JSONObject(json);

                int latest = obj.getInt("version");
                apkUrl = obj.getString("apk_url");

                ((MainActivity) context).runOnUiThread(() -> {
                    if (latest > currentVersion) {
                        btn.setEnabled(true);
                        btn.setText("Mise à jour disponible 🚀");
                    } else {
                        btn.setEnabled(false);
                        btn.setText("App à jour ✅");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startUpdate() {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setTitle("Mise à jour NeuralOS");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "neuralos.apk");

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = manager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (downloadId == id) {
                    installApk();
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Toast.makeText(context, "Téléchargement lancé", Toast.LENGTH_SHORT).show();
    }

    private void installApk() {

        File file = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "neuralos.apk"
        );

        Uri uri = FileProvider.getUriForFile(
            context,
            "com.neuralos.provider",
            file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}