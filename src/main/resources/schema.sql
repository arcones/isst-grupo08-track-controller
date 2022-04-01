CREATE TABLE Carrier
(
    Id       INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name     VARCHAR(50) NOT NULL,
    Password VARCHAR(50) NOT NULL
);

CREATE TABLE Parcel
(
    Id           INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Order_Number INT UNIQUE  NOT NULL,
    Carrier_Id   INT         NOT NULL AUTO_INCREMENT,
    State        VARCHAR(50) NOT NULL,
    foreign key (Carrier_Id) references Carrier(id)
);
