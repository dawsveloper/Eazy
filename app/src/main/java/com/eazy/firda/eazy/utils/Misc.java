package com.eazy.firda.eazy.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by firda on 4/13/2018.
 */

public class Misc {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public static void copyFile(String src, String dst) {
        File sourceFile = new File(src);

        File destFile = new File(dst);

		/* verify whether file exist in source location */
        if (!sourceFile.exists()) {
            System.out.println("Source File Not Found!");
        }

		/* if file not exist then create one */
        if (!destFile.exists()) {
            try {
                destFile.createNewFile();

                System.out.println("Destination file doesn't exist. Creating one!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {

            /**
             * getChannel() returns unique FileChannel object associated a file
             * output stream.
             */
            source = new FileInputStream(sourceFile).getChannel();

            destination = new FileOutputStream(destFile).getChannel();

            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MyTransferListener implements FTPDataTransferListener {
        public void started() {
//            Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
            Log.v("upload message", " Upload started");
        }

        @Override
        public void transferred(int length) {
//            Toast.makeText(getBaseContext(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
            Log.v("upload message", " transferred ..." + String.valueOf(length));
        }

        @Override
        public void completed() {
//            Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
            Log.v("upload message", " completed ..");
        }

        @Override
        public void aborted() {
//            Toast.makeText(getBaseContext()," transfer aborted , please try again...", Toast.LENGTH_SHORT).show();
            Log.v("upload message", " transfer aborted , please try again...");
        }

        @Override
        public void failed() {
            System.out.println(" failed ..." );
        }
    }
}
