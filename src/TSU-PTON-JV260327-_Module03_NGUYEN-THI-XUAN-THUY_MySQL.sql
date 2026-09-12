CREATE DATABASE db_library_management;
USE db_library_management;

SELECT * FROM borrow_cards;

DROP DATABASE db_library_management;
DROP TABLE borrow_cards;
DROP PROCEDURE get_all_borrow_cards;
DROP PROCEDURE add_borrow_cards;
DROP PROCEDURE get_borrow_cards_by_borrower_name;
DROP PROCEDURE update_borrow_cards_by_card_id;
DROP PROCEDURE delete_borrow_cards;
DROP PROCEDURE search_borrow_cards_by_book_title;
DROP PROCEDURE  find_borrow_cards_by_card_id;

CREATE TABLE borrow_cards (
card_id INT PRIMARY KEY AUTO_INCREMENT,
book_title VARCHAR(150) NOT NULL,
borrower_name VARCHAR(100) NOT NULL,
borrow_date TIMESTAMP NOT NULL,
return_deadline TIMESTAMP NOT NULL,
quantity INT NOT NULL,
status VARCHAR(30) NOT NULL
)

 -- Procedure thực hiện lấy danh sách tất cả các phiếu mượn
  DELIMITER //
  CREATE PROCEDURE get_all_borrow_cards()
  BEGIN
  SELECT * FROM borrow_cards;
  END//
  DELIMITER ;

  
-- Procedure thực hiện thêm mới một phiếu mượn
DELIMITER //
CREATE PROCEDURE add_borrow_cards(
IN b_book_title VARCHAR(150),
IN b_borrower_name VARCHAR(100),
IN b_borrow_date TIMESTAMP,
IN b_return_deadline TIMESTAMP,
IN b_quantity INT,
IN b_status VARCHAR(30)
)
BEGIN
INSERT INTO borrow_cards (
book_title,
borrower_name ,
borrow_date,
return_deadline,
quantity,
status
)
VALUES (
b_book_title,
b_borrower_name ,
b_borrow_date,
b_return_deadline,
b_quantity,
b_status
);
END //
DELIMITER ;

-- Procedure thực hiện lấy danh sách phiếu mượn theo tên độc giả borrower_name
  DELIMITER //
  CREATE PROCEDURE get_borrow_cards_by_borrower_name(
  IN b_borrower_name VARCHAR(100)
  )
  BEGIN
  SELECT * FROM borrow_cards
  WHERE borrower_name LIKE CONCAT('%', b_borrower_name, '%');
  END//
  DELIMITER ;

-- Procedure thực hiện cập nhật thông tin phiếu mượn theo card_id
 DELIMITER //
  CREATE PROCEDURE update_borrow_cards_by_card_id (
  IN b_card_id INT,
IN b_book_title VARCHAR(150),
IN b_borrower_name VARCHAR(100),
IN b_borrow_date TIMESTAMP,
IN b_return_deadline TIMESTAMP,
IN b_quantity INT,
IN b_status VARCHAR(30)
  )
  
  BEGIN
UPDATE borrow_cards
SET 
card_id = b_card_id,
book_title = b_book_title,
borrower_name = b_borrower_name ,
borrow_date = b_borrow_date,
return_deadline = b_return_deadline,
quantity = b_quantity,
status = b_status
WHERE card_id = b_card_id;
  END//
  DELIMITER ;

-- Procedure thực hiện xóa phiếu mượn theo card_id
 DELIMITER //
  CREATE PROCEDURE delete_borrow_cards(
  IN b_card_id INT
  )
  BEGIN
DELETE FROM borrow_cards
WHERE  card_id = b_card_id;
  END//
  DELIMITER ;

-- Procedure thực hiện tìm kiếm phiếu mượn theo tên sách book_title (Tìm gần đúng, không phân biệt hoa thường)
 DELIMITER //
  CREATE PROCEDURE search_borrow_cards_by_book_title (
  IN b_book_title VARCHAR(150)
  )
  
  BEGIN
SELECT * FROM borrow_cards
WHERE book_title LIKE CONCAT('%', b_book_title, '%');
  END//
  DELIMITER ;
  
  -- TÌM phiếu mượn theo id
  DELIMITER //
 CREATE PROCEDURE find_borrow_cards_by_card_id(
IN b_card_id INT
 )
 BEGIN
 SELECT * FROM borrow_cards
 WHERE  card_id = b_card_id;
 END//
 DELIMITER ;