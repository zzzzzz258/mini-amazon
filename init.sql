create table world_message
(
    sequence_num bigint primary key,
    acked        boolean default false,
    sent_time    bigint,
    simspeed     integer default null,
    disconnect   boolean default null
);
create table package (
  id            bigint primary key,
  warehouse_id  int not null,
  order_id      int not null ,
  pack_seq      bigint default null references world_message(sequence_num) on delete set null,
  isPacked      boolean default false ,
  load_seq      bigint default null references world_message(sequence_num) on delete set null ,
  isLoaded      boolean default false
);
create table product
(
  id            bigint not null,
  description   text,
  count         int not null,
  package_id    bigint primary key references package(id),
  buy_seq       bigint default null references world_message(sequence_num) on delete set null,
  isBought      boolean default false
);