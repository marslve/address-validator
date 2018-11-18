CREATE TABLE `default.openaddr_part`(
  `street` string, 
  `number` string, 
  `postcode` int, 
  `city` string)
COMMENT 'addresses partitioned by country'
PARTITIONED BY ( 
  `country` string)
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.ql.io.orc.OrcSerde' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'
LOCATION
  'hdfs://localhost:9000/user/hadoop/openaddresses/final'
TBLPROPERTIES (
  'bucketing_version'='2', 
  'transient_lastDdlTime'='1542562579');
