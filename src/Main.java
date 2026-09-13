import org.jetbrains.annotations.NotNull;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.InputMismatchException;
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
                    if (addBorrowCards(sc)) {
                        System.out.println("Thêm phiếu mượn thành công");
                    } else {
                        System.out.println("Thêm phiếu mượn thất bại!");
                    }
                    break;

                case 3:
                    updateBorrowCards(sc);
                    break;

                case 4:
                    deleteBorrowCards(sc);
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

    public static void displayBorrowCards() {
        if (ConnectionDB.conn == null) {
            System.out.println("Lỗi: Chưa kết nối được với Database!!");
            return;
        }

        String sqlDisplay = "{CALL get_all_borrow_cards()}";
        try (
                Connection conn = ConnectionDB.getConnection()
        ) {
            assert conn != null;
            try (CallableStatement stmt = conn.prepareCall(sqlDisplay);
                 ResultSet rs = stmt.executeQuery()
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


    public static boolean addBorrowCards(@NotNull Scanner sc) {

        BorrowCards borrowCards = new BorrowCards();

        System.out.println("Nhập tên sách:");
        BorrowCards.setBook_title(sc.nextLine());

        System.out.println("Nhập tên độc giả:");
        BorrowCards.setBorrower_name(sc.nextLine());

        System.out.println("Nhập ngày mượn (yyyy-MM-dd):");
        BorrowCards.setBorrow_date(
                LocalDate.parse(sc.nextLine())
        );

        System.out.println("Nhập hạn trả (yyyy-MM-dd):");
        BorrowCards.setReturn_deadline(
                LocalDate.parse(sc.nextLine())
        );

        System.out.println("Nhập số lượng:");
        BorrowCards.setQuantity(
                Integer.parseInt(sc.nextLine())
        );

        System.out.println("Nhập trạng thái:");
        BorrowCards.setStatus(sc.nextLine());

        String sql = "{CALL add_borrow_cards(?,?,?,?,?,?)}";

        try (
                Connection conn = ConnectionDB.getConnection()
        ) {
            assert conn != null;
            try (CallableStatement stmt = conn.prepareCall(sql)
            ) {

                stmt.setString(1, BorrowCards.getBook_title());
                stmt.setString(2, BorrowCards.getBorrower_name());
                stmt.setDate(3, Date.valueOf(BorrowCards.getBorrow_date()));
                stmt.setDate(4, Date.valueOf(BorrowCards.getReturn_deadline()));
                stmt.setInt(5, BorrowCards.getQuantity());
                stmt.setString(6, BorrowCards.getStatus());

                return stmt.executeUpdate() > 0;

            }
        } catch (SQLException e) {
            System.out.println("Lỗi SQL " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void updateBorrowCards(Scanner sc) {

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
        String sqlFind = "{CALL find_borrow_cards_by_card_id(?)}";
        Connection conn = ConnectionDB.getConnection();
        assert conn != null;
        try (CallableStatement stmt = conn.prepareCall(sqlFind)
        ) {
            stmt.setInt(1, cardId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println(
                            "Không tìm thấy thẻ mượn nào có ID: " + cardId);
                    return;
                }

                BorrowCards.setCard_id(rs.getInt("card_id"));
                BorrowCards.setBook_title(rs.getString("book_title"));
                BorrowCards.setBorrower_name(rs.getString("borrower_name"));
                BorrowCards.setBorrow_date(
                        rs.getDate("borrow_date").toLocalDate()
                );
                BorrowCards.setReturn_deadline(
                        rs.getDate("return_deadline").toLocalDate()
                );
                BorrowCards.setQuantity(rs.getInt("quantity"));
                BorrowCards.setStatus(rs.getString("status"));
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

            BorrowCards.setBook_title(bookTitle);
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

            BorrowCards.setBorrower_name(borrowerName);
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

        BorrowCards.setBorrow_date(borrowDate);

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

        BorrowCards.setReturn_deadline(returnDeadline);

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

                BorrowCards.setQuantity(quantity);
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

            BorrowCards.setStatus(newStatus);
            break;
        }

        // 11. Update database
        String sqlUpdate =
                "{CALL update_borrow_cards_by_card_id(?,?,?,?,?,?,?)}";

        try (
                CallableStatement stmt = conn.prepareCall(sqlUpdate)
        ) {

            stmt.setInt(1, BorrowCards.getCard_id());
            stmt.setString(2, BorrowCards.getBook_title());
            stmt.setString(3, BorrowCards.getBorrower_name());
            stmt.setDate(4, Date.valueOf(BorrowCards.getBorrow_date()));
            stmt.setDate(5, Date.valueOf(BorrowCards.getReturn_deadline()));
            stmt.setInt(6, BorrowCards.getQuantity());
            stmt.setString(7, BorrowCards.getStatus());

            stmt.executeUpdate();

            System.out.println("Cập nhật thẻ mượn thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
    }

    public static void deleteBorrowCards(Scanner sc) {

        // 1. Kiểm tra kết nối
        if (ConnectionDB.conn == null) {
            System.out.println("Lỗi: Chưa kết nối được với database");
            return;
        }

        // 2. Nhập card_id
        int cardId;

        while (true) {
            System.out.println("Nhập mã card_id cần xóa: ");

            try {
                cardId = sc.nextInt();

                if (cardId <= 0) {
                    System.out.println("card_id phải lớn hơn 0!!");
                    continue;
                }

                break;

            } catch (InputMismatchException e) {
                System.out.println(
                        "card_id không hợp lệ! Vui lòng nhập số nguyên!"
                );
                sc.nextLine();
            }
        }

        // 3. Tạo object
        BorrowCards card = new BorrowCards();

        // 4. Tìm phiếu theo ID
        String sqlFind = "{CALL find_borrow_cards_by_card_id(?)}";

        Connection conn = ConnectionDB.getConnection();

        try (CallableStatement stmt = conn.prepareCall(sqlFind)) {

            stmt.setInt(1, cardId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println(
                            "Không tìm thấy thẻ mượn nào có ID: " + cardId
                    );
                    return;
                }

                // ⭐ Lưu dữ liệu vào OBJECT card
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

        // 5. Xóa Enter còn sót lại sau nextInt()
        sc.nextLine();

        // 6. Xác nhận trước khi xóa
        System.out.println(
                "Bạn có chắc muốn xóa phiếu mượn có card_id là "
                        + cardId + " không?"
        );

        System.out.println("Nhập yes để xác nhận:");

        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Đã hủy thao tác xóa!");
            return;
        }

        // 7. Xóa phiếu mượn
        String sqlDelete = "{CALL delete_borrow_cards(?)}";

        try (CallableStatement stmt = conn.prepareCall(sqlDelete)) {

            stmt.setInt(1, card.getCard_id());

            stmt.executeUpdate();

            System.out.println("Xóa phiếu mượn thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
    }
}










