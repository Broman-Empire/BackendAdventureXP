-- Opretter testdata til AdventureXP

-- Opret aktiviteter
INSERT INTO activity (name, min_age, min_participants, max_participants, duration_minutes, parallel_courts)
VALUES
    ('Gokart', 12, 1, 12, 15, 5),
    ('Paintball', 16, 4, 20, 90, 2),
    ('Minigolf', 6, 1, 6, 60, 4),
    ('Sumo Wrestling', 10, 2, 4, 30, 1);
-- Udstyr til aktiviteter
INSERT INTO equipment (name, total_sets, usable_sets, activity_id)
VALUES
    ('Gokart sæt', 12, 12, 1),
    ('Paintball sæt', 20, 18, 2),
    ('Minigolf sæt', 24, 24, 3),
    ('Sumo sæt', 4, 4, 4);
-- Tidsrum (TimeSlots)
INSERT INTO time_slot (starts_at, ends_at, court, activity_id, capacity)
VALUES
    ('2025-10-05 10:00:00', '2025-10-05 11:30:00', 1, 1, 12),
    ('2025-10-05 10:00:00', '2025-10-05 11:30:00', 2, 1, 12),
    ('2025-10-05 12:00:00', '2025-10-05 13:00:00', 1, 2, 6),
    ('2025-10-05 14:00:00', '2025-10-05 14:45:00', 1, 3, 8);

-- Reservationer (kunder)
INSERT INTO reservation (customer_type, contact_name, email, phone, created_at)
VALUES
    ('Private', 'Sofie Hansen', 'sofie@example.com', '12345678', NOW()),
    ('Business', 'Adventure ApS', 'kontakt@adventure.dk', '87654321', NOW());

-- Bookinger knytter reservationer, aktiviteter og slots sammen
INSERT INTO booking (activity_id, participants, timeslot_id, reservation_id)
VALUES
    (1, 6, 1, 1),
    (2, 4, 3, 2);