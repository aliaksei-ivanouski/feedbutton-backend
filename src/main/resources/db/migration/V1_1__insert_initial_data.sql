insert into "venue"("name")
values ('Gandofly'),('Belarussian Cusine'),('Blue Lagoon');

insert into "manager"("email", "password", "name", "last_name", "active", "age", "venue_id", "role")
values ('peter@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Peter', 'Balconskiy', true, 32, 1, 'WAITER'),
       ('vaser@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Vaser', 'Pitonskiy', true, 25, 1, 'ADMIN'),
       ('sam@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Sam', 'Drake', false, 43, 3, 'VENUE_MANAGER'),
       ('seth@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Seth', 'Armstrong', true, 11, 1, 'WAITER'),
       ('sam1@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Sam', 'Luis', false, 65, 2, 'WAITER'),
       ('james@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'James', 'Grant', false, 71, 2, 'WAITER'),
       ('justin@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Justin', 'Mons', true, 43, 2, 'VENUE_MANAGER'),
       ('peter1@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Peter', 'Luis', true, 15, 2, 'ADMIN'),
       ('thomas@gmail.com', '{bcrypt}$2a$10$Iue7Rkg1gYw2eA9RA5Bza.ye22Gm1MZf2n2MYlVUZxoNNnKIyR7ta', 'Thomas', 'Stone', true, 9, 3, 'ADMIN');
