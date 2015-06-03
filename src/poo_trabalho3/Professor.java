/*------------------------------------------------------------
 *                      === POO_Trabalho3 ===
 *  
 *
 *  @author  Barbara Darques (ICMC-USP)
 *             
 *-----------------------------------------------------------*/
package poo_trabalho3;

import java.util.HashMap;

public class Professor extends User {

    public Professor(String name){
        loans = new HashMap<>();
        setName(name);
        setLOANSMAX(6);
        setDAYSMAX(60);
    }


    @Override
    public String toString(){
        int blockeddays = daysBlocked(Library.today);
        return "Nome: "+getName()+" | Tipo: Professor"+
                (blockeddays>0?" (Suspenso até "+Library.today.plusDays(blockeddays)+")":"");
    }

}
