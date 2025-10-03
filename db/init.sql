-- Opretter testdata til AdventureXP

-- Opret aktiviteter
INSERT INTO activity (id, name, min_age, min_participants, max_participants, duration_minutes, parallel_courts)
VALUES
    (1, 'Gokart', 12, 1, 12, 15, 5),
    (2, 'Paintball', 16, 4, 20, 90, 2),
    (3, 'Minigolf', 6, 1, 6, 60, 4),
    (4, 'Sumo Wrestling', 10, 2, 4, 30, 1);
-- Udstyr til aktiviteter
INSERT INTO equipment (id, name, total_sets, usable_sets, activity_id)
VALUES
    (1, 'Gokart sæt', 12, 12, 1),
    (2, 'Paintball sæt', 20, 18, 2),
    (3, 'Minigolf sæt', 24, 24, 3),
    (4, 'Sumo sæt', 4, 4, 4);
-- Tidsrum (TimeSlots)
INSERT INTO timeslot (id, starts_at, ends_at, unit_number, activity_id, capacity)
VALUES
    (1, '2025-10-05 10:00:00', '2025-10-05 11:30:00', 1, 1, 12),
    (2, '2025-10-05 10:00:00', '2025-10-05 11:30:00', 2, 1, 12),
    (3, '2025-10-05 12:00:00', '2025-10-05 13:00:00', 1, 2, 6),
    (4, '2025-10-05 14:00:00', '2025-10-05 14:45:00', 1, 3, 8);

-- Reservationer (kunder)
INSERT INTO reservation (id, customer_type, contact_name, email, phone, created_at)
VALUES
    (1, 'Private', 'Sofie Hansen', 'sofie@example.com', '12345678', NOW()),
    (2, 'Business', 'Adventure ApS', 'kontakt@adventure.dk', '87654321', NOW());

-- Bookinger knytter reservationer, aktiviteter og slots sammen
INSERT INTO booking (id, activity_id, participants, timeslot_id, reservation_id)
VALUES
    (1, 1, 6, 1, 1),
    (2, 2, 4, 3, 2);