package studip.app.db;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import studip.app.net.OAuthConnector;

import android.util.Log;

public class Document implements Serializable, IDItem  {
	
	public String document_id, user_id, name, description, filename, filesize, downloads, mime_type, icon;
	
	public Date mkdate, chdate;
	
	public boolean protectedB;

	public Document(JSONObject json) {
		try {
			document_id = json.getString("document_id");
			user_id = json.getString("user_id");
			name = json.getString("name");
			description = json.getString("description");
			mkdate = new Date(Long.parseLong(json.getString("mkdate")));
			chdate = new Date(Long.parseLong(json.getString("chdate")));
			filename = json.getString("filename");
			filesize = json.getString("filesize");
			downloads = json.getString("downloads");
			mime_type = json.getString("mime_type");
			icon = json.getString("icon");
			protectedB = json.getBoolean("protected");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getDocumentBytes() {
		//TODO in Datenbank
		try {
			URL url = new URL(OAuthConnector.server.API_URL + "/documents" + document_id + "/download");
			URLConnection ucon = url.openConnection();

			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(500);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			return baf.toByteArray();
		} catch (Exception e) {
			Log.d("Document download ERR", "Error: " + e.toString());
			return null;
		}
	}
	
	public String getID() {
		return document_id;
	}
}
