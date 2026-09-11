import java.time.LocalDate;

public class BorrowCards {
    static int card_id;
    static String book_title;
    static String borrower_name;
    static LocalDate borrow_date;
    static LocalDate return_deadline;
    static int quantity;
    static String status;
    public int getBook_title;

    public BorrowCards(int card_id, String book_title, String borrower_name, LocalDate borrow_date, LocalDate return_deadline, int quantity, String status) {

        this.card_id = card_id;
        this.book_title = book_title;
        this.borrower_name = borrower_name;
        this.borrow_date = borrow_date;
        this.return_deadline = return_deadline;
        this.quantity = quantity;
        this.status = status;

    }

    public BorrowCards() {

    }

    public static int getCard_id() {
        return card_id;
    }

    public static void setCard_id(int card_id) {
        BorrowCards.card_id = card_id;
    }

    public static String getBook_title() {
        return book_title;
    }

    public static void setBook_title(String book_title) {
        BorrowCards.book_title = book_title;
    }

    public static String getBorrower_name() {
        return borrower_name;
    }

    public static void setBorrower_name(String borrower_name) {
        BorrowCards.borrower_name = borrower_name;
    }

    public static LocalDate getBorrow_date() {
        return borrow_date;
    }

    public static void setBorrow_date(LocalDate borrow_date) {
        BorrowCards.borrow_date = borrow_date;
    }

    public static LocalDate getReturn_deadline() {
        return return_deadline;
    }

    public static void setReturn_deadline(LocalDate return_deadline) {
        BorrowCards.return_deadline = return_deadline;
    }

    public static int getQuantity() {
        return quantity;
    }

    public static void setQuantity(int quantity) {
        BorrowCards.quantity = quantity;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        BorrowCards.status = status;
    }

    @Override
    public String toString() {
        return "BorrowCards{"
                + "card_id=" + card_id
                + ", book_title=" + book_title
                + ", borrower_name=" + borrower_name
                + ", borrow_date=" + borrow_date
                + ", return_deadline=" + return_deadline
                + ", quantity=" + quantity
                + ", status=" + status + '}';

    }

    public void card_id(int cardId) {
    }

}


