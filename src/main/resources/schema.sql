CREATE TABLE IF NOT EXISTS Student(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name_customer VARCHAR(100) NOT NULL ,
    address_customer VARCHAR(200) NOT NULL
);
CREATE TABLE IF NOT EXISTS Customer(
      id INT PRIMARY KEY AUTO_INCREMENT,
      name_customer VARCHAR(100) NOT NULL ,
      address_customer VARCHAR(200) NOT NULL
);