CREATE TABLE Carrier
(
    Id       INT     NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    Name     VARCHAR(50) NOT NULL,
    Password VARCHAR(50) NOT NULL
);

CREATE TABLE Order
(
    Id          INT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    OrderNumber INT     NOT NULL,
    CarrierId   INT     NOT NULL AUTO_INCREMENT,
    State       VARCHAR(50) NOT NULL
);
