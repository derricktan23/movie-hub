-- ============================================================================
-- 03: Seed Data for Local Development & Testing
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Seed People (ppl_db)
-- ----------------------------------------------------------------------------
\c ppl_db;

INSERT INTO people (id, name, role) VALUES
    ('e1a1c902-6e27-4c7b-b8df-9111c1000001', 'Leonardo DiCaprio', 'ACTOR'),
    ('e2b2d013-7f38-4d8c-89ef-9222d2000002', 'Joseph Gordon-Levitt', 'ACTOR'),
    ('e3c3e124-8a49-4e9d-90fa-9333e3000003', 'Christian Bale', 'ACTOR'),
    ('e4d4f235-9b5a-4f0e-a1ab-9444f4000004', 'Heath Ledger', 'ACTOR'),
    ('e5e5a346-0c6b-4a1f-b2bc-9555a5000005', 'Cillian Murphy', 'ACTOR'),
    ('e6f6b457-1d7c-4b20-83cd-9666b6000006', 'Matthew McConaughey', 'ACTOR'),
    ('f7a7c568-2e8d-4c31-94de-9777c7000007', 'Christopher Nolan', 'DIRECTOR'),
    ('f8b8d679-3f9e-4d42-a5ef-9888d8000008', 'Emma Thomas', 'PRODUCER'),
    ('f9c9e780-4a0f-4e53-b6fa-9999e9000009', 'Hans Zimmer', 'COMPOSER')
ON CONFLICT (id) DO NOTHING;


-- ----------------------------------------------------------------------------
-- Seed Movies & Associations (movie_db)
-- ----------------------------------------------------------------------------
\c movie_db;

INSERT INTO movies (id, title, release_year, artwork_url) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Inception', 2010, 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800'),
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Interstellar', 2014, 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=800'),
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'The Dark Knight', 2008, 'https://images.unsplash.com/photo-1509281373149-e957c6296406?w=800'),
    ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'Oppenheimer', 2023, 'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=800')
ON CONFLICT (id) DO NOTHING;

INSERT INTO movie_cast (movie_id, cast_id) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e1a1c902-6e27-4c7b-b8df-9111c1000001'), -- Inception: Leonardo DiCaprio
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'e2b2d013-7f38-4d8c-89ef-9222d2000002'), -- Inception: Joseph Gordon-Levitt
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'f7a7c568-2e8d-4c31-94de-9777c7000007'), -- Inception: Christopher Nolan
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'e6f6b457-1d7c-4b20-83cd-9666b6000006'), -- Interstellar: Matthew McConaughey
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'f7a7c568-2e8d-4c31-94de-9777c7000007'), -- Interstellar: Christopher Nolan
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'e3c3e124-8a49-4e9d-90fa-9333e3000003'), -- The Dark Knight: Christian Bale
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'e4d4f235-9b5a-4f0e-a1ab-9444f4000004'), -- The Dark Knight: Heath Ledger
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'f7a7c568-2e8d-4c31-94de-9777c7000007'), -- The Dark Knight: Christopher Nolan
    ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'e5e5a346-0c6b-4a1f-b2bc-9555a5000005'), -- Oppenheimer: Cillian Murphy
    ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'f7a7c568-2e8d-4c31-94de-9777c7000007')  -- Oppenheimer: Christopher Nolan
ON CONFLICT (movie_id, cast_id) DO NOTHING;
