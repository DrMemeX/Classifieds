-- Demo data. Password for all seeded accounts: Andrei1234
-- The password column stores a BCrypt hash, not the plain password.

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-admin1', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-admin2', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-user1', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'USER', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-user2', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'USER', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-user3', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'USER', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-user4', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'USER', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO users (login, password, role, status, created_at)
VALUES ('andrei-user5', '$2a$10$km6D564BAzA7KAgDAPxf0.p1RKHFUNaanWAKNv2fptFnF.SVpBVsy', 'USER', 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-admin1'), 'Андрей', 'Администраторов', '+79000000001');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-admin2'), 'Алексей', 'Модераторов', '+79000000002');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-user1'), 'Иван', 'Иванов', '+79000000011');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-user2'), 'Мария', 'Петрова', '+79000000012');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-user3'), 'Сергей', 'Смирнов', '+79000000013');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-user4'), 'Ольга', 'Соколова', '+79000000014');

INSERT INTO user_profiles (id, first_name, last_name, phone)
VALUES ((SELECT id FROM users WHERE login = 'andrei-user5'), 'Дмитрий', 'Кузнецов', '+79000000015');

INSERT INTO categories (name, parent_id, active)
VALUES ('Электроника', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Транспорт', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Недвижимость', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Для дома', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Хобби и отдых', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Одежда и обувь', NULL, TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Смартфоны', (SELECT id FROM categories WHERE name = 'Электроника'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Ноутбуки', (SELECT id FROM categories WHERE name = 'Электроника'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Компьютеры', (SELECT id FROM categories WHERE name = 'Электроника'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Телевизоры', (SELECT id FROM categories WHERE name = 'Электроника'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Автомобили', (SELECT id FROM categories WHERE name = 'Транспорт'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Мотоциклы', (SELECT id FROM categories WHERE name = 'Транспорт'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Велосипеды', (SELECT id FROM categories WHERE name = 'Транспорт'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Квартиры', (SELECT id FROM categories WHERE name = 'Недвижимость'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Дома', (SELECT id FROM categories WHERE name = 'Недвижимость'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Мебель', (SELECT id FROM categories WHERE name = 'Для дома'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Бытовая техника', (SELECT id FROM categories WHERE name = 'Для дома'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Книги', (SELECT id FROM categories WHERE name = 'Хобби и отдых'), TRUE);

INSERT INTO categories (name, parent_id, active)
VALUES ('Спорт', (SELECT id FROM categories WHERE name = 'Хобби и отдых'), TRUE);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Смартфон Samsung Galaxy 1',
    'Демонстрационное объявление 1',
    1500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Ноутбук Lenovo 2',
    'Демонстрационное объявление 2',
    2000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Игровой компьютер 3',
    'Демонстрационное объявление 3',
    2500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Телевизор LG 4',
    'Демонстрационное объявление 4',
    3000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Автомобиль Toyota 5',
    'Демонстрационное объявление 5',
    3500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Мотоциклы'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Мотоцикл Yamaha 6',
    'Демонстрационное объявление 6',
    4000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Велосипеды'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Горный велосипед 7',
    'Демонстрационное объявление 7',
    4500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Квартиры'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Квартира 8',
    'Демонстрационное объявление 8',
    5000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Дома'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Загородный дом 9',
    'Демонстрационное объявление 9',
    5500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Мебель'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Диван 10',
    'Демонстрационное объявление 10',
    6000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Бытовая техника'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Стиральная машина 11',
    'Демонстрационное объявление 11',
    6500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Книги'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Книги 12',
    'Демонстрационное объявление 12',
    7000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Спорт'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Гантели 13',
    'Демонстрационное объявление 13',
    7500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Электроника'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Наушники 14',
    'Демонстрационное объявление 14',
    8000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Транспорт'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Автомобиль Kia 15',
    'Демонстрационное объявление 15',
    8500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Недвижимость'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Комната 16',
    'Демонстрационное объявление 16',
    9000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Для дома'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Стол 17',
    'Демонстрационное объявление 17',
    9500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Хобби и отдых'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Гитара 18',
    'Демонстрационное объявление 18',
    10000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Одежда и обувь'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Куртка 19',
    'Демонстрационное объявление 19',
    10500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Смартфон Samsung Galaxy 20',
    'Демонстрационное объявление 20',
    11000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Ноутбук Lenovo 21',
    'Демонстрационное объявление 21',
    11500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Игровой компьютер 22',
    'Демонстрационное объявление 22',
    12000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Телевизор LG 23',
    'Демонстрационное объявление 23',
    12500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Автомобиль Toyota 24',
    'Демонстрационное объявление 24',
    13000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Мотоциклы'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Мотоцикл Yamaha 25',
    'Демонстрационное объявление 25',
    13500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Велосипеды'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Горный велосипед 26',
    'Демонстрационное объявление 26',
    14000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Квартиры'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Квартира 27',
    'Демонстрационное объявление 27',
    14500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Дома'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Загородный дом 28',
    'Демонстрационное объявление 28',
    15000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Мебель'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Диван 29',
    'Демонстрационное объявление 29',
    15500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Бытовая техника'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Стиральная машина 30',
    'Демонстрационное объявление 30',
    16000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Книги'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Книги 31',
    'Демонстрационное объявление 31',
    16500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Спорт'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Гантели 32',
    'Демонстрационное объявление 32',
    17000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Электроника'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Наушники 33',
    'Демонстрационное объявление 33',
    17500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Транспорт'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Автомобиль Kia 34',
    'Демонстрационное объявление 34',
    18000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Недвижимость'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Комната 35',
    'Демонстрационное объявление 35',
    18500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Для дома'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Стол 36',
    'Демонстрационное объявление 36',
    19000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Хобби и отдых'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Гитара 37',
    'Демонстрационное объявление 37',
    19500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Одежда и обувь'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Куртка 38',
    'Демонстрационное объявление 38',
    20000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Смартфон Samsung Galaxy 39',
    'Демонстрационное объявление 39',
    20500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Ноутбук Lenovo 40',
    'Демонстрационное объявление 40',
    21000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Игровой компьютер 41',
    'Демонстрационное объявление 41',
    21500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Телевизор LG 42',
    'Демонстрационное объявление 42',
    22000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Автомобиль Toyota 43',
    'Демонстрационное объявление 43',
    22500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Мотоциклы'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Мотоцикл Yamaha 44',
    'Демонстрационное объявление 44',
    23000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Велосипеды'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Горный велосипед 45',
    'Демонстрационное объявление 45',
    23500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Квартиры'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Квартира 46',
    'Демонстрационное объявление 46',
    24000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Дома'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Загородный дом 47',
    'Демонстрационное объявление 47',
    24500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Мебель'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Диван 48',
    'Демонстрационное объявление 48',
    25000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Бытовая техника'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Стиральная машина 49',
    'Демонстрационное объявление 49',
    25500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Книги'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Книги 50',
    'Демонстрационное объявление 50',
    26000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Спорт'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Гантели 51',
    'Демонстрационное объявление 51',
    26500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Электроника'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Наушники 52',
    'Демонстрационное объявление 52',
    27000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Транспорт'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Автомобиль Kia 53',
    'Демонстрационное объявление 53',
    27500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Недвижимость'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Комната 54',
    'Демонстрационное объявление 54',
    28000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Для дома'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Стол 55',
    'Демонстрационное объявление 55',
    28500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Хобби и отдых'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Гитара 56',
    'Демонстрационное объявление 56',
    29000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Одежда и обувь'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Куртка 57',
    'Демонстрационное объявление 57',
    29500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Смартфон Samsung Galaxy 58',
    'Демонстрационное объявление 58',
    30000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Ноутбук Lenovo 59',
    'Демонстрационное объявление 59',
    30500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Игровой компьютер 60',
    'Демонстрационное объявление 60',
    31000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Телевизор LG 61',
    'Демонстрационное объявление 61',
    31500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Автомобиль Toyota 62',
    'Демонстрационное объявление 62',
    32000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Мотоциклы'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Мотоцикл Yamaha 63',
    'Демонстрационное объявление 63',
    32500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Велосипеды'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Горный велосипед 64',
    'Демонстрационное объявление 64',
    33000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Квартиры'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Квартира 65',
    'Демонстрационное объявление 65',
    33500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Дома'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Загородный дом 66',
    'Демонстрационное объявление 66',
    34000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Мебель'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Диван 67',
    'Демонстрационное объявление 67',
    34500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Бытовая техника'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Стиральная машина 68',
    'Демонстрационное объявление 68',
    35000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Книги'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Книги 69',
    'Демонстрационное объявление 69',
    35500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Спорт'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Гантели 70',
    'Демонстрационное объявление 70',
    36000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Электроника'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Наушники 71',
    'Демонстрационное объявление 71',
    36500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Транспорт'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Автомобиль Kia 72',
    'Демонстрационное объявление 72',
    37000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Недвижимость'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Комната 73',
    'Демонстрационное объявление 73',
    37500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Для дома'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Стол 74',
    'Демонстрационное объявление 74',
    38000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Хобби и отдых'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Гитара 75',
    'Демонстрационное объявление 75',
    38500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Одежда и обувь'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Куртка 76',
    'Демонстрационное объявление 76',
    39000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Смартфон Samsung Galaxy 77',
    'Демонстрационное объявление 77',
    39500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Ноутбук Lenovo 78',
    'Демонстрационное объявление 78',
    40000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Игровой компьютер 79',
    'Демонстрационное объявление 79',
    40500.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Телевизор LG 80',
    'Демонстрационное объявление 80',
    41000.00,
    'ACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Автомобиль Toyota 81',
    'Демонстрационное объявление 81',
    41500.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Мотоциклы'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Мотоцикл Yamaha 82',
    'Демонстрационное объявление 82',
    42000.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Велосипеды'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Горный велосипед 83',
    'Демонстрационное объявление 83',
    42500.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Квартиры'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Квартира 84',
    'Демонстрационное объявление 84',
    43000.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Дома'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Загородный дом 85',
    'Демонстрационное объявление 85',
    43500.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Мебель'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Диван 86',
    'Демонстрационное объявление 86',
    44000.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Бытовая техника'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Стиральная машина 87',
    'Демонстрационное объявление 87',
    44500.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Книги'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Книги 88',
    'Демонстрационное объявление 88',
    45000.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Спорт'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Гантели 89',
    'Демонстрационное объявление 89',
    45500.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Электроника'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Наушники 90',
    'Демонстрационное объявление 90',
    46000.00,
    'INACTIVE',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Транспорт'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Автомобиль Kia 91',
    'Демонстрационное объявление 91',
    46500.00,
    'BLOCKED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Недвижимость'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Комната 92',
    'Демонстрационное объявление 92',
    47000.00,
    'BLOCKED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Для дома'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Стол 93',
    'Демонстрационное объявление 93',
    47500.00,
    'BLOCKED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Хобби и отдых'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Гитара 94',
    'Демонстрационное объявление 94',
    48000.00,
    'BLOCKED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Одежда и обувь'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Куртка 95',
    'Демонстрационное объявление 95',
    48500.00,
    'BLOCKED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user1'),
    (SELECT id FROM categories WHERE name = 'Смартфоны'),
    (SELECT id FROM regions WHERE name = 'Москва'),
    'Москва',
    'Смартфон Samsung Galaxy 96',
    'Демонстрационное объявление 96',
    49000.00,
    'DELETED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user2'),
    (SELECT id FROM categories WHERE name = 'Ноутбуки'),
    (SELECT id FROM regions WHERE name = 'Московская область'),
    'Химки',
    'Ноутбук Lenovo 97',
    'Демонстрационное объявление 97',
    49500.00,
    'DELETED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user3'),
    (SELECT id FROM categories WHERE name = 'Компьютеры'),
    (SELECT id FROM regions WHERE name = 'Санкт-Петербург'),
    'Санкт-Петербург',
    'Игровой компьютер 98',
    'Демонстрационное объявление 98',
    50000.00,
    'DELETED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user4'),
    (SELECT id FROM categories WHERE name = 'Телевизоры'),
    (SELECT id FROM regions WHERE name = 'Орловская область'),
    'Орёл',
    'Телевизор LG 99',
    'Демонстрационное объявление 99',
    50500.00,
    'DELETED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisements (seller_id, category_id, region_id, locality, title, description, price, status, created_at, updated_at)
VALUES (
    (SELECT id FROM users WHERE login = 'andrei-user5'),
    (SELECT id FROM categories WHERE name = 'Автомобили'),
    (SELECT id FROM regions WHERE name = 'Краснодарский край'),
    'Краснодар',
    'Автомобиль Toyota 100',
    'Демонстрационное объявление 100',
    51000.00,
    'DELETED',
    CURRENT_TIMESTAMP,
    NULL
);

INSERT INTO advertisement_images (advertisement_id, object_key, display_order)
VALUES ((SELECT id FROM advertisements WHERE title = 'Смартфон Samsung Galaxy 1'), 'demo/image-1.jpg', 1);

INSERT INTO advertisement_images (advertisement_id, object_key, display_order)
VALUES ((SELECT id FROM advertisements WHERE title = 'Ноутбук Lenovo 2'), 'demo/image-2.jpg', 1);

INSERT INTO advertisement_images (advertisement_id, object_key, display_order)
VALUES ((SELECT id FROM advertisements WHERE title = 'Игровой компьютер 3'), 'demo/image-3.jpg', 1);

INSERT INTO advertisement_images (advertisement_id, object_key, display_order)
VALUES ((SELECT id FROM advertisements WHERE title = 'Телевизор LG 4'), 'demo/image-4.jpg', 1);

INSERT INTO advertisement_images (advertisement_id, object_key, display_order)
VALUES ((SELECT id FROM advertisements WHERE title = 'Автомобиль Toyota 5'), 'demo/image-5.jpg', 1);

INSERT INTO comments (advertisement_id, author_id, text, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Смартфон Samsung Galaxy 1'), (SELECT id FROM users WHERE login = 'andrei-user2'), 'Объявление актуально?', CURRENT_TIMESTAMP);

INSERT INTO comments (advertisement_id, author_id, text, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Ноутбук Lenovo 2'), (SELECT id FROM users WHERE login = 'andrei-user3'), 'Объявление актуально?', CURRENT_TIMESTAMP);

INSERT INTO comments (advertisement_id, author_id, text, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Игровой компьютер 3'), (SELECT id FROM users WHERE login = 'andrei-user4'), 'Объявление актуально?', CURRENT_TIMESTAMP);

INSERT INTO comments (advertisement_id, author_id, text, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Телевизор LG 4'), (SELECT id FROM users WHERE login = 'andrei-user5'), 'Объявление актуально?', CURRENT_TIMESTAMP);

INSERT INTO comments (advertisement_id, author_id, text, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Автомобиль Toyota 5'), (SELECT id FROM users WHERE login = 'andrei-user1'), 'Объявление актуально?', CURRENT_TIMESTAMP);

INSERT INTO conversations (advertisement_id, buyer_id, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Смартфон Samsung Galaxy 1'), (SELECT id FROM users WHERE login = 'andrei-user2'), CURRENT_TIMESTAMP);

INSERT INTO conversations (advertisement_id, buyer_id, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Ноутбук Lenovo 2'), (SELECT id FROM users WHERE login = 'andrei-user3'), CURRENT_TIMESTAMP);

INSERT INTO conversations (advertisement_id, buyer_id, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Игровой компьютер 3'), (SELECT id FROM users WHERE login = 'andrei-user4'), CURRENT_TIMESTAMP);

INSERT INTO conversations (advertisement_id, buyer_id, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Телевизор LG 4'), (SELECT id FROM users WHERE login = 'andrei-user5'), CURRENT_TIMESTAMP);

INSERT INTO conversations (advertisement_id, buyer_id, created_at)
VALUES ((SELECT id FROM advertisements WHERE title = 'Автомобиль Toyota 5'), (SELECT id FROM users WHERE login = 'andrei-user1'), CURRENT_TIMESTAMP);

-- Messages are not inserted here because migration 039 stores AES-GCM encrypted text and IV.
-- Create messages through the REST API after startup.