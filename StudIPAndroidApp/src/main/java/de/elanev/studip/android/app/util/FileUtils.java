/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.util;

import java.util.HashMap;

import de.elanev.studip.android.app.R;

/**
 * Utility class with methods for working with documents and files
 * Created by joern on 11.11.13.
 */
public class FileUtils {

    // Static field containing all file type which are supported by Stud.IP
    private static final HashMap<String, Integer> sMimeTypeMap;

    // Statically initialize the mime type map
    static {
        sMimeTypeMap = new HashMap<String, Integer>();

        // archive types
        sMimeTypeMap.put("application/x-gzip", R.drawable.ic_file_archive);
        sMimeTypeMap.put("application/x-bzip2", R.drawable.ic_file_archive);
        sMimeTypeMap.put("application/zip", R.drawable.ic_file_archive);

        // document types
        sMimeTypeMap.put("text/plain", R.drawable.ic_file_text);
        sMimeTypeMap.put("text/css", R.drawable.ic_file_text);
        sMimeTypeMap.put("text/csv", R.drawable.ic_file_text);
        sMimeTypeMap.put("application/rtf", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/pdf", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/msword", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/ms-excel", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/ms-powerpoint", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/x-shockwave-flash", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.oasis.opendocument.presentation", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.oasis.opendocument.spreadsheet", R.drawable.ic_file_office);
        sMimeTypeMap.put("application/vnd.oasis.opendocument.text", R.drawable.ic_file_office);

        // image types
        sMimeTypeMap.put("image/gif", R.drawable.ic_file_picture);
        sMimeTypeMap.put("image/jpeg", R.drawable.ic_file_picture);
        sMimeTypeMap.put("image/png", R.drawable.ic_file_picture);
        sMimeTypeMap.put("image/x-ms-bmp", R.drawable.ic_file_picture);

        // audio types
        sMimeTypeMap.put("audio/mp3", R.drawable.ic_file_audio);
        sMimeTypeMap.put("audio/ogg", R.drawable.ic_file_audio);
        sMimeTypeMap.put("audio/wave", R.drawable.ic_file_audio);
        sMimeTypeMap.put("application/x-pn-realaudio", R.drawable.ic_file_audio);

        // video types
        sMimeTypeMap.put("video/mpeg", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/quicktime", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/x-msvideo", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/x-flv", R.drawable.ic_file_video);
        sMimeTypeMap.put("application/ogg", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/ogg", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/mp4", R.drawable.ic_file_video);
        sMimeTypeMap.put("video/webm", R.drawable.ic_file_video);
    }

    /**
     * Returns the drawable resource corresponding to the specified mime type String. If the mime
     * type is not officially supported it will return a generic icon.
     *
     * @param mimeType the mime type String
     * @return the corresponding drawable resource
     */
    public static int getResourceForMimeType(String mimeType) {
        if (sMimeTypeMap.containsKey(mimeType))
            return sMimeTypeMap.get(mimeType);
        else
            return R.drawable.ic_file_generic;
    }
}
