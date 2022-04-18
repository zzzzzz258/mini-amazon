create table world_message
(
    sequence_num bigint primary key,
    acked        boolean default false,
    sent_time    bigint,
    simspeed     integer default null,
    disconnect   boolean default null
);