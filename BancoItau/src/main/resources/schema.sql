create table PIX(
    id bigint AUTO_INCREMENT unique ,
    reqId bigint unique ,
    tipo varchar,
    agencia varchar,
    conta varchar,
    chave varchar primary key not null ,
    status varchar

);

create table CONTA(
    id bigint AUTO_INCREMENT unique ,
    nome varchar,
    cpf varchar primary key not null ,
    email varchar unique ,
    numeroConta varchar,
    agencia varchar
);