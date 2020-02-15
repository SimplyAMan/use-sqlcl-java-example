PROMPT Run script 02_get_instance.sql

SELECT 'instance name - ' || instance_name || ', host name - ' || host_name AS info
  FROM v$instance
/