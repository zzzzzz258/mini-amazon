create table warehouse
(
    id  int primary key generated always as IDENTITY ,
    x   int,
    y   int
);
create table world_message
(
    sequence_num bigint primary key,
    acked        boolean default false,
    sent_time    bigint,
    simspeed     integer default null,
    disconnect   boolean default null
);
create table package (
  id            bigint primary key generated always as IDENTITY ,
  warehouse_id  int not null,
  order_id      int not null ,
  x             int not null,
  y             int not null,
  ups_account_name  text,
  pack_seq      bigint default null references world_message(sequence_num) on delete set null,
  is_packed      boolean default false ,
  truck_id      int,
  load_seq      bigint default null references world_message(sequence_num) on delete set null ,
  is_loaded      boolean default false
);
create table product
(
  package_id bigint references package(id) primary key,
  product_id bigint,
  description text,
  count      int,
  buy_seq    bigint default null references world_message(sequence_num) on delete set null,
  is_bought  boolean default false
);