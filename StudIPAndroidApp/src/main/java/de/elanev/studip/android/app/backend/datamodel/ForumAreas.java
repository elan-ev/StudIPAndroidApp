package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumAreas {
  @JsonIgnoreProperties(ignoreUnknown = true) @JsonProperty("areas")
  public List<ForumArea> forumAreas;
}
