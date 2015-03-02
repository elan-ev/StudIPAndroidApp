package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForumEntry {
  public static final String ID = ForumEntry.class.getName() + ".id";
  public static final String SUBJECT = ForumEntry.class.getName() + ".subject";
  public static final String CONTENT = ForumEntry.class.getName() + ".content";
  public static final String DATE = ForumEntry.class.getName() + ".date";
  public static final String USER = ForumEntry.class.getName() + ".user";

  @JsonProperty("chdate")
  public long chdate;
  @JsonProperty("anonymous")
  public int anonymous;
  @JsonProperty("children")
  public List<ForumEntry> children;
  @JsonProperty("content")
  public String content;
  @JsonProperty("content_html")
  public String contentHtml;
  @JsonProperty("mkdate")
  public long mkdate;
  @JsonProperty("seminar_id")
  public String seminarId;
  @JsonProperty("topic_id")
  public String topicId;
  @JsonProperty("subject")
  public String subject;
  @JsonProperty("depth")
  public int depth;
  @JsonProperty("new")
  public boolean isNew;
  @JsonProperty("new_children")
  public int newChildren;
  @JsonProperty("user_id")
  public String userId;

  public User user;
}
