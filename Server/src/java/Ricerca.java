/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Davide
 */
public class Ricerca extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    int idutente;
    String ruolo,prestito,nome,cognome,nickname;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
         JSONArray jsonarrpreferiti = new JSONArray();
      JSONArray jsonarrutenti = new JSONArray();
        PrintWriter out = response.getWriter();
        List <Utente> preferiti;
        List <Utente> utenti;
        
        idutente=Integer.parseInt(request.getParameter("idutente"));
        ruolo=request.getParameter("ruolo");
        nome=request.getParameter("nome");
        cognome=request.getParameter("cognome");
        nickname=request.getParameter("nickname");
        prestito=request.getParameter("prestito");
        
        if (ruolo.equals("Tutti")){
            if(prestito.equals("Qualsiasi")){
                 preferiti=this.preferiti(idutente,nome,cognome,nickname);
                 utenti=this.utentinon(idutente,nome,cognome,nickname);
                }else{preferiti=this.preferitiprestiti(idutente, nome, cognome, nickname, prestito);
                        utenti=this.utentinonprestiti(idutente, nome, cognome, nickname, prestito);
            }
        }else{
            if(prestito.equals("Qualsiasi")){preferiti=this.preferitiruolo(idutente, nome, cognome, nickname, ruolo);
            utenti=this.utentinonruolo(idutente, nome, cognome, nickname, ruolo);}else{
               preferiti=this.preferitiprestitiruolo(idutente, nome, cognome, nickname, prestito, ruolo);
                utenti=this.utentinonprestitiruolo(idutente, nome, cognome, nickname, prestito, ruolo);
            }
        }
         for(int i=0;i<preferiti.size();i++){
           JSONObject obj= new JSONObject();
           obj.put("idpref", new Integer(preferiti.get(i).getId()));
           obj.put("nomepref",preferiti.get(i).getnome());
           obj.put("cognomepref",preferiti.get(i).getcognome());
           obj.put("emailpref",preferiti.get(i).getemail());
           obj.put("nicknamepref",preferiti.get(i).getnickname());
           obj.put("ruolopref",preferiti.get(i).getruolo());
           jsonarrpreferiti.add(obj);
       }       
       
        
       for(int j=0;j<utenti.size();j++){
         JSONObject obj= new JSONObject();
           obj.put("id", new Integer(utenti.get(j).getId()));
           obj.put("nome",utenti.get(j).getnome());
           obj.put("cognome",utenti.get(j).getcognome());
           obj.put("email",utenti.get(j).getemail());
           obj.put("nickname",utenti.get(j).getnickname());
           obj.put("ruolo",utenti.get(j).getruolo());
           jsonarrpreferiti.add(obj);  
       }
       
       out.print(jsonarrpreferiti);
       }
    
    
    
    
    public List<Utente> preferiti(int idutente,String nome,String cognome,String nickname){
       List <Utente> preferiti= new ArrayList<Utente>();
       List<Utente>preferitiid=new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT IDPREFERITO "
                   + "FROM PREFERITO "
                   + "WHERE IDUTENTE=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
            st.setInt(1, idutente);
            rs = st.executeQuery();
            while(rs.next()){
            Utente u= new Utente();
            u.setid(rs.getInt(1));
            preferitiid.add(u);}
            
            ResultSet rs1;
             try{
                 for(int i=0;i<preferitiid.size();i++){
           String query1="SELECT ID,NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE "
                   + "WHERE ID=? AND NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ?";
           PreparedStatement st1 = DBManager.db.prepareStatement(query1);
            st1.setInt(1, preferitiid.get(i).getId());
            st1.setString(2,'%'+nome+'%');
            st1.setString(3,'%'+cognome+'%');
            st1.setString(4,'%'+nickname+'%');
            rs1 = st1.executeQuery();
            if(rs1.next()){
                Utente u2 =new Utente();
                u2.setid(rs1.getInt(1));
                u2.setnome(rs1.getString(2));
                u2.setcognome(rs1.getString(3));
                u2.setruolo(rs1.getString(4));
                u2.setemail(rs1.getString(5));
                u2.setnickname(rs1.getString(6));
                preferiti.add(u2);
                
            }
                                   }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}

    }catch(Exception e){}
        return preferiti;
       }
    
    
    
    
    
      public List<Utente> utentinon(int idutente,String nome,String cognome,String nickname){
       List <Utente> utenti= new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT ID,NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE "
                   + "WHERE ID!=? AND NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
           st.setInt(1, idutente);
           st.setString(2,'%'+nome+'%');
           st.setString(3,'%'+cognome+'%');
           st.setString(4,'%'+nickname+'%');
           rs = st.executeQuery();
            while(rs.next()){
            Utente u2 =new Utente();
                u2.setid(rs.getInt(1));
                u2.setnome(rs.getString(2));
                u2.setcognome(rs.getString(3));
                u2.setruolo(rs.getString(4));
                u2.setdescrizioneruolo(rs.getString(5));
                u2.setnickname(rs.getString(6));
                utenti.add(u2);
                    }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}
     return utenti;}
    
    
    
    
    public List<Utente> preferitiprestiti(int idutente,String nome,String cognome,String nickname,String prestito){
       List <Utente> preferiti= new ArrayList<Utente>();
       List<Utente>preferitiid=new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT IDPREFERITO "
                   + "FROM PREFERITO "
                   + "WHERE IDUTENTE=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
            st.setInt(1, idutente);
            rs = st.executeQuery();
            while(rs.next()){
            Utente u= new Utente();
            u.setid(rs.getInt(1));
            preferitiid.add(u);}
            
            ResultSet rs1;
             try{
                 for(int i=0;i<preferitiid.size();i++){
           String query1="SELECT UTENTE.ID,UTENTE.NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE,OGGETTO "
                   + "WHERE UTENTE.ID=IDPROPRIETARIO AND UTENTE.ID=? AND UTENTE.NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND OGGETTO.NOME=?";
           PreparedStatement st1 = DBManager.db.prepareStatement(query1);
            st1.setInt(1, preferitiid.get(i).getId());
            st1.setString(2,'%'+nome+'%');
            st1.setString(3,'%'+cognome+'%');
            st1.setString(4,'%'+nickname+'%');
            st1.setString(5,prestito);
            rs1 = st1.executeQuery();
            if(rs1.next()){
                Utente u2 =new Utente();
                u2.setid(rs1.getInt(1));
                u2.setnome(rs1.getString(2));
                u2.setcognome(rs1.getString(3));
                u2.setruolo(rs1.getString(4));
                u2.setemail(rs1.getString(5));
                u2.setnickname(rs1.getString(6));
                preferiti.add(u2);
                
            }
                                   }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}

    }catch(Exception e){}
        return preferiti;
       }
    
    
     public List<Utente> utentinonprestiti(int idutente,String nome,String cognome,String nickname,String prestito){
       List <Utente> utenti= new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT UTENTE.ID,UTENTE.NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE,OGGETTO "
                   + "WHERE UTENTE.ID=IDPROPRIETARIO AND UTENTE.ID!=? AND UTENTE.NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND OGGETTO.NOME=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
           st.setInt(1, idutente);
           st.setString(2,'%'+nome+'%');
           st.setString(3,'%'+cognome+'%');
           st.setString(4,'%'+nickname+'%');
           st.setString(5,prestito);
           rs = st.executeQuery();
            while(rs.next()){
            Utente u2 =new Utente();
                u2.setid(rs.getInt(1));
                u2.setnome(rs.getString(2));
                u2.setcognome(rs.getString(3));
                u2.setruolo(rs.getString(4));
                u2.setdescrizioneruolo(rs.getString(5));
                u2.setnickname(rs.getString(6));
                utenti.add(u2);
                    }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}
     return utenti;}
    
    
    
    public List<Utente> preferitiruolo(int idutente,String nome,String cognome,String nickname,String ruolo){
       List <Utente> preferiti= new ArrayList<Utente>();
       List<Utente>preferitiid=new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT IDPREFERITO "
                   + "FROM PREFERITO "
                   + "WHERE IDUTENTE=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
            st.setInt(1, idutente);
            rs = st.executeQuery();
            while(rs.next()){
            Utente u= new Utente();
            u.setid(rs.getInt(1));
            preferitiid.add(u);}
            
            ResultSet rs1;
             try{
                 for(int i=0;i<preferitiid.size();i++){
           String query1="SELECT ID,NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE "
                   + "WHERE ID=? AND NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND RUOLO=?";
           PreparedStatement st1 = DBManager.db.prepareStatement(query1);
            st1.setInt(1, preferitiid.get(i).getId());
            st1.setString(2,'%'+nome+'%');
            st1.setString(3,'%'+cognome+'%');
            st1.setString(4,'%'+nickname+'%');
            st1.setString(5,ruolo);
            rs1 = st1.executeQuery();
            if(rs1.next()){
                Utente u2 =new Utente();
                u2.setid(rs1.getInt(1));
                u2.setnome(rs1.getString(2));
                u2.setcognome(rs1.getString(3));
                u2.setruolo(rs1.getString(4));
                u2.setemail(rs1.getString(5));
                u2.setnickname(rs1.getString(6));
                preferiti.add(u2);
                
            }
                                   }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}

    }catch(Exception e){}
        return preferiti;
       }
    
    public List<Utente> utentinonruolo(int idutente,String nome,String cognome,String nickname,String ruolo){
       List <Utente> utenti= new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT ID,NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE "
                   + "WHERE ID!=? AND NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND RUOLO=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
           st.setInt(1, idutente);
           st.setString(2,'%'+nome+'%');
           st.setString(3,'%'+cognome+'%');
           st.setString(4,'%'+nickname+'%');
           st.setString(5,ruolo);
           rs = st.executeQuery();
            while(rs.next()){
            Utente u2 =new Utente();
                u2.setid(rs.getInt(1));
                u2.setnome(rs.getString(2));
                u2.setcognome(rs.getString(3));
                u2.setruolo(rs.getString(4));
                u2.setdescrizioneruolo(rs.getString(5));
                u2.setnickname(rs.getString(6));
                utenti.add(u2);
                    }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}
     return utenti;}
    
    
    
    
    
    
     public List<Utente> preferitiprestitiruolo(int idutente,String nome,String cognome,String nickname,String prestito,String ruolo){
       List <Utente> preferiti= new ArrayList<Utente>();
       List<Utente>preferitiid=new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT IDPREFERITO "
                   + "FROM PREFERITO "
                   + "WHERE IDUTENTE=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
            st.setInt(1, idutente);
            rs = st.executeQuery();
            while(rs.next()){
            Utente u= new Utente();
            u.setid(rs.getInt(1));
            preferitiid.add(u);}
            
            ResultSet rs1;
             try{
                 for(int i=0;i<preferitiid.size();i++){
           String query1="SELECT UTENTE.ID,UTENTE.NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE,OGGETTO "
                   + "WHERE UTENTE.ID=IDPROPRIETARIO AND UTENTE.ID=? AND UTENTE.NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND OGGETTO.NOME=? AND RUOLO=?";
           PreparedStatement st1 = DBManager.db.prepareStatement(query1);
            st1.setInt(1, preferitiid.get(i).getId());
            st1.setString(2,'%'+nome+'%');
            st1.setString(3,'%'+cognome+'%');
            st1.setString(4,'%'+nickname+'%');
            st1.setString(5,prestito);
            st1.setString(6,ruolo);
            rs1 = st1.executeQuery();
            if(rs1.next()){
                Utente u2 =new Utente();
                u2.setid(rs1.getInt(1));
                u2.setnome(rs1.getString(2));
                u2.setcognome(rs1.getString(3));
                u2.setruolo(rs1.getString(4));
                u2.setemail(rs1.getString(5));
                u2.setnickname(rs1.getString(6));
                preferiti.add(u2);
                
            }
                                   }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}

    }catch(Exception e){}
        return preferiti;
       }
     
      public List<Utente> utentinonprestitiruolo(int idutente,String nome,String cognome,String nickname,String prestito,String ruolo){
       List <Utente> utenti= new ArrayList<Utente>();
       ResultSet rs;
       try{
           String query="SELECT UTENTE.ID,UTENTE.NOME,COGNOME,RUOLO,EMAIL,NICKNAME "
                   + "FROM UTENTE,OGGETTO "
                   + "WHERE UTENTE.ID=IDPROPRIETARIO AND UTENTE.ID!=? AND UTENTE.NOME lIKE ? AND COGNOME LIKE ? AND NICKNAME LIKE ? AND OGGETTO.NOME=? AND RUOLO=?";
           PreparedStatement st = DBManager.db.prepareStatement(query);
           st.setInt(1, idutente);
           st.setString(2,'%'+nome+'%');
           st.setString(3,'%'+cognome+'%');
           st.setString(4,'%'+nickname+'%');
           st.setString(5,prestito);
           st.setString(6,ruolo);
           rs = st.executeQuery();
            while(rs.next()){
            Utente u2 =new Utente();
                u2.setid(rs.getInt(1));
                u2.setnome(rs.getString(2));
                u2.setcognome(rs.getString(3));
                u2.setruolo(rs.getString(4));
                u2.setdescrizioneruolo(rs.getString(5));
                u2.setnickname(rs.getString(6));
                utenti.add(u2);
                    }
                 rs.close();
                   st.close();
 
       }catch(Exception e){e.printStackTrace();}
     return utenti;}
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}