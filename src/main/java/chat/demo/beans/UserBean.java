package chat.demo.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class UserBean {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private String pseudo;



    @DatabaseField
    private String password;


//constructor 1
    public UserBean(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
    }


//constructor 2
    public UserBean(Long id, String pseudo, String password) {
        this.id = id;
        this.pseudo = pseudo;
        this.password = password;
    }

//constructor 3
    public UserBean() {
    }


//___getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
