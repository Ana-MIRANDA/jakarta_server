package chat.demo;

import chat.demo.beans.ErrorBean;
import chat.demo.beans.MessageBean;
import chat.demo.beans.UserBean;
import chat.demo.dao.MessageDao;
import chat.demo.dao.UserDao;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController //Permet de transformer une classe en WebService
public class ChatAPI {

    /* Era p fazer os testes mas como as funçoes nao sao sstatics nao da mas se as puser static dao erro
    public static void main(String[] args){
        test();
    }*/

    DaoConnexion conexao = new DaoConnexion();

//0.Faire un test: OK
    //http://localhost:8080/test

    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/test")
    public static String test() {
        System.out.println("/test");
        return "test hello";
    }

//____________________________________________
//1. le serveur reçoit le message envoyé par l'utilisateur ce que permet de la sauvegarder
        //tant qu'on a pas de BDD je cree une liste avec Postman qui  recoit como request body - no postman - {"sentBy" :  "toto", "content" : "salut!"}

//http://localhost:8080/envoyerMsg
    @PostMapping("/envoyerMsg")
    public Object envoyerMsg( @RequestBody MessageBean msg)  {
        try {
            System.out.println("content: "+msg.getContent()+" sentBy: "+msg.getUser().getPseudo());
    //verificar se pseudo e null / vide antes de enviar msg
           if(msg.getUser().getPseudo() == null || msg.getUser().getPseudo().equals("")){
            throw new Exception("User vide/null");
           }
     //verificar se contente null / vide antes de enviar msg
            if(msg.getContent() == null || msg.getContent().equals("")){
                throw new Exception("Message vide/null");
            }

            MessageDao.saveMsg(msg, conexao.createConnection() );
           return null;
        }

        catch(Exception e) {
            e.printStackTrace();
            return new ErrorBean(e.getMessage());


    //comment eviter lerreur de close connection
        }finally{
            try {
               conexao.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//___________________________
//2.quand on arrive a la pagehome on demande la liste de messages + aussi tester sur postman tant que nous avons pas de Bdd

    //http://localhost:8080/listeMsg
    @GetMapping("/listeMsg")
    public Object demanderListeMsg()  {
        try{
            return MessageDao.getMsgList(conexao.createConnection()); //chamar a funçao que esta em chatDao
        }
        catch(Exception e) {
            e.printStackTrace();
            System.out.println(" demanderListMsg() ChatApi");
            return new ErrorBean(e.getMessage()); //deve ter return pk se houver erro nao retorna nd e tem de retornar Objeto segundo a funçao
        }
        finally {
            try {
                conexao.closeConnection();
            } catch (IOException e) { //como a conxao tb e de base de dados tem se por tb o try catch
                e.printStackTrace();
            }
        }
        }

//______________________________________
 // Registar uma conta verificar se pseudo e password estao bem preenchidos

    //http://localhost:8080/register
    @PostMapping("/register")
    public Object registerUser( @RequestBody UserBean userReceived)  {
        try {
            System.out.println("Pseudo: "+userReceived.getPseudo()+" password: "+userReceived.getPassword());

            //verificar se pseudo e null / vide
            if(userReceived.getPseudo() == null || userReceived.getPseudo().equals("")){
                throw new Exception("Pseudo vide/null");
            }
            //verificar se password null / vide antes de enviar msg
            if(userReceived.getPassword() == null || userReceived.getPassword().equals("")){
                throw new Exception("Password vide/null");
            }

            //verifica se o user ja existe na BDD
            if(UserDao.userExists(userReceived.getPseudo(), conexao.createConnection() )){
                throw new Exception("That pseudo already exists!");
            }

            //criar utilisador na base de dados
            UserDao.createUser(userReceived, conexao.createConnection());
            return userReceived; //retorna o user recebido ja passado pela BD, com id pk na createUser ele recebe um id pk é metido na base de dados onde recebe o id.
        }

        catch(Exception e) {
            e.printStackTrace();
            return new ErrorBean(e.getMessage());


            //comment eviter lerreur de close connection
        }finally{
            try {
                conexao.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


 //______________________________________
 //http://localhost:8080/login
 @PostMapping("/login")
 public Object login( @RequestBody UserBean userReceived)  {
     try {
         System.out.println("Pseudo: "+userReceived.getPseudo()+" password: "+userReceived.getPassword());

         //verificar se pseudo e null / vide
         if(userReceived.getPseudo() == null || userReceived.getPseudo().equals("")){
             throw new Exception("Pseudo vide/null");
         }
         //verificar se password null / vide antes de enviar msg
         if(userReceived.getPassword() == null || userReceived.getPassword().equals("")){
             throw new Exception("Password vide/null");
         }

         //verifica se o user ja existe na BDD
         if(!UserDao.userExists(userReceived.getPseudo(), conexao.createConnection() )){
             throw new Exception("That Pseudo does not exist!");
         }

         //verificar o par pseudo+pass
         if(UserDao.verifyAuthentification(userReceived, conexao.createConnection())){ //se for true, existe
             return userReceived; //retorna todas as infos do user. ver como so retornar o id e o pseudo
         } throw new Exception("User pseudo or password are incorrect!"); //se for return false da esta msg/exception
     }

     catch(Exception e) {
         e.printStackTrace();
         return new ErrorBean(e.getMessage());


         //comment eviter lerreur de close connection
     }finally{
         try {
             conexao.closeConnection();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

 }




}//fecha a class
