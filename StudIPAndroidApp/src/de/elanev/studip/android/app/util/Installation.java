/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Helper class to enable identifying the specific app installation with an anonymous id.
 * <p/>
 * Created by joern on 31.10.13.
 */
public class Installation {
    private static final String INSTALLATION = "INSTALLATION";
    private static String sID = null;

    /**
     * Returns an unique identifier id for the specific app installation
     *
     * @param context execution context
     * @return the unique app installation identifier string
     */
    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                // if the app installation is new, write the new unique identifier into the
                // installation file in the apps private directory
                if (!installation.exists())
                    writeInstallationFile(installation);

                // read the identifier from the installation file in the apps private directory
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    /*
     * reads the installation identifier from the given file
     */
    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    /*
     * writes a new installation identifier to a file in the apps private directory
     */
    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}
