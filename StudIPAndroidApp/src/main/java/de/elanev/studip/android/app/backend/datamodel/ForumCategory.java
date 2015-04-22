package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumCategory {
  public static final String TITLE = ForumCategory.class.getName() + ".title";
  public static final String ID = ForumCategory.class.getName() + ".id";

  @JsonProperty("category_id")
  public String categoryId;
  @JsonProperty("entry_name")
  public String entryName;
  @JsonProperty("pos")
  public int pos;
  @JsonProperty("seminar_id")
  public String seminarId;
}
