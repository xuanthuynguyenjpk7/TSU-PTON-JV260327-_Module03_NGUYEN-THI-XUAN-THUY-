import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("================= LIBRARY MANAGEMENT =================");
            System.out.println("1. Danh sách tất cả phiếu mượn");
            System.out.println("2. Thêm mới phiếu mượn");
            System.out.println("3. Cập nhật thông tin phiếu mượn");
            System.out.println("4. Xóa phiếu mượn");
            System.out.println("5. Tìm kiếm phiếu mượn theo tên độc giả");
            System.out.println("6. Tìm kiếm phiếu mượn theo tên sách");
            System.out.println("7. Thoát");
            System.out.println("Nhập lựa chọn của bạn: ");

            choice = sc.nextInt();
            sc.nextLine(); // QUAN TRỌNG

            switch (choice) {

                case 1:
                    displayBorrowCards();
                    break;

                case 2:
                    addBorrowCards(sc);
                    break;

                case 3:
                    updateBorrowCards(sc);
                    break;

                case 4:
                    System.out.println("Xóa");
                    break;

                case 5:
                    System.out.println("Tìm kiếm theo tên độc giả");
                    break;

                case 6:
                    System.out.println("Tìm kiếm theo tên sách");
                    break;

                case 7:
                    System.out.println("Thoát");
                    break;

                default:
                    System.out.println("Vui lòng chọn từ 1 đến 7!");
            }

        } while (choice != 7);

        sc.close();
    }

    public static void displayBorrowCards() throws SQLException {
        if (ConnectionDB.conn == null) {
            System.out.println("Lỗi: Chưa kết nối được với Database!!");
            return;
        }

        String sqlDisplay = "{CALL get_all_borrow_cards()}";
        try (
                Connection conn = ConnectionDB.getConnection();
        ) {
            try (CallableStatement stmt = ConnectionDB.conn.prepareCall(sqlDisplay);
                 ResultSet rs = stmt.executeQuery();
            ) {
                System.out.println("--- ĐÃ KẾT NỐI VÀ CHẠY PROCEDURE ---");
                while (rs.next()) {
                    int cardId = rs.getInt("card_id");
                    String bookTitle = rs.getString("book_title");
                    String borrowerName = rs.getString("borrower_name");
                    LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
                    LocalDate returnDeadline = rs.getDate("return_deadline").toLocalDate();
                    int quantity = rs.getInt("quantity");
                    String status = rs.getString("status");
                    System.out.println("card_id: " + cardId + " book_title" + bookTitle + " borrower_name " + borrowerName + " borrow_date" + borrowDate + " return_deadline " + returnDeadline + " quantity " + quantity + " status " + status);
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }


    public static boolean addBorrowCards(Scanner sc) throws SQLException {

        BorrowCards borrowCards = new BorrowCards();

        System.out.println("Nhập tên sách:");
        borrowCards.setBook_title(sc.nextLine());

        System.out.println("Nhập tên độc giả:");
        borrowCards.setBorrower_name(sc.nextLine());

        System.out.println("Nhập ngày mượn (yyyy-MM-dd):");
        borrowCards.setBorrow_date(
                LocalDate.parse(sc.nextLine())
        );

        System.out.println("Nhập hạn trả (yyyy-MM-dd):");
        borrowCards.setReturn_deadline(
                LocalDate.parse(sc.nextLine())
        );

        System.out.println("Nhập số lượng:");
        borrowCards.setQuantity(
                Integer.parseInt(sc.nextLine())
        );

        System.out.println("Nhập trạng thái:");
        borrowCards.setStatus(sc.nextLine());

        String sql = "{CALL add_borrow_cards(?,?,?,?,?,?)}";

        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement stmt = conn.prepareCall(sql)
        ) {

            stmt.setString(1, borrowCards.getBook_title());
            stmt.setString(2, borrowCards.getBorrower_name());
            stmt.setDate(3, Date.valueOf(borrowCards.getBorrow_date().toEpochDay));
            stmt.setDate(4, Date.valueOf(borrowCards.getReturn_deadline().toLocalDate()));
            stmt.setInt(5, borrowCards.getQuantity());
            stmt.setString(6, borrowCards.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void updateBorrowCards(Scanner sc) throws SQLException {

        // 1. Kiểm tra kết nối
        if (ConnectionDB.conn == null) {
            System.out.println("Lỗi: Chưa kết nối được với Database!!");
            return;
        }

        // 2. Nhập card_id
        int cardId;

        while (true) {
            System.out.println("Nhập id phiếu mượn cần sửa:");

            try {
                cardId = Integer.parseInt(sc.nextLine().trim());

                if (cardId <= 0) {
                    System.out.println("ID phải lớn hơn 0!");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("ID không hợp lệ, vui lòng nhập số nguyên!");
            }
        }

        // 3. Tạo object
        BorrowCards card = new BorrowCards();

        // 4. Tìm phiếu theo ID
        String sqlFind = "{CALL find_borrow_card_by_card_id(?)}";

        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement stmt = conn.prepareCall(sqlFind)
        ) {

            stmt.setInt(1, cardId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println(
                            "Không tìm thấy thẻ mượn nào có ID: " + cardId
                    );
                    return;
                }

                card.setCard_id(rs.getInt("card_id"));
                card.setBook_title(rs.getString("book_title"));
                card.setBorrower_name(rs.getString("borrower_name"));
                card.setBorrow_date(
                        rs.getDate("borrow_date").toLocalDate()
                );
                card.setReturn_deadline(
                        rs.getDate("return_deadline").toLocalDate()
                );
                card.setQuantity(rs.getInt("quantity"));
                card.setStatus(rs.getString("status"));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            return;
        }

        // 5. Nhập tên sách mới
        while (true) {

            System.out.println("Nhập tên sách mới:");
            String bookTitle = sc.nextLine().trim();

            if (bookTitle.isEmpty()) {
                System.out.println("Tên sách không được để trống!");
                continue;
            }

            card.setBook_title(bookTitle);
            break;
        }

        // 6. Nhập tên độc giả mới
        while (true) {

            System.out.println("Nhập tên độc giả mới:");
            String borrowerName = sc.nextLine().trim();

            if (borrowerName.isEmpty()) {
                System.out.println("Tên độc giả không được để trống!");
                continue;
            }

            card.setBorrower_name(borrowerName);
            break;
        }

        // 7. Nhập ngày mượn mới
        LocalDate borrowDate = null;

        while (borrowDate == null) {

            System.out.println("Nhập ngày mượn mới (yyyy-MM-dd):");
            String dateInput = sc.nextLine().trim();

            try {
                borrowDate = LocalDate.parse(dateInput);

            } catch (Exception e) {
                System.out.println("Ngày không hợp lệ! Vui lòng nhập lại!");
            }
        }

        card.setBorrow_date(borrowDate);

        // 8. Nhập hạn trả mới
        LocalDate returnDeadline = null;

        while (returnDeadline == null) {

            System.out.println("Nhập ngày trả mới (yyyy-MM-dd):");
            String dateInput = sc.nextLine().trim();

            try {
                returnDeadline = LocalDate.parse(dateInput);

            } catch (Exception e) {
                System.out.println("Ngày không hợp lệ! Vui lòng nhập lại!");
            }
        }

        card.setReturn_deadline(returnDeadline);

        // 9. Nhập quantity mới
        while (true) {

            System.out.println("Nhập số lượng mới:");
            String newQuantity = sc.nextLine().trim();

            try {

                int quantity = Integer.parseInt(newQuantity);

                if (quantity <= 0) {
                    System.out.println("Số lượng phải lớn hơn 0!");
                    continue;
                }

                card.setQuantity(quantity);
                break;

            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên!");
            }
        }

        // 10. Nhập status mới
        while (true) {

            System.out.println("Nhập status mới:");
            String newStatus = sc.nextLine().trim();

            if (newStatus.isEmpty()) {
                System.out.println("Status không được để trống!");
                continue;
            }

            card.setStatus(newStatus);
            break;
        }

        // 11. Update database
        String sqlUpdate =
                "{CALL update_borrow_cards_by_card_id(?,?,?,?,?,?,?)}";

        try (
                Connection conn = ConnectionDB.getConnection();
                CallableStatement stmt = conn.prepareCall(sqlUpdate)
        ) {

            stmt.setInt(1, card.getCard_id());
            stmt.setString(2, card.getBook_title());
            stmt.setString(3, card.getBorrower_name());
            stmt.setDate(4, Date.valueOf(card.getBorrow_date().toLocalDate()));
            stmt.setDate(5, Date.valueOf(card.getReturn_deadline().toLocalDate()));
            stmt.setInt(6, card.getQuantity());
            stmt.setString(7, card.getStatus());

            stmt.executeUpdate();

            System.out.println("Cập nhật thẻ mượn thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
    }
}









