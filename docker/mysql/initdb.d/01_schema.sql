CREATE TABLE demo (
code VARCHAR(5) NOT NULL,
value VARCHAR(11) NULL,
update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);