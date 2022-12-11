DO
$do$
BEGIN
   IF
EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'exercise') THEN

      RAISE NOTICE 'Role "exercise" already exists. Skipping.';
ELSE
CREATE ROLE exercise LOGIN PASSWORD 'password';
END IF;
END
$do$;

SELECT 'CREATE DATABASE exercise'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'exercise')\gexec

GRANT ALL PRIVILEGES ON DATABASE exercise TO exercise;

\c exercise;

CREATE TABLE IF NOT EXISTS product
(
    product_id    integer not null
    constraint product_pk
    primary key,
    product_name  varchar not null,
    category_name varchar not null
);


alter table product
    owner to exercise;

create table IF NOT EXISTS part
(
    punctuated_part_number varchar not null,
    part_description       varchar not null,
    product_id             integer not null
    constraint part_product_product_id_fk
    references product
    on delete cascade,
    original_retail_price  money   not null,
    brand_name             varchar not null,
    image_url              varchar not null,
    constraint part_pk
    primary key (punctuated_part_number, product_id)
    );

alter table part
    owner to exercise;