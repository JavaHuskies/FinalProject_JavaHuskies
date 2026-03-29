-- ─────────────────────────────────────────────────────────────────────────────
-- Deep Thought Entertainment Group — seed.sql
-- INFO 5100 Final Project — Java Huskies
--
-- Static seed data: network, enterprises, organizations.
-- User rows are inserted by SeedService.java (password hashes computed at
-- runtime via AuthService.hashPassword() — do not hardcode hashes here).
-- Safe to re-run: all inserts use INSERT OR IGNORE.
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Network ───────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO network (network_id, name, headquarters, founded) VALUES
    ('deepThoughtEntertainmentGroup',
     'Deep Thought Entertainment Group',
     'Magrathea', '1979');

-- ── Enterprises ───────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO enterprise (enterprise_id, network_id, name, type) VALUES
    ('magratheaStudios',       'deepThoughtEntertainmentGroup', 'Magrathea Studios',        'Production'),
    ('starshipTitanicLeisure', 'deepThoughtEntertainmentGroup', 'Starship Titanic Leisure',  'Hospitality'),
    ('galacticBroadcasting',   'deepThoughtEntertainmentGroup', 'Galactic Broadcasting',     'Media'),
    ('siriusCybernetics',      'deepThoughtEntertainmentGroup', 'Sirius Cybernetics',        'Technology');

-- ── Organizations ─────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO organization (org_id, enterprise_id, name, type) VALUES
    -- Magrathea Studios
    ('slartibartfastPictures',         'magratheaStudios',       'Slartibartfast Pictures',          'Film'),
    ('bistromathAnimation',            'magratheaStudios',       'Bistromath Animation',             'Animation'),
    -- Starship Titanic Leisure
    ('magratheaThemeWorlds',           'starshipTitanicLeisure', 'Magrathea Theme Worlds',           'Theme Park'),
    ('milliwaysEntertainment',         'starshipTitanicLeisure', 'Milliways Entertainment',          'Events'),
    -- Galactic Broadcasting
    ('infiniteImprobabilityStreaming', 'galacticBroadcasting',   'Infinite Improbability Streaming', 'Streaming'),
    ('panGalacticBroadcast',          'galacticBroadcasting',   'Pan-Galactic Broadcast',           'Broadcast'),
    -- Sirius Cybernetics
    ('megadodoLicensing',             'siriusCybernetics',      'Megadodo Licensing',               'Licensing'),
    ('hooloovooRetail',               'siriusCybernetics',      'Hooloovoo Retail',                 'Retail');
