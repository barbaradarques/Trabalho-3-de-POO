/*------------------------------------------------------------
 *                      === POO_Trabalho3 ===
 *  
 *
 *  @author  Barbara Darques (ICMC-USP)
 *             
 *-----------------------------------------------------------*/
package poo_trabalho3;

import java.time.LocalDate;
import java.time.Period;
import static poo_trabalho3.Library.FORMATTER;
import static poo_trabalho3.Library.today;

public class Loan {

    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String book;
    private User user;


    
    public void setReturnDate(LocalDate returnDate){
        this.returnDate = returnDate;
    }

    public LocalDate getLoanDate(){
        return loanDate;
    }


    public LocalDate getDueDate(){
        return dueDate;
    }


    public LocalDate getReturnDate(){
        return returnDate;
    }


    public String getBook(){
        return book;
    }


    public User getUser(){
        return user;
    }



    public Loan(LocalDate loanDate, LocalDate dueDate, String book, User user){
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.book = book;
        this.user = user;
    }


    @Override
    public String toString(){
        boolean isLate = false;
        if(returnDate==null||returnDate.isAfter(today)){
            Period p = Period.between(loanDate, today);
            long daysSinceLoan = p.getDays()+30*p.getMonths()+365*p.getYears();
            if(daysSinceLoan>user.getDAYSMAX()){
                isLate = true;
            }
        }
        return "Usuário: " + user.getName() + " | Livro: " + book + " | Retirada: " + loanDate.format(FORMATTER)
                + " | Vencimento: " + dueDate.format(FORMATTER) 
                + (isLate?" (ATRASADO) ":"")
                + (returnDate==null|| returnDate.isAfter(today) ? "" : " | Devolução: " + returnDate.format(FORMATTER));
    }
    
    public boolean hasBeenAlteredInTheFuture(){
        if(this.returnDate!=null&&returnDate.isAfter(today)){
            System.out.println("Operação não pode ser realizada pois empréstimo foi alterado no futuro.");
            return true;
        }
        return false;
    }

}
