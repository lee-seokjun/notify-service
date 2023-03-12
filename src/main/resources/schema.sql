CREATE table IF NOT EXISTS cofound_notify (
    notify_id varchar(50) primary key,
    receiver_id varchar(50) NOT NULL,
    created_by varchar(50) NOT NULL,
    created_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    message varchar(500) NOT NULL,
    noty_type varchar(15) NOT NULL,
    read_falg varchar(1) NOT NULL
);