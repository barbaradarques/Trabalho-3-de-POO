/*------------------------------------------------------------
 *                      === POO_Trabalho3 ===
 *  
 *
 *  @author  Barbara Darques (ICMC-USP)
 *             
 *-----------------------------------------------------------*/


package poo_trabalho3;

import java.time.LocalDate;
import java.util.HashMap;


public class CommunityMember extends User {
    private static final int LOANSMAX = 2;
    private static final int DAYSMAX = 15;

    public CommunityMember (String name) {
        loans = new HashMap<>();
        setName(name);
        setLOANSMAX(2);
        setDAYSMAX(15);
    }


    @Override
    public Loan addLoan(String book, LocalDate date){
        if(Library.getBooks().get(book).equals("geral")){
            LocalDate duedate = date.plusDays(DAYSMAX);
            Loan loan = new Loan(date, duedate, book, this);
            getLoans().put(book, loan);
            return loan;
        }
        System.out.println("Membros da comunidade não são autorizados a realizar esse tipo de empréstimo.");
        return null;
    }

 @Override
    public String toString(){
        int blockeddays = daysBlocked(Library.today);
        return "Nome: "+getName()+" | Tipo: Comunidade"+
                (blockeddays>0?" (Suspenso até "+Library.today.plusDays(blockeddays)+")":"");
    }
}
