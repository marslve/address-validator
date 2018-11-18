CREATE EXTERNAL TABLE `default.openaddr_raw`(
  `lon` decimal(9,7), 
  `lat` decimal(9,7), 
  `number` string, 
  `street` string, 
  `unit` string, 
  `city` string, 
  `district` string, 
  `region` string, 
  `postcode` int, 
  `id` string, 
  `hash` string)
COMMENT 'openaddresses raw'
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' 
WITH SERDEPROPERTIES ( 
  'field.delim'=',', 
  'serialization.format'=',') 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
TBLPROPERTIES (
  'bucketing_version'='2', 
  'last_modified_by'='hadoop', 
  'last_modified_time'='1542563949', 
  'skip.header.line.count'='1', 
  'transient_lastDdlTime'='1542563949');
