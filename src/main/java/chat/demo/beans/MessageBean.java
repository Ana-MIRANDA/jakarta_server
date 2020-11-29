package chat.demo.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// avec @DatabaseTable on va indiquer le nom de la table e @DatabaseField on defini chaque champ de la table
@DatabaseTable(tableName = "message")
public class MessageBean {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private String content;
    @DatabaseField(foreign = true)
    private UserBean user;

//constructor1
    public MessageBean(String content, UserBean user) {
        this.content = content;
        this.user = user;
    }

//constructor 2
    public MessageBean() {
    }

//Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
