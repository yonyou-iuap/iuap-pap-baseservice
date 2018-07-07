create table BASE_ATTACHMENT
(
  id               VARCHAR2(64) not null,
  filename         VARCHAR2(100),
  accessaddress    VARCHAR2(100),
  refid            VARCHAR2(100),
  refname          VARCHAR2(100),
  create_time      VARCHAR2(64),
  create_user      VARCHAR2(64),
  last_modified    VARCHAR2(64),
  last_modify_user VARCHAR2(64),
  ts               VARCHAR2(64),
  dr               NUMBER(11)
)