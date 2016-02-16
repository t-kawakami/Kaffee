-- Project Name : PaperBaseball
-- Date/Time    : 2015/08/16 1:22:39
-- Author       : kawakami_note
-- RDBMS Type   : MySQL
-- Application  : A5:SQL Mk-2

-- 投球入力履歴
drop table if exists PITCHING cascade;

create table PITCHING (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , BALL_NUM INT(4) not null comment 'BALL_NUM'
  , COURSE_X INT(2) comment 'COURSE_X'
  , COURSE_Y INT(2) comment 'COURSE_Y'
  , BALL_KIND_ID INT(2) comment 'BALL_KIND_ID'
  , constraint PITCHING_PKC primary key (GAME_ID,BALL_NUM)
) comment '投球入力履歴' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- イニング結果
drop table if exists INNING_RESULT cascade;

create table INNING_RESULT (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , INNING INT(4) not null comment 'INNING'
  , TOP INT(2) default 1 not null comment 'TOP'
  , SCORE INT(4) default 0 comment 'SCORE'
  , HIT_NUM INT(4) default 0 comment 'HIT_NUM'
  , constraint INNING_RESULT_PKC primary key (GAME_ID,INNING,TOP)
) comment 'イニング結果' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 打撃結果
drop table if exists BATTING_RESULT cascade;

create table BATTING_RESULT (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , BALL_NUM INT(4) not null comment 'BALL_NUM'
  , STRIKE INT(2) default 0 comment 'STRIKE'
  , BALL INT(2) default 0 comment 'BALL'
  , FOUL INT(2) default 0 comment 'FOUL'
  , BATTING_RESULT_ID INT(2) comment 'BATTING_RESULT_ID'
  , constraint BATTING_RESULT_PKC primary key (GAME_ID,BALL_NUM)
) comment '打撃結果' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 打撃カウント
drop table if exists BATTING_COUNT cascade;

create table BATTING_COUNT (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , BALL_NUM INT(8) not null comment 'BALL_NUM'
  , INNING INT(4) not null comment 'INNING'
  , TOP INT(2) default 1 not null comment 'TOP'
  , STRIKE_COUNT INT(2) default 0 not null comment 'STRIKE_COUNT'
  , BALL_COUNT INT(2) default 0 not null comment 'BALL_COUNT'
  , OUT_COUNT INT(2) default 0 not null comment 'OUT_COUNT'
  , constraint BATTING_COUNT_PKC primary key (GAME_ID,BALL_NUM)
) comment '打撃カウント' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 打撃入力履歴
drop table if exists BATTING cascade;

create table BATTING (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , BALL_NUM INT(4) not null comment 'BALL_NUM'
  , COURSE_X INT(2) comment 'COURSE_X	 nullの場合は見逃し'
  , COURSE_Y INT(2) comment 'COURSE_Y	 nullの場合は見逃し'
  , BALL_KIND_ID INT(2) comment 'BALL_KIND_ID	 nullの場合は見逃し'
  , constraint BATTING_PKC primary key (GAME_ID,BALL_NUM)
) comment '打撃入力履歴' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 塁カウント
drop table if exists BASE_COUNT cascade;

create table BASE_COUNT (
  GAME_ID VARCHAR(10) not null comment 'GAME_ID'
  , BALL_NUM INT(4) not null comment 'BALL_NUM'
  , FIRST_BASE INT(2) default 0 comment 'FIRST_BASE'
  , SECOND_BASE INT(2) default 0 comment 'SECOND_BASE'
  , THIRD_BASE INT(2) default 0 comment 'THIRD_BASE'
  , constraint BASE_COUNT_PKC primary key (GAME_ID,BALL_NUM)
) comment '塁カウント' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 変化球種名
drop table if exists BALL_KIND_NAME cascade;

create table BALL_KIND_NAME (
  BALL_KIND_ID INT(4) not null comment 'BALL_KIND_ID'
  , BALL_KIND_NAME VARCHAR(32) comment 'BALL_KIND_NAME'
  , constraint BALL_KIND_NAME_PKC primary key (BALL_KIND_ID)
) comment '変化球種名' ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 変化球種
drop table if exists BALL_KIND cascade;

create table BALL_KIND (
  BALL_KIND_ID INT(4) not null comment 'BALL_KIND_ID'
  , COURSE_X_VECTOR INT(4) default 0 not null comment 'COURSE_X_VECTOR'
  , COURSE_Y_VECTOR INT(4) default 0 not null comment 'COURSE_Y_VECTOR'
  , RULE_ID INT(4) default 0 not null comment 'RULE_ID'
) comment '変化球種' ENGINE=InnoDB DEFAULT CHARSET=utf8;
