package ml.iks.md.models;

import javax.persistence.Id;
import java.util.Date;

public class Notification {

    @Id
    private Long id;
    private Date date;
    private String title;
    private String content;
    private int level;
}
