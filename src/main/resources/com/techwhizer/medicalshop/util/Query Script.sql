/* DATABASE DETAILS

        DB_USERNAME =postgres
        DB_PASSWORD =postgres
        DB_PORT = 5432
        DB_NAME = medical_shop
*/

drop table if exists tbl_cart cascade;
drop table if exists tbl_company cascade;
drop table if exists tbl_dealer cascade;
drop table if exists tbl_discount cascade;
drop table if exists tbl_doctor cascade;
drop table if exists tbl_items_master cascade;
drop table if exists tbl_license cascade;
drop table if exists tbl_manufacturer_list cascade;
drop table if exists tbl_mr_list cascade;
drop table if exists tbl_patient cascade;
drop table if exists TBL_PRODUCT_TAX cascade;
drop table if exists tbl_purchase_items cascade;
drop table if exists tbl_purchase_main cascade;
drop table if exists tbl_role cascade;
drop table if exists tbl_sale_items cascade;
drop table if exists tbl_sale_main cascade;
drop table if exists tbl_shop_details cascade;
drop table if exists tbl_stock cascade;
drop table if exists tbl_users cascade;

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
VALUES ('Admin', 'Account', 'admin', 'MALE', 'admin@gmail.com', 1234567899, 'admin');

CREATE TABLE TBL_SHOP_DETAILS
(
    shop_id           serial primary key  not null,
    SHOP_NAME         VARCHAR(100) unique NOT NULL,
    SHOP_EMAIL        VARCHAR(100) unique NOT NULL,
    SHOP_PHONE_1      VARCHAR(40) unique  NOT NULL,
    SHOP_PHONE_2      VARCHAR(40),
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
    CARE_OF         VARCHAR(100),
    WEIGHT          VARCHAR(50),
    BP              VARCHAR(50),
    PULSE           VARCHAR(50),
    SUGAR           VARCHAR(50),
    SPO2            VARCHAR(100),
    TEMP            VARCHAR(100),
    CVS             VARCHAR(100),
    CNS             VARCHAR(100),
    CHEST           VARCHAR(100),
    invoice_number  varchar(50),
    registered_date timestamp default CURRENT_TIMESTAMP
);

CREATE TABLE TBL_CART
(
    CART_ID      BIGSERIAL PRIMARY KEY NOT NULL,
    stock_id     INTEGER               NOT NULL,
    MRP          NUMERIC               NOT NULL,
    STRIP        INT,
    PCS          INT,

    CREATED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_SALE_MAIN
(
    SALE_MAIN_ID        SERIAL PRIMARY KEY                  NOT NULL,
    PATIENT_ID          INTEGER                             NOT NULL,
    DOCTOR_ID           INT,
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

    PACK          VARCHAR(200),
    MFR_ID        INT,
    BATCH         VARCHAR(200),
    EXPIRY_DATE   VARCHAR(50),

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
    stock_id      integer not null ,

    FOREIGN KEY (SALE_MAIN_ID)
        REFERENCES TBL_SALE_MAIN (SALE_MAIN_ID),
    FOREIGN KEY (ITEM_ID)
        REFERENCES tbl_items_master (item_id)
);

CREATE TABLE TBL_DOCTOR
(
    DOCTOR_ID     SERIAL PRIMARY KEY,
    DR_NAME       VARCHAR(100) NOT NULL,
    DR_PHONE      varchar(20),
    DR_ADDRESS    VARCHAR(200),
    DR_REG_NUM    VARCHAR(100),
    SPECIALITY    VARCHAR(100),
    QUALIFICATION VARCHAR(300),
    CREATED_DATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
