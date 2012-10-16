package studip.app.db;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class SemesterClass implements Serializable {

	public String id, name, compact_mode, workgroup_mode, only_inst_user, turnus_default, default_read_level, default_write_level, bereiche, show_browse, write_access_nobody, topic_create_autor, visible, course_creation_forbidden, overview, forum, admin, documents, schedule, participants, literature, chat, scm, wiki, resources, calendar, elearning_interface, description, create_description, studygroup_mode, title_dozent, title_dozent_plural, title_tutor, title_tutor_plural, title_autor, title_autor_plural;
	
	public Date mkdate, chdate;
	
	public HashMap<String, HashMap<String, Integer>> modules = new HashMap<String, HashMap<String, Integer>>();
	
	public SemesterClass(JSONObject json) {
		try {
			id = json.getString("id");
			name = json.getString("name");
			compact_mode = json.getString("compact_mode");
			workgroup_mode = json.getString("workgroup_mode");
			only_inst_user = json.getString("only_inst_user");
			turnus_default = json.getString("turnus_default");
			default_read_level = json.getString("default_read_level");
			default_write_level = json.getString("default_write_level");
			bereiche = json.getString("bereiche");
			show_browse = json.getString("show_browse");
			write_access_nobody = json.getString("write_access_nobody");
			topic_create_autor = json.getString("topic_create_autor");
			visible = json.getString("visible");
			course_creation_forbidden = json.getString("course_creation_forbidden");
			overview = json.getString("overview");
			forum = json.getString("forum");
			admin = json.getString("admin");
			documents = json.getString("documents");
			schedule = json.getString("schedule");
			participants = json.getString("participants");
			literature = json.getString("literature");
			chat = json.getString("chat");
			scm = json.getString("scm");
			wiki = json.getString("wiki");
			resources = json.getString("resources");
			calendar = json.getString("calendar");
			elearning_interface = json.getString("elearning_interface");
			
			JSONObject modObj = json.getJSONObject("modules");
			
			int activated = modObj.getJSONObject("CoreOverview").getInt("activated");
			int sticky = modObj.getJSONObject("CoreOverview").getInt("sticky");
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			map.put("activated", activated);
			map.put("sticky", sticky);
			modules.put("CoreOverview", map);
			
			activated = modObj.getJSONObject("CoreAdmin").getInt("activated");
			sticky = modObj.getJSONObject("CoreAdmin").getInt("sticky");
			map = new HashMap<String, Integer>();
			map.put("activated", activated);
			map.put("sticky", sticky);
			modules.put("CoreAdmin", map);
			
			description = json.getString("description");
			create_description = json.getString("create_description");
			studygroup_mode = json.getString("studygroup_mode");
			title_dozent = json.getString("title_dozent");
			title_dozent_plural = json.getString("title_dozent_plural");
			title_tutor = json.getString("title_tutor");
			title_tutor_plural = json.getString("title_tutor_plural");
			title_autor = json.getString("title_autor");
			title_autor_plural = json.getString("title_autor_plural");
	        mkdate = new Date(Long.parseLong(json.getString("mkdate")) * 1000L);
	        chdate = new Date(Long.parseLong(json.getString("chdate")) * 1000L);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
