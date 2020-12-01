package chat.demo;

import chat.demo.beans.ErrorBean;
import chat.demo.beans.MessageBean;
import chat.demo.beans.UserBean;
import chat.demo.dao.MessageDao;
import chat.demo.dao.UserDao;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//pour creer le idSession
import java.sql.SQLException;
import java.util.List;
import java.util.UUID; //(Universally unique identifier)

@RestController //Permet de transformer une classe en WebService
public class ChatAPI {


    public static String createIdSession() {
        // Creating a random UUID (Universally unique identifier).
        UUID idSession = UUID.randomUUID();
        String randomUUIDString = idSession.toString();
        return randomUUIDString;
    }

    DaoConnexion conexao = new DaoConnexion();


//0.Faire un test: OK
    //http://localhost:8080/test
    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/test")
    public static String test() {
        System.out.println("/test");
        createIdSession();
        return "test hello";
    }

//____________________________________________
//1.le serveur reçoit le message envoyé par l'utilisateur ce que permet de la sauvegarder

//http://localhost:8080/envoyerMsg
    @PostMapping("/envoyerMsg")
    public Object envoyerMsg( @RequestBody MessageBean msg) {

        System.out.println(msg.getUser().getIdSession() + " " + msg.getUser().getPseudo() + " " + msg.getUser().getId() );
        //pseudo null e id null - criar funçao
        System.out.println(msg.getContent());

        //Creer conexion
        JdbcConnectionSource connectionSource = null;
        try {
            connectionSource = conexao.createConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //verifier donnees
        try {
            //verifier que le idSession n'est pas null/vide
        if(msg.getUser().getIdSession() == null  || msg.getUser().getIdSession().trim().length()==0 ){
            throw new Exception("Session vide/null");
        }

        //verifier si l'idSession est active/existe sur la BDD
            List<UserBean> usersIdSession = UserDao.getUserByIdSession(msg.getUser().getIdSession(), connectionSource );
        if(usersIdSession.isEmpty()){
            throw new Exception("idSession doesn't exist!");
        }
        //get userId pour la BDD
            msg.getUser().setId(usersIdSession.get(0).getId());

        //get pseudo pour le msg
            msg.getUser().setPseudo(usersIdSession.get(0).getPseudo());

        //verifier si le contenu est null ou vide avant de sauvegarder msg
            if(msg.getContent() == null || msg.getContent().trim().length()==0){
                throw new Exception("Message vide/null");
            }
            MessageDao.saveMsg(msg, connectionSource);
           return null;
        }
        catch(Exception e) {
            e.printStackTrace();
            return new ErrorBean(e.getMessage());

    //fermer la connexion
        }finally{
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//___________________________
//2.quand on arrive a la pagehome on demande la liste de messages

    //http://localhost:8080/listeMsg
    @GetMapping("/listeMsg")
    public Object demanderListeMsg(@RequestBody UserBean u) throws Exception { //vai receber do cliente o idSession q permite veriifcar se o idSession e valido


        //Creer conexion
        JdbcConnectionSource connectionSource = null;
        try {
            connectionSource = conexao.createConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //System.out.println("\nids: "+ userEmpty.getIdSession();
        if(u.getIdSession() == null  || u.getIdSession().trim().length()==0){
            throw new Exception("Session vide/null");
        }
        //verifier si idsession exists/est active BDD
        if(UserDao.getUserByIdSession(u.getIdSession(), connectionSource).isEmpty()){
            throw new Exception("idSession doesn't exist!");
        }
        //sil ny a pas d'erreurs envoie liste msgs
        try{

            return MessageDao.getMsgList(connectionSource);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(" demanderListMsg() ChatApi");
            return new ErrorBean(e.getMessage()); //deve ter return pk se houver erro nao retorna nd e tem de retornar Objeto segundo a funçao
        }
        finally {
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

//______________________________________
 // Register un compte - verifier données

    //http://localhost:8080/register
    @PostMapping("/register")
    public Object registerUser( @RequestBody UserBean userReceived)  {

        //Creer conexion
        JdbcConnectionSource connectionSource = null;
        try {
            connectionSource = conexao.createConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        try {
            System.out.println("Pseudo: "+userReceived.getPseudo()+" password: "+userReceived.getPassword());

            //pseudo null / vide
            if(userReceived.getPseudo() == null || userReceived.getPseudo().trim().length()==0){
                throw new Exception("Pseudo vide/null");
            }
            //verifier password
            if(userReceived.getPassword() == null || userReceived.getPassword().trim().length()==0){
                throw new Exception("Password vide/null");
            }

            //si l'user existe deja sur la BDD
            if(UserDao.userExists(userReceived.getPseudo(), connectionSource )){
                throw new Exception("That pseudo already exists!");
            }

            //Creer un user
            userReceived.setIdSession(createIdSession());//o idSession de userReceived é o resultado (string) da chamada da funçao getIdSession()
            UserDao.createUpdateUser(userReceived, connectionSource); ///criar o user na BDD
            return new UserBean(userReceived.getIdSession()); // meter spring.jackson.default-property-inclusion = NON_NULL na application.propreties patra nao retornar os valores nulls sn retorna id=null, pseudo=null,etc.Assim so retorna mesmo o idSession

        }
        catch(Exception e) {
            e.printStackTrace();
            return new ErrorBean(e.getMessage());
            //comment eviter lerreur de close connection
        }finally{
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

 //______________________________________
 //http://localhost:8080/login
 @PostMapping("/login")
 public Object login( @RequestBody UserBean userReceived)  { //retourne un onjet: soit un erreur soit un objet

     //Creer conexion à la BDD faire les changements et la fermer à la fin. Si une tache a un erreur la connexion s'arrete et les autres taches nont pas lieu.
     JdbcConnectionSource connectionSource = null;
     try {
         connectionSource = conexao.createConnection();
     } catch (SQLException throwables) {
         throwables.printStackTrace();
     }

     try {
         System.out.println("Pseudo: "+userReceived.getPseudo()+" password: "+userReceived.getPassword());

         //Verifier le pseudo
         if(userReceived.getPseudo() == null || userReceived.getPseudo().trim().length()==0){
             throw new Exception("Pseudo vide/null");
         }
         //verifier psw
         if(userReceived.getPassword() == null || userReceived.getPassword().trim().length()==0){
             throw new Exception("Password vide/null");
         }

         //verifier si suer existe deja
         if(!UserDao.userExists(userReceived.getPseudo(),  connectionSource )){
             throw new Exception("That Pseudo does not exist!");
         }

         //verifier pair pseudo+pass
         if(UserDao.verifyAuthentification(userReceived,  connectionSource)){ //si true, existe
             //idsession
            userReceived.setIdSession(createIdSession());
             UserDao.createUpdateUser(userReceived,  connectionSource); ///creer une user
             return new UserBean(userReceived.getIdSession());

         } throw new Exception("User pseudo or password are incorrect!");
     }

     catch(Exception e){
         e.printStackTrace();
         return new ErrorBean(e.getMessage());

        //fermer connexion
     }finally{
         try {
             connectionSource.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

 }




}
