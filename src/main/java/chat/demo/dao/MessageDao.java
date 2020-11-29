package chat.demo.dao;

import chat.demo.beans.MessageBean;
import chat.demo.beans.UserBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MessageDao { //chama-se MessageDao pk a tabela da BDD se chama Message



// DAO pour usar MessageBean pour utiliser pour requetes, updates, delete, etc
    public static Dao<MessageBean, Long> getDaoMsg(JdbcConnectionSource jdbc) throws SQLException {
       return DaoManager.createDao(jdbc, MessageBean.class);
    }


//__________function saveMsg()________

    //creer une liste provisoire - tant que pas de bdd e testada no postman

    public static void saveMsg(MessageBean msg, JdbcConnectionSource jdbc)  {

        try {
            UserDao.createUser(msg.getUser(), jdbc); //passar os argum a chamada de funçao createUser: userBean + connexion
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(msg.getUser().getId()); //devolve o id

        try {
            getDaoMsg(jdbc).createOrUpdate(msg);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }//fecha saveMsg()


//__________function getMsgList()___________________

//nao fiz o try ctach pk a funçao que a invoca ja esta dentro de try catch
    static public ArrayList<MessageBean> getMsgList(JdbcConnectionSource jdbc) throws Exception {

//inner join en ORMLite (a tabela users com tabela message)
            QueryBuilder<MessageBean, Long> messageQB = MessageDao.getDaoMsg(jdbc).queryBuilder();
            messageQB.limit((long) 20); //ir buscar as 20 ultimas mensagens
            messageQB.orderBy("id", false) ;// as mensagens eerao orddenadas por id do + recente p o + antigo(ascending=true)
            QueryBuilder<UserBean, Long> userQB  = UserDao.getDaoUser(jdbc).queryBuilder();
        //le moment du join avec order query
            List<MessageBean> results = messageQB.join(userQB).query();
            System.out.println(results);


        //para a BDD ir buscar o pseudo pelo id do user da msg (ORMLite nao faz inner join por isso fiz a assim)
        for (MessageBean m: results) {
          Long mgUserId =  m.getUser().getId(); //obter id de quem escreveu a msg
           UserBean u = UserDao.getUserById(mgUserId, jdbc);
            m.setUser(u); //para dar o corpo inteiro do user e nao so o pseudo, pois se houver mais alguma a coisa a mudar + tarde vai avec
        }

        return (ArrayList<MessageBean>) results; //vai fazer cast/transf de results k e 1 lista em arraylist
    }


//_________________________________

} //fecha a class

