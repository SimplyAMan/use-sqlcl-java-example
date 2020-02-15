set serveroutput on
begin
  $if dbms_db_version.version < 18 $then
    dbms_output.put_line('version less than Oracle 18');
  $else
    dbms_output.put_line('version high than Oracle 18');
  $end
end;
/ 