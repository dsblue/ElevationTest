package com.camgian.wms;


import android.os.AsyncTask;
import org.xml.sax.InputSource;

import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created by npalmer on 5/20/2015.
 */
public class WMSRequest {

    public static final String WMS_SERVER_NAME  = "data.worldwind.arc.nasa.gov";
    protected volatile ByteBuffer byteBuffer;
    protected volatile URLConnection connection;
    private int responseCode;
    private String responseMessage;
    protected volatile int contentLength = 0;
    protected volatile String contentType;

    public WMSRequest() {

        /*
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = null;

                    try {

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority(WMS_SERVER_NAME)
                                .appendPath("wms")
                                .appendQueryParameter("service", "wms")
                                .appendQueryParameter("request", "GetCapabilities");

                        url = new URL(builder.build().toString());
                        Environment.getExternalStorageDirectory();
                        connection = url.openConnection();
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);

                        HttpURLConnection htpc = (HttpURLConnection) connection;
                        responseCode = htpc.getResponseCode();
                        responseMessage = htpc.getResponseMessage();
                        contentType = connection.getContentType();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        */


        //String msg = Logging.getMessage("HTTPRetriever.ResponseInfo", this.responseCode, connection.getContentLength(),
        //        contentType != null ? contentType : "content type not returned", connection.getURL());
        //Logging.verbose(msg);

        /*
        if (this.responseCode == HttpURLConnection.HTTP_OK){
            this.contentLength = this.connection.getContentLength();

            ByteBuffer buffer;
            InputStream inputStream = null;
            try
            {
                inputStream = this.connection.getInputStream();
                if (inputStream == null)
                {
                    //Logging.error(Logging.getMessage("URLRetriever.InputStreamFromConnectionNull", connection.getURL()));
                    //return null;
                }

                // The legacy WW servers send data with application/zip as the content type, and the retrieval initiator is
                // expected to know what type the unzipped content is. This is a kludge, but we have to deal with it. So
                // automatically unzip the content if the content type is application/zip.
                this.contentType = connection.getContentType();
                if (this.contentType != null && this.contentType.equalsIgnoreCase("application/zip"))
                {
                    // Assume single file in zip and decompress it
                    //buffer = this.readZipStream(inputStream, connection.getURL());
                }
                else
                {
                    // The Content-Length header on Android does not seem to be a reliable indicator of the stream length.
                    // It looks like the Android connection classes are internally requesting content with gzip encoding,
                    // and transparently unzipping the content. However, the content-length refers to the compressed length.
                    // Treat the stream as if it did not have a content length header to make sure that we read the full
                    // contents.
                    //buffer = this.readNonSpecificStreamUnknownLength(inputStream);


                }
            }
            catch (Exception e) {

            }
            //finally
            //{
                //WWIO.closeStream(inputStream, connection.getURL().toString());
            //}
        }
        */

    }

    /*
    protected ByteBuffer readNonSpecificStreamUnknownLength(InputStream inputStream) throws IOException
    {
        final int pageSize = (int) Math.ceil(Math.pow(2, 15));

        ReadableByteChannel channel = Channels.newChannel(inputStream);
        ByteBuffer buffer = ByteBuffer.allocate(pageSize);

        int count = 0;
        int numBytesRead = 0;
        while (!this.interrupted() && count >= 0)
        {
            count = channel.read(buffer);
            if (count > 0)
                this.contentLengthRead.getAndAdd(numBytesRead += count);

            if (count > 0 && !buffer.hasRemaining())
            {
                ByteBuffer biggerBuffer = ByteBuffer.allocate(buffer.limit() + pageSize);
                biggerBuffer.put((ByteBuffer) buffer.rewind());
                buffer = biggerBuffer;
            }
        }

        if (buffer != null)
            buffer.flip();

        return buffer;
    }
    */
}

class RetrieveURLTask extends AsyncTask<String, Void, Integer> {

    private Exception exception;

    protected Integer doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            InputSource is = new InputSource(url.openStream());
            return 1;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(Integer i) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}