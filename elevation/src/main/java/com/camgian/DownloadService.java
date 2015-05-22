package com.camgian;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class DownloadService extends IntentService {
    private static final String ACTION_DOWNLOAD = "com.camgian.action.DOWNLOAD_FILE";

    private static final String EXTRA_URL       = "com.camgian.extra.url";
    private static final String EXTRA_FILENAME  = "com.camgian.extra.filename";

    public static final int DOWNLOAD_COMPLETE_OK    = 0;
    public static final int DOWNLOAD_COMPLETE_ERROR = 1;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startDownload(Context context, ResultReceiver receiver, String url, String filename) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_FILENAME, filename);
        intent.putExtra("receiver", receiver);
        context.startService(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            ResultReceiver receiver = intent.getParcelableExtra("receiver");

            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                handleDownload(
                        intent.getStringExtra(EXTRA_URL),
                        intent.getStringExtra(EXTRA_FILENAME),
                        receiver);
            }
        }
    }

    /**
     * Handle download in the provided background thread with the provided
     * parameters.
     */
    private void handleDownload(String urlToDownload, String filename, ResultReceiver receiver) {
        OutputStream output = null;
        InputStream input = null;

        boolean successful = false;

        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();

            //int fileLength = connection.getContentLength();

            // download the file
            input = new BufferedInputStream(connection.getInputStream());
            output = new FileOutputStream(filename);

            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            successful = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Maybe replace this clunky code with a Java 7 "try with resources"
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bundle resultData = new Bundle();
        resultData.putString("filename", filename);
        if (successful) {
            receiver.send(DOWNLOAD_COMPLETE_OK, resultData);
        } else {
            receiver.send(DOWNLOAD_COMPLETE_ERROR, resultData);
        }
    }
}
