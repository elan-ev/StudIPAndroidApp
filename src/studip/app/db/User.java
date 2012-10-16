package studip.app.db;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class User implements Serializable, IDItem {
	
	public String user_id, username, perms, title_pre, forename, lastname, title_post, email, avatar_small, avatar_medium, avatar_normal, phone, homepage, privadr;

	public byte[] avatar_small_bytes, avatar_medium_bytes, avatar_normal_bytes;
	
	public static final int SMALL_IMAGE = 0, MEDIUM_IMAGE = 1, NORMAL_IMAGE = 2;
	
	public User(JSONObject json) {
        try { 		
			user_id = json.getString("user_id");
	        username = json.getString("username");
	        perms = json.getString("perms");
	        title_pre = json.getString("title_pre");
	        forename = json.getString("forename");
	        lastname = json.getString("lastname");
	        title_post = json.getString("title_post");
	        email = json.getString("email");
	        avatar_small = json.getString("avatar_small");
	        avatar_medium = json.getString("avatar_medium");
	        avatar_normal = json.getString("avatar_normal");
	        phone = json.getString("phone");
	        homepage = json.getString("homepage");
	        privadr = json.getString("privadr");
	        
	        avatar_small_bytes = getImageBytes(new URL(this.avatar_small));
	        avatar_medium_bytes = getImageBytes(new URL(this.avatar_medium));
	        avatar_normal_bytes = getImageBytes(new URL(this.avatar_normal));
	        
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Drawable getImage(int size) {
		byte[] image;
		switch (size) {
		default:
		case SMALL_IMAGE:
			image = avatar_small_bytes;
			break;
		case MEDIUM_IMAGE:
			image = avatar_medium_bytes;
			break;
		case NORMAL_IMAGE:
			image = avatar_normal_bytes;
			break;
		}

		ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
		Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

		return new BitmapDrawable(bitmap);
	}

	private byte[] getImageBytes(URL url) {
		try {
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
			Log.d("User Image ERR", "Error: " + e.toString());
			return null;
		}

	}
	
	public String getFullName() {
		return this.title_pre + " " + this.forename + " " + this.lastname + " " + this.title_post;
	}
	
	public String getName() {
		return this.forename + " " + this.lastname;
	}

	public String getID() {
		return user_id;
	}
}
