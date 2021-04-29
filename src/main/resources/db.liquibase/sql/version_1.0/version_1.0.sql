DROP TABLE IF EXISTS customer;
CREATE TABLE customer (
  customer_id bigint NOT NULL,
  created_at timestamp NOT NULL ,
  PRIMARY KEY (customer_id)
)