ALTER TABLE guest_book
ADD COLUMN is_anonymous TINYINT DEFAULT 0 NOT NULL COMMENT '是否匿名：0=否，1=是' AFTER status;
