-- =====================================================
-- TABLE: users
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     name TEXT NOT NULL,
                                     email TEXT NOT NULL UNIQUE,
                                     address TEXT,
                                     created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
                                     updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- =====================================================
-- TABLE: meeting
-- =====================================================
CREATE TABLE IF NOT EXISTS meeting (
                                       id UUID PRIMARY KEY,
                                       title TEXT,
                                       description TEXT,
                                       organizer_id UUID NOT NULL,
                                       user_id UUID NOT NULL,
                                       start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                       end_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                       CONSTRAINT fk_meeting_organizer FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE,
                                       CONSTRAINT fk_meeting_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE: meeting_participants
-- =====================================================
CREATE TABLE IF NOT EXISTS meeting_participants (
                                                    meeting_id UUID NOT NULL,
                                                    participant UUID NOT NULL,
                                                    PRIMARY KEY (meeting_id, participant),
                                                    FOREIGN KEY (meeting_id) REFERENCES meeting(id) ON DELETE CASCADE,
                                                    FOREIGN KEY (participant) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE: time_slot
-- =====================================================
CREATE TABLE IF NOT EXISTS time_slot (
                                         id UUID PRIMARY KEY,
                                         user_id UUID NOT NULL,
                                         start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                         end_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                         busy BOOLEAN NOT NULL,
                                         CONSTRAINT fk_timeslot_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================
-- Meeting indexes
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_meeting_organizer' AND n.nspname = 'public') THEN
            CREATE INDEX idx_meeting_organizer ON meeting(organizer_id);
        END IF;
    END$$;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_meeting_user' AND n.nspname = 'public') THEN
            CREATE INDEX idx_meeting_user ON meeting(user_id);
        END IF;
    END$$;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_meeting_participant' AND n.nspname = 'public') THEN
            CREATE INDEX idx_meeting_participant ON meeting_participants(participant);
        END IF;
    END$$;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_meeting_start_end' AND n.nspname = 'public') THEN
            CREATE INDEX idx_meeting_start_end ON meeting(start_time, end_time);
        END IF;
    END$$;

-- TimeSlot indexes
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_timeslot_user' AND n.nspname = 'public') THEN
            CREATE INDEX idx_timeslot_user ON time_slot(user_id);
        END IF;
    END$$;

DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                       WHERE c.relname = 'idx_timeslot_start_end' AND n.nspname = 'public') THEN
            CREATE INDEX idx_timeslot_start_end ON time_slot(start_time, end_time);
        END IF;
    END$$;