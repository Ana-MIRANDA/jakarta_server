package chat.demo.dao;

import chat.demo.beans.MessageBean;
import chat.demo.beans.UserBean;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class MessageDao { //on l'appelle MessageDao prc q la table BDD s'appelle Message

// DAO pour utiliser MessageBean  pour faire requetes, updates, delete, etc
    public static Dao<MessageBean, Long> getDaoMsg(JdbcConnectionSource jdbc) throws SQLException {
       return DaoManager.createDao(jdbc, MessageBean.class);
    }


//__________function saveMsg()________

    public static void saveMsg(MessageBean msg, JdbcConnectionSource jdbc)  {
        long now = System.currentTimeMillis();
        msg.setDate(now);
        System.out.println("cont " + msg.getContent() + "\n date " + msg.getDate() + "\n userid " + msg.getUser().getId());
        try {
            getDaoMsg(jdbc).createOrUpdate(msg);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

//__________function getMsgList()___________________

    static public ArrayList<MessageBean> getMsgList(JdbcConnectionSource jdbc) throws Exception {

            //inner join en ORMLite (la ytbale users avec la table message)
            QueryBuilder<MessageBean, Long> messageQB = MessageDao.getDaoMsg(jdbc).queryBuilder();
            messageQB.selectRaw("content", "user_id", "date");//choisir  les columns
            messageQB.limit((long) 20); //les dernieres 20 msgs
            messageQB.orderBy("date", false) ;// msgs ordonnées

            //ORMLite pour quil ne retoune pas la column idMsg
            String query = messageQB.prepareStatementString(); // ici sauvegarder tt ce qui est en haut
            RawRowMapper<MessageBean> rawRowMapper = MessageDao.getDaoMsg(jdbc).getRawRowMapper(); // RawRowMapper est lobjet qui permet transformer rawResults en java objetcs
            GenericRawResults<MessageBean> rawResults = MessageDao.getDaoMsg(jdbc).queryRaw(query,rawRowMapper); //va chercher les resultats a la BDD
            List<MessageBean> results = rawResults.getResults(); //Transformer les resultats en list

            System.out.println(results);

        //ORMLite ne fait pas innerjoin donc je lai fait comme pour recuperer dans la BDD le pseudo par l' id user qui a ebvoyé le msg
        for (MessageBean m: results) {
            System.out.println("msgs \n id: " + m.getId() + "\n content " + m.getContent() + "\n userid " + m.getUser().getId() + "\n date " + new Timestamp(m.getDate())); //new Timestamp(m.getDate() pluslisible
            Long mgUserId =  m.getUser().getId(); //obter id de quem escreveu a msg
           UserBean u = UserDao.getUserById(mgUserId, jdbc);
           //dans le msg il y aura qu le pseudo
           UserBean user = new UserBean();
           user.setPseudo(u.getPseudo());
            m.setUser(user); //para dar o corpo inteiro do user e nao so o pseudo, pois se houver mais alguma a coisa a mudar + tarde vai avec
        }

        return (ArrayList<MessageBean>) results; //cast results (transformer en ArrayList<MessageBean>)
    }

}

