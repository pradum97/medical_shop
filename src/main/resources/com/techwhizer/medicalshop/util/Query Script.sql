/*DROP TABLE IF EXISTS TBL_SHOP_DETAILS;
DROP TABLE IF EXISTS MENU_CATEGORY;
DROP TABLE IF EXISTS TBL_MENU;
DROP TABLE IF EXISTS TBL_FEEDBACK;
DROP TABLE IF EXISTS TBL_ROLE;
DROP TABLE IF EXISTS TBL_CART;
DROP TABLE IF EXISTS TBL_PCS_MRP;
DROP TABLE IF EXISTS TBL_PACK_MRP;
DROP TABLE IF EXISTS TBL_WEIGHT_MRP;
DROP TABLE IF EXISTS TBL_COUPON;
DROP TABLE IF EXISTS TBL_DUES;
DROP TABLE IF EXISTS DUES_HISTORY;
DROP TABLE IF EXISTS tbl_dealer;
DROP TABLE IF EXISTS PURCHASE_HISTORY;
DROP TABLE IF EXISTS TBL_CUSTOMER;
DROP TABLE IF EXISTS kitty_party_items;
DROP TABLE IF EXISTS kitty_party_main;
DROP TABLE IF EXISTS TBL_LICENSE;
DROP TABLE IF EXISTS tbl_sale_Items;
DROP TABLE IF EXISTS TBL_RETURN_ITEMS;
DROP TABLE IF EXISTS TBL_RETURN_MAIN;
DROP TABLE IF EXISTS TBL_SALE_MAIN;
DROP TABLE IF EXISTS TBL_PRODUCTS;
DROP TABLE IF EXISTS TBL_PRODUCT_TAX;
DROP TABLE IF EXISTS TBL_CATEGORY;
DROP TABLE IF EXISTS TBL_USERS;
DROP TABLE IF EXISTS TBL_PAPER_TYPE;
DROP TABLE IF EXISTS TBL_UPI;
DROP TABLE IF EXISTS TBL_CRM_SETTING;*/

/* DATABASE DETAILS

        DB_USERNAME =postgres
        DB_PASSWORD =postgres
        DB_PORT = 5432
        DB_NAME = hotel_management
*/

CREATE TABLE tbl_users
(
    USER_ID        SERIAL PRIMARY KEY       NOT NULL,
    FIRST_NAME     VARCHAR(50)              NOT NULL,
    LAST_NAME      VARCHAR(50),
    USERNAME       VARCHAR(100)             NOT NULL UNIQUE,
    GENDER         VARCHAR(6)               NOT NULL,
    role_id        integer      default 1   NOT NULL,
    EMAIL          VARCHAR(100) UNIQUE      NOT NULL,
    PHONE          bigint UNIQUE            NOT NULL,
    PASSWORD       VARCHAR(200)             NOT NULL,
    FULL_ADDRESS   VARCHAR(200) default null,
    USER_IMG_PATH  TEXT         default 'avtar_3.png',
    CREATED_TIME   timestamp    DEFAULT CURRENT_TIMESTAMP,
    ACCOUNT_STATUS int          default '1' NOT NULL,
    MAC_ADDRESS    VARCHAR(30)
);

/*CREATE ADMIN*/
INSERT INTO tbl_users(first_name, last_name, username, gender, email, phone, password)
VALUES ('PRADUM', 'KUMAR', 'admin', 'MALE', 'admin@gmail.com', 1234567899, 'admin');

CREATE TABLE TBL_SHOP_DETAILS
(
    shop_id           serial primary key  not null,
    SHOP_NAME         VARCHAR(100) unique NOT NULL,
    SHOP_EMAIL        VARCHAR(100) unique NOT NULL,
    SHOP_PHONE_1      VARCHAR(10) unique  NOT NULL,
    SHOP_PHONE_2      VARCHAR(10),
    SHOP_GST_NUMBER   VARCHAR(100),
    SHOP_FOOD_LICENCE VARCHAR(100),
    SHOP_DRUG_LICENCE VARCHAR(100),
    SHOP_ADDRESS      VARCHAR(200) unique NOT NULL
);

CREATE TABLE TBL_PRODUCT_TAX
(
    TAX_ID      SERIAL PRIMARY KEY NOT NULL,
    SGST        NUMERIC            NOT NULL,
    CGST        NUMERIC            NOT NULL,
    IGST        NUMERIC            NOT NULL,
    gstName     VARCHAR(100),
    HSN_SAC     NUMERIC UNIQUE     not null,
    DESCRIPTION VARCHAR(200)
);

