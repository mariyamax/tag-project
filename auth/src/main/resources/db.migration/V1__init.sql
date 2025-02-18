--Создаем базового админа
-- Почта - admin@mail.ru
-- Пароль - password
INSERT INTO users(email, password, role)
VALUES ('admin@mail.ru', '4297f870a34fabc1071ecc7700d3cdd7', 'ADMIN');

INSERT INTO users(email, password, role)
VALUES ('assesor@mail.ru', '4297f870a34fabc1071ecc7700d3cdd7', 'ASSESSOR_MAIN');