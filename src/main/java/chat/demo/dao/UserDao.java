package chat.demo.dao;

import chat.demo.beans.MessageBean;
import chat.demo.beans.UserBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

    // DAO pour usar UserBean pour utiliser pour requetes, updates, delete, etc
    public static Dao<UserBean, Long> getDaoUser(JdbcConnectionSource jdbc) throws SQLException {
        return DaoManager.createDao(jdbc, UserBean.class);
    }

//Criar user se ele ainda nao existe na BDD. se existir fica com o id do 1° user da lista retornada com users que tem o mmo nome
    public static void createUser(UserBean user, JdbcConnectionSource jdbc) throws SQLException {
            getDaoUser(jdbc).createOrUpdate(user); //create user do tipo userBean e getDaoUser e a minha ORM/tipo sql insert into
    }



///Comparar user com id em parametro
    public static UserBean getUserById( Long unId, JdbcConnectionSource jdbc) throws SQLException {
        return getDaoUser(jdbc).queryForId(unId); //retorna o user que tiver o id que se quer
    }

/// Verificar se pseudo ja existe antes de criar um novo user
    public static Boolean userExists(String userPseudo, JdbcConnectionSource jdbc) throws SQLException {
        List<UserBean> listSamePseudo = getDaoUser(jdbc).queryForEq("pseudo",userPseudo);// faz a query como se fosse where em sql e "pseudo" e o nome da coluna da BDD
        if (listSamePseudo.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }


    public static Boolean verifyAuthentification(UserBean user, JdbcConnectionSource jdbc ) throws SQLException {

        //Map é colecao de chaves - valor. E a query em OrmLite para ir procurar user com pseudo e pass iguais aos dos inputs
        Map<String, Object> infos = new HashMap<String, Object>();
        infos.put("pseudo", user.getPseudo());
        infos.put("password", user.getPassword());
        List<UserBean> listSameInfos = getDaoUser(jdbc).queryForFieldValues(infos);

        if(listSameInfos.isEmpty()){
            return false; //lista vazia o gajo nao existe (exemplo  engano num dos inputs)
        } user.setId(listSameInfos.get(0).getId()); //ao verificar que ele existe, para nao retornar id null, temos de lhe dar o id que lhe pertence, neste caso o que estiver no index 0
        return true; //user+pass estao corretos
    }


}//fecha class

