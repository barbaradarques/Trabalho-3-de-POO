/*------------------------------------------------------------
 *                      === POO_Trabalho3 ===
 *  
 *  
 *  @author  Barbara Darques (ICMC-USP)
 *             
 *-----------------------------------------------------------*/
package poo_trabalho3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Library {

    private static final String USERS_CSV = "src/users.csv";
    private static final String OPERATIONS_CSV = "src/operations.csv";
    private static final String BOOKS_CSV = "src/books.csv";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static HashMap<String, String> books; // <nome do livro, tipo do livro>
    private static HashMap<String, User> users; // <nome do usuário, objeto do usuário>
    public static ArrayList<Loan> allLoans; // todos os empréstimos de todos os tempos
    private static ArrayList<Loan> validLoans; // só leva em consideração os empréstimos realizados da data atual
    public static LocalDate today;
    private static Scanner s;


    public static void main(String[] args){
        start();
        s = new Scanner(System.in);
        try {
            mainMenu();
        } catch (IOException ex){
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public static HashMap<String, String> getBooks(){
        return books;
    }


    public static void rewriteCSV(){

    }


    public static void loadLoans() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(OPERATIONS_CSV));
        String line;
        while ((line = reader.readLine()) != null){
            String[] values = line.split(",");
            // <data da ação>,<nome da ação>,<nome do usuário>,<nome do livro>
            switch (values[1]){
                case "retirada":
                    borrowBook(values[2], values[3], LocalDate.parse(values[0], FORMATTER), true);
                    break;
                case "devolução":
                    returnBook(values[2], values[3], LocalDate.parse(values[0], FORMATTER));
                    break;
            }

        }
    }


    public static void loadBooks() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(BOOKS_CSV));
        reader.lines().forEach(line -> {
            String[] values = line.split(",");
            registerBook(values[0], values[1], true);
        });

    }


    public static void loadUsers() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(USERS_CSV));
        String line;
        while ((line = reader.readLine()) != null){
            String[] values = line.split(",");
            registerUser(values[0], values[1], true);
        }
    }


    public static void registerUser(String name, String type, boolean isFromCSV){
        if (users.containsKey(name)){
            System.err.println("\nEsse usuário já existe no sistema.\n");
        } else {
            if (!isFromCSV){
                FileWriter writer;
                try {
                    writer = new FileWriter(USERS_CSV, true);
                    writer.append(name + "," + type + "\n");
                    writer.close();
                } catch (IOException ex){
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("\nOperação realizada com sucesso.\n");
            }
            switch (type){
                case "estudante":
                    users.put(name, new Student(name));
                    break;
                case "professor":
                    users.put(name, new Professor(name));
                    break;
                case "comunidade":
                    users.put(name, new CommunityMember(name));
                    break;
            }
        }
    }


    public static void borrowBook(String username, String bookname, LocalDate date, boolean isFromCSV) throws FileNotFoundException, IOException{
        if (users.containsKey(username) && books.containsKey(bookname)){
            User user = users.get(username);

            if (user.isBlocked(date) == false){
                Loan loan = user.addLoan(bookname, date);
                allLoans.add(loan);
                if (!isFromCSV){ //se ainda não estiver registrado no arquivo CSV, ou seja, for entrada do usuário
                    FileWriter writer = new FileWriter(OPERATIONS_CSV, true);
                    writer.append(date.format(FORMATTER) + ",retirada," + username + "," + bookname + "\n");
                    writer.close();
                    validLoans.add(loan);
                    System.out.println("\nOperação realizada com sucesso.\n");
                }
            }
        } else {
            System.err.println("\nEssa pessoa e/ou livro não existe(m) no sistema.\n");
        }

    }


    public static void returnBook(String username, String bookname, LocalDate date){
        if (users.containsKey(username)){
            User user = users.get(username);
            if (user.getLoans().containsKey(bookname)){
                Loan loan = user.getLoans().get(bookname);
                if (!loan.hasBeenAlteredInTheFuture()){
                    loan.setReturnDate(date);
                }
            } else {
                System.err.println("\nEsse livro não existe ou não foi emprestado a esse usuário.");
            }
        } else {
            System.err.println("\n Essa pessoa não existe no sistema.\n");
        }

    }


    public static void registerBook(String book, String type, boolean isFromCSV){
        if (books.containsKey(book)){
            System.err.println("\nEsse livro já existe no sistema.\n");
        } else {
            if (!isFromCSV){
                FileWriter writer;
                try {
                    writer = new FileWriter(BOOKS_CSV, true);
                    writer.append(book + "," + type + "\n");
                    writer.close();
                } catch (IOException ex){
                    Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("\nOperação realizada com sucesso.\n");
            }
            books.put(book, type);

        }

    }


    public static void listUsers(){
        System.out.println("====== Lista de usuários cadastrados ======");
        users.values().stream()
                .sorted(Comparator.comparing(User::getName))
                .forEach(u -> {
                    System.out.println(u);
                });
    }


    public static void listBooks(){
        System.out.println("====== Lista de livros cadastrados ======");
        books.keySet().stream()
                .sorted()
                .forEach(b -> {
                    System.out.println("Livro: " + b + " | Tipo: " + books.get(b));
                });
    }


    public static void listValidLoans(){
        System.out.println("\n========================== Lista de empréstimos registrados até o dia " + today.format(FORMATTER) + " ==========================");
        validLoans.stream()
                .sorted(Comparator.comparing(Loan::getLoanDate))
                .forEach(l -> {
                    System.out.println(l);
                });
        System.out.println("===========================================================================================================\n");
    }


    public static void setDate(String date){// no formato dd/mm/aaaa
        today = LocalDate.parse(date, FORMATTER);
    }


    public static void setValidLoans(){
        validLoans = new ArrayList<>();
        allLoans.stream()
                .filter(l -> (!l.getLoanDate().isAfter(today))) // ou seja, é antes ou igual a hoje
                .forEach(l -> {
                    validLoans.add(l);
                });
    }


    public static void start(){
        books = new HashMap<>();
        users = new HashMap<>();
        allLoans = new ArrayList<>();
        today = LocalDate.now(); //define o dia da consulta
        try {
            loadBooks();
            loadUsers();
            loadLoans();
            setValidLoans();
        } catch (IOException ex){
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public static void mainMenu() throws IOException{
        System.out.println("\n=============== Olá, hoje é " + today.format(FORMATTER) + " ===============");
        System.out.println("> Selecione uma opção:");
        System.out.println("[1] Fazer um empréstimo");
        System.out.println("[2] Devolver um livro");
        System.out.println("[3] Cadastrar um novo livro");
        System.out.println("[4] Cadastrar um novo usuário");
        System.out.println("[5] Checar a situação cadastral de um usuário");
        System.out.println("[6] Listar todos os livros");
        System.out.println("[7] Listar todos os usuários");
        System.out.println("[8] Listar todos os empréstimos");
        System.out.println("[9] Mudar a data de hoje");
        System.out.println("[0] Sair");
        System.out.println("======================================================");
        int option = s.nextInt();
        s.nextLine(); //para consumir o \n que não foi consumido acima
        String username;
        String book;
        String type;
        switch (option){
            case 1:
                System.out.println("=== FAZER NOVO EMPRÉSTIMO ===");
                System.out.println("Insira o nome do usuário:");
                username = s.nextLine();
                System.out.println("Insira o nome do livro:");
                book = s.nextLine();
                borrowBook(username, book, today, false);
                mainMenu();
                break;
            case 2:
                System.out.println("=== DEVOLUÇÃO DE LIVRO ===");
                System.out.println("Insira o nome do usuário:");
                username = s.nextLine();
                System.out.println("Insira o nome do livro:");
                book = s.nextLine();
                returnBook(username, book, today);
                mainMenu();
                break;
            case 3:
                System.out.println("=== CADASTRO DE LIVRO ===");
                System.out.println("Insira o nome do livro:");
                book = s.nextLine();
                System.out.println("Insira o tipo do livro(\"texto\" ou \"geral\"): ");
                type = s.nextLine();
                registerBook(book, type, false);
                mainMenu();
                break;
            case 4:
                System.out.println("=== CADASTRO DE USUÁRIO ===");
                System.out.println("Insira o nome do usuário:");
                username = s.nextLine();
                System.out.println("Insira o tipo do usuário(\"estudante\", \"professor\" ou \"comunidade\"): ");
                type = s.nextLine();
                registerUser(username, type, false);
                mainMenu();
                break;
            case 5:
                System.out.println("==== STATUS DO USUÁRIO ====");
                System.out.println("Insira o nome do usuário:");
                username = s.nextLine();
                if (users.containsKey(username)){
                    User user = users.get(username);
                    System.out.println("\n"+user);
                    System.out.println("==== Empréstimos feitos até "+today.format(FORMATTER)+" ====");
                    user.getLoans().values().stream()
                            .filter(l -> (!l.getLoanDate().isAfter(today)))
                            .forEach(l -> {
                                System.out.println(l);
                            });
                } else {
                    System.err.println("\nEsse usuário não existe no sistema.\n");
                }

                mainMenu();
                break;
            case 6:
                listBooks();
                mainMenu();
                break;
            case 7:
                listUsers();
                mainMenu();
                break;
            case 8:
                listValidLoans();
                mainMenu();
                break;
            case 9:
                System.out.println("==== NOVA DATA ====");
                System.out.println("Digite uma data no formato \"dd/mm/aaaa\"");
                setDate(s.nextLine());
                System.out.println("\nOperação realizada com sucesso.\n");
                start();
                mainMenu();
                break;
            case 0:
                System.out.println("================== Sistema encerrado =================");
                System.exit(0);
                break;

        }

    }

}
