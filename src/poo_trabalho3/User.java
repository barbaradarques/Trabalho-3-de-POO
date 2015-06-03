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
import java.util.HashMap;

public abstract class User {

    private int LOANSMAX;
    private int DAYSMAX;
    private String name;
    public HashMap<String, Loan> loans; // <livro, empréstimo>
    private int blockedDays = 0;

    public int getLOANSMAX(){
        return LOANSMAX;
    }


    public int getDAYSMAX(){
        return DAYSMAX;
    }


    public void setLOANSMAX(int LOANSMAX){
        this.LOANSMAX = LOANSMAX;
    }


    public void setDAYSMAX(int DAYSMAX){
        this.DAYSMAX = DAYSMAX;
    }


    public void setName(String name){
        this.name = name;
    }


    public String getName(){
        return name;
    }


    public HashMap<String, Loan> getLoans(){
        return loans;
    }


    public Loan addLoan(String book, LocalDate date){
        LocalDate duedate = date.plusDays(DAYSMAX);
        Loan loan = new Loan(date, duedate, book, this);
        loans.put(book, loan);
        return loan;
    }

    public int daysBlocked(LocalDate date){
        blockedDays=0;
        loans.values().stream()
                .filter(l->(l.getLoanDate().isBefore(date)))
                .forEach(l->{
                    if(l.getReturnDate()!=null&&l.getReturnDate().isBefore(date)){ //se o livro já foi devolvido até essa data                        
                        Period p = l.getLoanDate().until(l.getReturnDate()); //do dia do empréstimo até a data  da devolução
                        int interval = p.getDays()+30*p.getMonths()+365*p.getYears(); 
                        if(interval>DAYSMAX){ //se o intervalo acima foi maior que o período limite de 
                            int delay = interval-DAYSMAX; //dias de atraso
                            p=l.getReturnDate().plusDays(delay).until(date);
                            blockedDays+= p.getDays()+30*p.getMonths()+365*p.getYears();//dias restantes de atraso
                        }
                    } else if(l.getReturnDate()==null||l.getReturnDate().isAfter(date)){//se ainda não foi entregue até a atual data
                        Period p = l.getDueDate().until(date); //dias de atraso
                        int interval = p.getDays()+30*p.getMonths()+365*p.getYears(); //dias desde o empréstimo 
                        if(interval>0){
                            blockedDays+= interval;
                        }                     
                    }
                });
        return blockedDays;
    }
    public boolean isBlocked(LocalDate date){  
        if(daysBlocked(date)>0){
            System.out.println(this.name+" não pode fazer empréstimo pois está bloqueado(a) por "+blockedDays+" dias.");
            return true;
        }
        return false;
    }
    
    

}
