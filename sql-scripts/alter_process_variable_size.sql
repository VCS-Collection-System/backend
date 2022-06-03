--alter the variable_instance_log table columns value, old value size to store more than 255 characters

alter table VariableInstanceLog 
alter column value type varchar(4000);


alter table VariableInstanceLog 
alter column oldvalue type varchar(4000);