-- ─────────────────────────────────────────────────────────────────────────────
-- Deep Thought Entertainment Group — schema.sql
-- INFO 5100 Final Project — Java Huskies
--
-- SQLite dialect. Safe to re-run: all tables use CREATE TABLE IF NOT EXISTS.
-- Execute in dependency order: network → enterprise → org → user → all others.
-- PersistenceService.initializeSchema() runs this on first launch.
-- ─────────────────────────────────────────────────────────────────────────────

PRAGMA foreign_keys = ON;

-- ── 1. Network ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS network (
    network_id      TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    headquarters    TEXT,
    founded         TEXT,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 2. Enterprise ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS enterprise (
    enterprise_id   TEXT PRIMARY KEY,
    network_id      TEXT NOT NULL REFERENCES network(network_id),
    name            TEXT NOT NULL,
    type            TEXT,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 3. Organization ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS organization (
    org_id          TEXT PRIMARY KEY,
    enterprise_id   TEXT NOT NULL REFERENCES enterprise(enterprise_id),
    name            TEXT NOT NULL,
    type            TEXT,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 4. User (staff) ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user (
    user_id         TEXT PRIMARY KEY,
    org_id          TEXT NOT NULL REFERENCES organization(org_id),
    enterprise_id   TEXT NOT NULL REFERENCES enterprise(enterprise_id),
    first_name      TEXT,
    last_name       TEXT,
    email           TEXT UNIQUE,
    password_hash   TEXT NOT NULL,
    role            TEXT NOT NULL CHECK (role IN (
                        'networkAdmin', 'systemAdmin',
                        'enterpriseAdmin', 'groupCeo', 'groupCfo',
                        'enterprisePresident', 'enterpriseCoo',
                        'orgDirector', 'creativeLead', 'technologyLead',
                        'marketingLead', 'complianceOfficer', 'dataAnalyst'
                    )),
    jwt_token       TEXT,
    is_active       INTEGER NOT NULL DEFAULT 1,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 5. Guest ──────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS guest (
    guest_id            TEXT PRIMARY KEY,
    first_name          TEXT,
    last_name           TEXT,
    email               TEXT UNIQUE NOT NULL,
    password_hash       TEXT NOT NULL,
    phone               TEXT,
    loyalty_points      INTEGER NOT NULL DEFAULT 0,
    is_verified         INTEGER NOT NULL DEFAULT 0,
    verification_token  TEXT,
    jwt_token           TEXT,
    created_at          TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 6. Work Request ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS work_request (
    request_id      TEXT PRIMARY KEY,
    title           TEXT NOT NULL,
    description     TEXT,
    request_type    TEXT NOT NULL CHECK (request_type IN (
                        'ContentRequest', 'LicensingRequest',
                        'BroadcastRequest', 'BookingRequest',
                        'ComplianceRequest'
                    )),
    status          TEXT NOT NULL DEFAULT 'draft' CHECK (status IN (
                        'draft', 'submitted',
                        'outboundApproved', 'inboundApproved',
                        'feasibilityReview', 'feasibilityComplete',
                        'approved', 'rejected', 'closed'
                    )),
    origin_org_id   TEXT NOT NULL REFERENCES organization(org_id),
    target_org_id   TEXT NOT NULL REFERENCES organization(org_id),
    submitted_by    TEXT NOT NULL REFERENCES user(user_id),
    assigned_to     TEXT REFERENCES user(user_id),
    created_at      TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 7. Status Change ─────────────────────────────────────────────────────────
-- Audit trail for all work request status transitions.
-- Rollback records are appended with is_rollback = 1 — prior records are
-- never deleted, preserving the full history.
CREATE TABLE IF NOT EXISTS status_change (
    status_change_id    TEXT PRIMARY KEY,
    request_id          TEXT NOT NULL REFERENCES work_request(request_id),
    previous_status     TEXT NOT NULL,
    new_status          TEXT NOT NULL,
    changed_by_id       TEXT NOT NULL REFERENCES user(user_id),
    changed_at          TEXT NOT NULL DEFAULT (datetime('now')),
    reason              TEXT,
    is_rollback         INTEGER NOT NULL DEFAULT 0
);

-- ── 8. Work Request Comment ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS work_request_comment (
    comment_id      TEXT PRIMARY KEY,
    request_id      TEXT NOT NULL REFERENCES work_request(request_id),
    author_id       TEXT NOT NULL REFERENCES user(user_id),
    body            TEXT NOT NULL,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 9. Booking ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS booking (
    booking_id      TEXT PRIMARY KEY,
    guest_id        TEXT NOT NULL REFERENCES guest(guest_id),
    org_id          TEXT NOT NULL REFERENCES organization(org_id),
    attraction      TEXT,
    booking_date    TEXT,
    party_size      INTEGER DEFAULT 1,
    status          TEXT NOT NULL DEFAULT 'confirmed' CHECK (status IN (
                        'confirmed', 'cancelled', 'completed'
                    )),
    total_cost      REAL DEFAULT 0.0,
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 10. Complaint ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS complaint (
    complaint_id    TEXT PRIMARY KEY,
    guest_id        TEXT NOT NULL REFERENCES guest(guest_id),
    org_id          TEXT NOT NULL REFERENCES organization(org_id),
    subject         TEXT,
    body            TEXT,
    status          TEXT NOT NULL DEFAULT 'open' CHECK (status IN (
                        'open', 'inProgress', 'resolved', 'closed'
                    )),
    created_at      TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 11. Casino Session ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS casino_session (
    session_id      TEXT PRIMARY KEY,
    guest_id        TEXT NOT NULL REFERENCES guest(guest_id),
    started_at      TEXT NOT NULL DEFAULT (datetime('now')),
    ended_at        TEXT,
    credits_start   INTEGER NOT NULL DEFAULT 0,
    credits_end     INTEGER
);

-- ── 12. Game Round ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS game_round (
    round_id        TEXT PRIMARY KEY,
    session_id      TEXT NOT NULL REFERENCES casino_session(session_id),
    game_type       TEXT NOT NULL,
    wager           INTEGER NOT NULL DEFAULT 0,
    outcome         TEXT NOT NULL CHECK (outcome IN ('win', 'loss', 'push')),
    payout          INTEGER NOT NULL DEFAULT 0,
    played_at       TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ── 13. Report ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS report (
    report_id       TEXT PRIMARY KEY,
    title           TEXT NOT NULL,
    scope           TEXT NOT NULL CHECK (scope IN (
                        'network', 'enterprise', 'organization'
                    )),
    scope_id        TEXT NOT NULL,
    generated_by    TEXT NOT NULL REFERENCES user(user_id),
    generated_at    TEXT NOT NULL DEFAULT (datetime('now')),
    format          TEXT NOT NULL DEFAULT 'json',
    data_json       TEXT
);

-- ── 14. Notification Log ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notification_log (
    log_id          TEXT PRIMARY KEY,
    recipient       TEXT NOT NULL,
    channel         TEXT NOT NULL CHECK (channel IN ('email')),
    subject         TEXT,
    body            TEXT,
    status          TEXT NOT NULL DEFAULT 'sent' CHECK (status IN (
                        'sent', 'failed', 'pending'
                    )),
    sent_at         TEXT NOT NULL DEFAULT (datetime('now')),
    related_id      TEXT
);

-- ── Indexes ───────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_enterprise_network   ON enterprise(network_id);
CREATE INDEX IF NOT EXISTS idx_org_enterprise        ON organization(enterprise_id);
CREATE INDEX IF NOT EXISTS idx_user_org              ON user(org_id);
CREATE INDEX IF NOT EXISTS idx_user_enterprise       ON user(enterprise_id);
CREATE INDEX IF NOT EXISTS idx_wr_origin             ON work_request(origin_org_id);
CREATE INDEX IF NOT EXISTS idx_wr_target             ON work_request(target_org_id);
CREATE INDEX IF NOT EXISTS idx_wr_submitted_by       ON work_request(submitted_by);
CREATE INDEX IF NOT EXISTS idx_sc_request            ON status_change(request_id);
CREATE INDEX IF NOT EXISTS idx_sc_changed_by         ON status_change(changed_by_id);
CREATE INDEX IF NOT EXISTS idx_wrc_request           ON work_request_comment(request_id);
CREATE INDEX IF NOT EXISTS idx_booking_guest         ON booking(guest_id);
CREATE INDEX IF NOT EXISTS idx_complaint_guest       ON complaint(guest_id);
CREATE INDEX IF NOT EXISTS idx_casino_guest          ON casino_session(guest_id);
CREATE INDEX IF NOT EXISTS idx_game_session          ON game_round(session_id);
CREATE INDEX IF NOT EXISTS idx_report_scope          ON report(scope_id);
CREATE INDEX IF NOT EXISTS idx_notif_related         ON notification_log(related_id);