
-- before migration

create table temp_task_info_for_migration AS
	(select t.id, t.description from task t
	where t.name  = 'REVIEW VACCINATION CARD'
	 and t.createdon < '2021-12-09 00:00:00');
	
-- after migration

UPDATE
    task
SET
    t.description = ttifm.description
FROM
    task t
INNER JOIN
    temp_task_info_for_migration ttifm
ON 
    t.id = ttifm.id;
   
drop table temp_task_info_for_migration;
