-- =====================================================
-- EXAMPLE DATA: USERS
-- =====================================================
INSERT INTO users (id, name, email, address) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Alice Smith', 'alice@example.com', '123 Main St, City, Country')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, address) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Bob Johnson', 'bob@example.com', '456 Oak Ave, City, Country')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, address) VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Charlie Brown', 'charlie@example.com', '789 Pine Rd, City, Country')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, address) VALUES
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Diana Prince', 'diana@example.com', '101 Maple St, City, Country')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, address) VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Eve Adams', 'eve@example.com', '202 Elm St, City, Country')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, email, address) VALUES
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Frank Miller', 'frank@example.com', '303 Birch Blvd, City, Country')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- EXAMPLE DATA: MEETINGS
-- =====================================================
INSERT INTO meeting (id, title, description, organizer_id, user_id, start_time, end_time) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Team Meeting', 'Weekly team sync', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '2025-09-22 09:00:00+00', '2025-09-22 10:00:00+00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO meeting (id, title, description, organizer_id, user_id, start_time, end_time) VALUES
    ('22222222-2222-2222-2222-222222222222', 'Project Kickoff', 'Kickoff meeting for new project', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ffffffff-ffff-ffff-ffff-ffffffffffff', '2025-09-23 14:00:00+00', '2025-09-23 15:30:00+00')
ON CONFLICT (id) DO NOTHING;

-- =====================================================
-- EXAMPLE DATA: MEETING PARTICIPANTS
-- =====================================================
INSERT INTO meeting_participants (meeting_id, participant) VALUES
    ('11111111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc')
ON CONFLICT (meeting_id, participant) DO NOTHING;

INSERT INTO meeting_participants (meeting_id, participant) VALUES
    ('11111111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd')
ON CONFLICT (meeting_id, participant) DO NOTHING;

INSERT INTO meeting_participants (meeting_id, participant) VALUES
    ('22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc')
ON CONFLICT (meeting_id, participant) DO NOTHING;

-- =====================================================
-- EXAMPLE DATA: TIME SLOTS
-- =====================================================
INSERT INTO time_slot (id, user_id, start_time, end_time, busy) VALUES
    ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '2025-09-22 08:00:00+00', '2025-09-22 09:30:00+00', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO time_slot (id, user_id, start_time, end_time, busy) VALUES
    ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '2025-09-22 10:00:00+00', '2025-09-22 11:00:00+00', FALSE)
ON CONFLICT (id) DO NOTHING;
