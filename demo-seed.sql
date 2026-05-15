-- ============================================================
-- CarpeDiem — Demo Seed (Change 7)
-- Run ONCE after schema.sql.
-- Creates demo squad_group + 5 users + 5 tasks.
-- Does NOT insert daily_logs — DemoResetService does that at runtime.
-- ============================================================

USE carpediem;

-- Group
INSERT INTO squad_groups (name, invite_code)
VALUES ('The Grind Squad', 'DEMO00')
ON DUPLICATE KEY UPDATE name = VALUES(name);

SET @gid = (SELECT id FROM squad_groups WHERE invite_code = 'DEMO00' LIMIT 1);

-- Users (passwords are placeholders — demo login goes through /demo, no password check)
INSERT INTO users (name, email, password_hash, group_id) VALUES
  ('Kavya Nair',   'kavya@demo.carpediem', '$2a$10$dummyhashKavyaNair111111111111111111111111111u', @gid),
  ('Arjun Sharma', 'arjun@demo.carpediem', '$2a$10$dummyhashArjunSharma11111111111111111111111u', @gid),
  ('Priya Patel',  'priya@demo.carpediem', '$2a$10$dummyhashPriyaPatel11111111111111111111111u', @gid),
  ('Rohan Mehta',  'rohan@demo.carpediem', '$2a$10$dummyhashRohanMehta11111111111111111111111u', @gid),
  ('Demo',         'demo@carpediem.app',   '$2a$10$dummyhashDemoUser1111111111111111111111111u', @gid)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Tasks (group-scoped, shared across the squad)
INSERT INTO tasks (name, group_id, position) VALUES
  ('DSA',      @gid, 0),
  ('Gym',      @gid, 1),
  ('AI Study', @gid, 2),
  ('Reading',  @gid, 3),
  ('Coding',   @gid, 4);
