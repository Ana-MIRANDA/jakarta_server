package chat.demo.dao;

import chat.demo.beans.UserBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {


    public static Dao<UserBean, Long> getDaoUser(JdbcConnectionSource jdbc) throws SQLException {
        return DaoManager.createDao(jdbc, UserBean.class);
    }

    // Creer user
    public static void createUpdateUser(UserBean user, JdbcConnectionSource jdbc) throws SQLException {
        getDaoUser(jdbc).createOrUpdate(user);
    }

    //Comparer users par id
    public static UserBean getUserById(Long unId, JdbcConnectionSource jdbc) throws SQLException {
        return getDaoUser(jdbc).queryForId(unId);
    }

    // Search user by idSession
    public static UserBean getUserByIdSession(String unIdSession, JdbcConnectionSource jdbc) throws Exception {

        List<UserBean> lista = getDaoUser(jdbc).queryForEq("idSession", unIdSession); //lista
        //verifier list = veifier sil y des users avec ce IdSession
        if (lista == null || lista.isEmpty()) {
            throw new Exception("idSession doesn't exist!");
        }
        return lista.get(0); //retourne luser que tiver o idSession enviado pelo client
    }


    // Verifier si user exists avant de creer un nouveau user
    public static Boolean userExists(String userPseudo, JdbcConnectionSource jdbc) throws SQLException {
        List<UserBean> listSamePseudo = getDaoUser(jdbc).queryForEq("pseudo", userPseudo);// faz a query como se fosse where em sql e "pseudo" e o nome da coluna da BDD
        return !listSamePseudo.isEmpty();
    }


    public static Boolean verifyAuthentification(UserBean user, JdbcConnectionSource jdbc) throws SQLException {

        //Map est la collection key-value . La query ORMLite pour chercher user avec pseudo et psw indiques dans les inputs
        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put("pseudo", user.getPseudo());
        infos.put("password", user.getPassword());
        List<UserBean> listSameInfos = getDaoUser(jdbc).queryForFieldValues(infos);

        if (listSameInfos.isEmpty()) {
            return false;
        }
        user.setId(listSameInfos.get(0).getId());
        return true; //user+pass sont OK
    }


}

