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
                                       user_id UUID NOT NULL,
                                       start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                       end_time TIMESTAMP WITH TIME ZONE NOT NULL
                                       --CONSTRAINT fk_meeting_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE: meeting_participants
-- =====================================================
CREATE TABLE IF NOT EXISTS meeting_participants (
                                                    meeting_id UUID NOT NULL,
                                                    participant UUID NOT NULL,
                                                    PRIMARY KEY (meeting_id, participant)
                                                    --FOREIGN KEY (meeting_id) REFERENCES meeting(id) ON DELETE CASCADE,
                                                    --FOREIGN KEY (participant) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- TABLE: time_slot
-- =====================================================
CREATE TABLE IF NOT EXISTS time_slot (
                                         id UUID PRIMARY KEY,
                                         user_id UUID NOT NULL,
                                         start_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                         end_time TIMESTAMP WITH TIME ZONE NOT NULL,
                                         busy BOOLEAN NOT NULL
                                         --CONSTRAINT fk_timeslot_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES
-- =====================================================
-- Meeting indexes