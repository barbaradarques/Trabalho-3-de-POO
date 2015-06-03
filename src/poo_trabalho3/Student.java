/*------------------------------------------------------------
 *                      === POO_Trabalho3 ===
 *  
 *
 *  @author  Barbara Darques (ICMC-USP)
 *             
 *-----------------------------------------------------------*/


package poo_trabalho3;

import java.util.HashMap;


public class Student extends User {
    
    public Student (String name) {
        loans = new HashMap<>();
        setLOANSMAX(4);
        setDAYSMAX(15);
        setName(name);
    }
    
    @Override
    public String toString(){
        int blockeddays = daysBlocked(Library.today);
        return "Nome: "+getName()+" | Tipo: Estudante"+
                (blockeddays>0?" (Suspenso até "+Library.today.plusDays(blockeddays)+")":"");
    }

}
