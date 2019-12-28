--创建数据库
create database if not exists everything-plus;
--创建数据库表
drop table if exists  file-index;
create table if not exists file_index
(
    name varchar(256) not null commit '文件名称',
    path varchar(2048) not null commit '文件路径',
    depth int not null commit '文件路径深度',
    file-type varchar (32) not null commit '文件类型'
);