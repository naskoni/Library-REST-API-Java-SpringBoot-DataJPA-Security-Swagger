INSERT INTO library_user
    (id, created, updated, name, username, password, role, status)
VALUES (1, '2020-03-03 14:00:00.000', '2020-03-03 14:00:00.000',
        'admin', 'admin',
        '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
        'ROLE_ADMIN', 'ACTIVE'),
       (2, '2020-03-03 14:00:00.000', '2020-03-03 14:00:00.000',
        'user', 'user',
        '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb',
        'ROLE_USER', 'ACTIVE');