-- use ayushblog;
-- show tables;
-- select * from users INNER JOIN appointments where appointments.status = 'PENDING';
-- select * from posts;
-- select * from users;
-- ALTER TABLE `ayushblog`.`posts` 
-- MODIFY COLUMN `description` TEXT;
-- 01:35:36	ALTER TABLE `ayushblog`.`posts` MODIFY COLUMN `description` TEXT;

-- ALTER TABLE `ayushblog`.`posts`
-- MODIFY COLUMN `description` LONGTEXT;

-- ALTER TABLE `ayushblog`.`posts`
-- MODIFY COLUMN `description` LONGTEXT;

-- select* from comments;
-- select * from categories;
-- drop table consultants;
-- select * from slot_consultants;
-- SELECT * FROM consultants;
-- -- INSERT INTO consultants (name, max_appointments_per_slot) 
-- -- VALUES ('Rupa Murghai', 1), ('Pankaj Suneja', 1);

-- SELECT slot_id 
-- FROM appointments 
-- WHERE slot_id NOT IN (SELECT id FROM slots);
-- SELECT * FROM appointments WHERE slot_id NOT IN (SELECT id FROM slots);
-- SET SQL_SAFE_UPDATES = 0;
-- DESCRIBE slots;
-- ALTER TABLE slots MODIFY COLUMN is_booked BIT(1) NOT NULL DEFAULT 0;
-- ALTER TABLE slots MODIFY COLUMN is_fully_booked BIT(1) NOT NULL DEFAULT 0;


-- ALTER TABLE appointments MODIFY phone_number VARCHAR(255) NULL;

-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE slot;
-- SET FOREIGN_KEY_CHECKS = 1;

-- select * from appointments;
-- select * from consultants;


-- select * from slot;
-- -- Replace 7699417617200 with the slot ID you want to check
-- SELECT id, is_booked, is_fully_booked, start_time, end_time 
-- FROM slots 
-- WHERE id = 7699417617200;

-- SELECT * FROM slots WHERE date = '2024-11-13' AND slot_time = '13:00' AND is_booked = 1;



-- CREATE TABLE slot (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     start_time DATETIME NOT NULL,
--     end_time DATETIME NOT NULL,
--     fully_booked BOOLEAN NOT NULL
-- );
-- INSERT INTO consultants (name)
-- VALUES ('Pankaj Suneja'), 
--        ('Rupa Murghai');




