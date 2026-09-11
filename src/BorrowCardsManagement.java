import java.sql.Connection;
import java.util.Scanner;

public class BorrowCardsManagement {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Connection conn = ConnectionDB.conn;
        System.out.println("Welcome to the Borrow Card Management System");
    }
}