CREATE TABLE TBL_ROLE
(
    ROLE_ID SERIAL PRIMARY KEY NOT NULL,
    ROLE    VARCHAR(50) unique
);

INSERT INTO TBL_ROLE (ROLE)
VALUES ('ADMIN'),
       ('STAFF');

CREATE TABLE tbl_dealer
(
    dealer_id    SERIAL PRIMARY KEY,
    dealer_name  varchar(100) not null,
    dealer_phone varchar(20)  not null,
    dealer_email varchar(100),
    dealer_dl    varchar(100),
    dealer_gstNo varchar(100),
    ADDRESS      TEXT         NOT NULL,
    STATE        VARCHAR(20),
    ADDED_DATE   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tbl_company
(
    company_id      serial primary key,
    company_name    varchar(200) not null,
    company_address varchar(200) not null,
    created_date    timestamp default current_timestamp
);

CREATE TABLE TBL_DISCOUNT
(

    DISCOUNT_ID   SERIAL PRIMARY KEY NOT NULL,
    DISCOUNT      NUMERIC            NOT NULL,
    DISCOUNT_NAME VARCHAR(200)       not null,
    description   varchar(200),
    created_date  timestamp default current_timestamp

);

CREATE TABLE TBL_ITEMS_MASTER
(
    ITEM_ID      SERIAL PRIMARY KEY,
    ITEMS_NAME   VARCHAR(300) NOT NULL,
    UNIT         VARCHAR(100) NOT NULL,
    STRIP_TAB    NUMERIC   default 0,
    PACKING      VARCHAR(100) NOT NULL,
    COMPANY_ID   INT,
    MFR_ID       INT,
    DISCOUNT_ID  INT,
    MR_ID        INT,
    GST_ID       INT          NOT NULL,
    TYPE         VARCHAR(50)  NOT NULL,
    NARCOTIC     VARCHAR(50)  NOT NULL,
    ITEM_TYPE    VARCHAR(50)  NOT NULL,
    STATUS       INT          NOT NULL,
    CREATED_BY   INT,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IS_ACTIVE    INT       DEFAULT 1,
    foreign key (GST_ID) REFERENCES tbl_product_tax (TAX_ID),
    foreign key (CREATED_BY) REFERENCES tbl_users (user_id),
    foreign key (MFR_ID) REFERENCES tbl_manufacturer_list (MFR_ID),
    foreign key (MR_ID) REFERENCES tbl_mr_list (MR_ID)
);

CREATE TABLE tbl_manufacturer_list
(
    mfr_id            serial primary key,
    manufacturer_name varchar(200),
    created_date      timestamp default current_timestamp
);
CREATE TABLE tbl_mr_list
(
    mr_id        serial primary key,
    name         varchar(200) not null,
    phone        varchar(20),
    gender       varchar(20)  not null,
    email        varchar(100),
    company      varchar(100),
    address      varchar(200),
    created_date timestamp default current_timestamp
);

CREATE TABLE TBL_PURCHASE_MAIN
(
    PURCHASE_MAIN_ID SERIAL PRIMARY KEY,
    DEALER_ID        INT         NOT NULL,
    BILL_NUM         VARCHAR(50) NOT NULL,
    DEALER_BILL_NUM  VARCHAR(50),
    BILL_DATE        VARCHAR(15) NOT NULL,
    CREATED_DATE     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IS_ACTIVE        INT       DEFAULT 1,
    FOREIGN KEY (DEALER_ID) REFERENCES tbl_dealer (DEALER_ID)
);

CREATE TABLE TBL_PURCHASE_ITEMS
(
    PURCHASE_ITEMS_ID SERIAL PRIMARY KEY,
    PURCHASE_MAIN_ID  INT          NOT NULL,
    ITEM_ID           INT          NOT NULL,
    BATCH             VARCHAR(100) NOT NULL,
    EXPIRY_DATE       VARCHAR(50)  NOT NULL,
    LOT_NUMBER        VARCHAR(50),
    PURCHASE_RATE     NUMERIC,
    MRP               NUMERIC,
    SALE_PRICE        NUMERIC,
    QUANTITY          NUMERIC      NOT NULL,
    QUANTITY_UNIT     VARCHAR(20)  NOT NULL,
    FOREIGN KEY (PURCHASE_MAIN_ID) REFERENCES TBL_PURCHASE_MAIN (PURCHASE_MAIN_ID),
    FOREIGN KEY (ITEM_ID) REFERENCES TBL_ITEMS_MASTER (ITEM_ID)
);

CREATE TABLE TBL_STOCK
(
    STOCK_ID          SERIAL PRIMARY KEY,
    ITEM_ID           INT         NOT NULL,
    PURCHASE_MAIN_ID  INT         NOT NULL,
    PURCHASE_ITEMS_ID INT         NOT NULL,
    QUANTITY          NUMERIC     NOT NULL,
    QUANTITY_UNIT     VARCHAR(20) NOT NULL,
    UPDATE_DATE       VARCHAR(20) NOT NULL,
    FOREIGN KEY (ITEM_ID) REFERENCES TBL_ITEMS_MASTER (ITEM_ID),
    FOREIGN KEY (PURCHASE_MAIN_ID) REFERENCES TBL_PURCHASE_MAIN (PURCHASE_MAIN_ID),
    FOREIGN KEY (PURCHASE_ITEMS_ID) REFERENCES TBL_PURCHASE_ITEMS (PURCHASE_ITEMS_ID)
);

CREATE TABLE TBL_PATIENT
(
    PATIENT_ID      SERIAL PRIMARY KEY NOT NULL,
    NAME            VARCHAR(100)       NOT NULL,
    PHONE           VARCHAR(30),
    ADDRESS         VARCHAR(200),
    ID_NUMBER       VARCHAR(100),
    GENDER          VARCHAR(10),
    AGE             VARCHAR(5),
    DISCOUNT        NUMERIC,
    CARE_OF         VARCHAR(100),
    WEIGHT          VARCHAR(50),
    BP              VARCHAR(50),
    PULSE           VARCHAR(50),
    SUGAR           VARCHAR(50),
    registered_date timestamp default CURRENT_TIMESTAMP
);

CREATE TABLE TBL_CART
(
    CART_ID      BIGSERIAL PRIMARY KEY NOT NULL,
    ITEM_ID      INTEGER               NOT NULL,
    MRP          NUMERIC               NOT NULL,
    STRIP        INT,
    PCS          INT,
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE TBL_SALE_MAIN
(
    SALE_MAIN_ID        SERIAL PRIMARY KEY                  NOT NULL,
    PATIENT_ID          INTEGER                             NOT NULL,
    SELLER_ID           INTEGER                             NOT NULL,
    ADDITIONAL_DISCOUNT NUMERIC,
    PAYMENT_MODE        VARCHAR                             NOT NULL,
    TOT_TAX_AMOUNT      NUMERIC,
    NET_AMOUNT          NUMERIC                             NOT NULL,
    INVOICE_NUMBER      VARCHAR(100)                        NOT NULL,
    BILL_TYPE           VARCHAR(100)                        NOT NULL,
    sale_date           timestamp default CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (SELLER_ID)
        REFERENCES tbl_users (user_id)
);

CREATE TABLE TBL_SALE_ITEMS
(
    SALE_ITEM_ID  BIGSERIAL PRIMARY KEY               NOT NULL,
    SALE_MAIN_ID  INTEGER                             NOT NULL,
    ITEM_ID       INTEGER                             NOT NULL,
    ITEM_NAME     VARCHAR(200)                        NOT NULL,
    PURCHASE_RATE NUMERIC                             NOT NULL,
    MRP           NUMERIC   DEFAULT 0                 NOT NULL,
    SALE_RATE     NUMERIC                             NOT NULL,
    STRIP         INT       default 0,
    PCS           INT       default 0,
    DISCOUNT      numeric,
    HSN_SAC       NUMERIC,
    igst          NUMERIC,
    sgst          NUMERIC,
    cgst          NUMERIC,
    STRIP_TAB     INT,
    NET_AMOUNT    NUMERIC                             NOT NULL,
    TAX_AMOUNT    NUMERIC,
    sale_date     timestamp default CURRENT_TIMESTAMP NOT NULL,

    FOREIGN KEY (SALE_MAIN_ID)
        REFERENCES TBL_SALE_MAIN (SALE_MAIN_ID),
    FOREIGN KEY (ITEM_ID)
        REFERENCES tbl_items_master (item_id)
);

CREATE TABLE TBL_DOCTOR(
    DOCTOR_ID SERIAL PRIMARY KEY ,
    DR_NAME VARCHAR(100) NOT NULL ,
    DR_PHONE varchar(20),
    DR_ADDRESS VARCHAR(200),
    DR_REG_NUM VARCHAR(100),
    SPECIALITY VARCHAR(100),
    QUALIFICATION VARCHAR(300),
    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- top finished


CREATE TABLE TBL_DUES
(
    DUES_ID        SERIAL PRIMARY KEY NOT NULL,
    CUSTOMER_ID    INTEGER            NOT NULL,
    SALE_MAIN_ID   INTEGER            NOT NULL,
    DUES_AMOUNT    NUMERIC,
    DUES_NOTES     VARCHAR(300),
    INVOICE_NUMBER VARCHAR(100),
    PAYMENT_MODE   VARCHAR(50)        NOT NULL,
    LAST_PAYMENT   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CUSTOMER_ID)
        REFERENCES tbl_customer (CUSTOMER_ID)
);

CREATE TABLE DUES_HISTORY
(
    DUES_HISTORY_ID BIGSERIAL PRIMARY KEY NOT NULL,
    DUES_ID         INTEGER               NOT NULL,
    CUSTOMER_ID     INTEGER               NOT NULL,
    SALE_MAIN_ID    INTEGER               NOT NULL,
    PREVIOUS_DUES   NUMERIC               NOT NULL,
    PAID_AMOUNT     NUMERIC               NOT NULL,
    CURRENT_DUES    NUMERIC               NOT NULL,
    PAYMENT_MODE    VARCHAR               NOT NULL,
    PAYMENT_DATE    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CUSTOMER_ID)
        REFERENCES tbl_customer (CUSTOMER_ID)

);

CREATE TABLE TBL_LICENSE
(
    LICENSE_ID          SERIAL PRIMARY KEY,
    COMPANY_ID          INT unique          NOT NULL,
    APPLICATION_ID      VARCHAR(50) unique  NOT NULL,
    SERIAL_KEY          VARCHAR(100) unique NOT NULL,
    START_ON            VARCHAR(20)         NOT NULL,
    EXPIRES_ON          VARCHAR(20)         NOT NULL,
    LICENSE_TYPE        VARCHAR(50)         NOT NULL,
    LICENSE_PERIOD_DAYS INTEGER             NOT NULL,
    REGISTERED_EMAIL    VARCHAR(100)        NOT NULL
);


CREATE TABLE TBL_RETURN_MAIN
(
    RETURN_MAIN_ID      SERIAL PRIMARY KEY,
    SALE_MAIN_ID        INTEGER NOT NULL,
    seller_id           integer not null,
    CREATED_DATE        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TOTAL_REFUND_AMOUNT NUMERIC NOT NULL,
    OLD_INVOICE_NUMBER  VARCHAR(50),
    INVOICE_NUMBER      TEXT    NOT NULL,
    REMARK              TEXT,
    foreign key (SALE_MAIN_ID) references tbl_sale_main (SALE_MAIN_ID),
    foreign key (seller_id) references tbl_users (user_id)
);

CREATE TABLE TBL_RETURN_ITEMS
(
    return_items_id SERIAL PRIMARY KEY,
    RETURN_MAIN_ID  INTEGER NOT NULL,
    PRODUCT_ID      INTEGER NOT NULL,
    RETURN_QUANTITY INTEGER NOT NULL,
    SALE_ITEM_ID    INTEGER NOT NULL,
    quantity_Unit   varchar(10),
    RATE            NUMERIC NOT NULL,
    foreign key (RETURN_MAIN_ID) references TBL_RETURN_MAIN (RETURN_MAIN_ID),
    foreign key (PRODUCT_ID) references TBL_PRODUCTs (PRODUCT_ID)
);

CREATE TABLE TBL_PAPER_TYPE
(
    PAPER_ID   SERIAL PRIMARY KEY,
    PAPER_TYPE VARCHAR(100) unique NOT NULL
);

INSERT INTO TBL_PAPER_TYPE(PAPER_TYPE)
VALUES ('RECEIPT');

CREATE TABLE TBL_UPI
(
    U_ID        SERIAL PRIMARY KEY,
    UPI_ID      VARCHAR(200) unique NOT NULL,
    PAYEE_NAME  VARCHAR(200) unique NOT NULL,
    UPDATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_CRM_SETTING
(
    CRM_SETTING_ID SERIAL PRIMARY KEY,
    CRM_TYPE       VARCHAR(100),
    UPDATE_TIME    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO TBL_CRM_SETTING(CRM_TYPE)
VALUES ('MANUAL');



