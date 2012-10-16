package studip.app.db;

public class ColorManager {
	
	private static ColorManager instance;

	public static final int COLOR_LIGHT = 1;
	public static final int COLOR_DARK = 2;
	public static final int COLOR_BACKGROUND = 3;	
	
	private String light = "#899ab9";
	private String dark = "#34578c";
	private String background = "#e1e4e9";
	
	private ColorManager() {
		/*for (Object obj : DatabaseHandler.instance.getAllObjects(DatabaseHandler.TABLE_COLORS)) {
			Color color = (Course) obj;
			courses.put(course.course_id, course);
		}*/
	}

	public static ColorManager getInstance() {
		if (instance == null)
			return instance = new ColorManager();
		return instance;
	}
	
	public String getColor(int color) {
		switch(color) {
		case 1: return light;
		case 2: return dark;
		default: return background;
		}
	}
}
