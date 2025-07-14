-- V3__insert_sample_users.sql

INSERT INTO users (id, name, email, password)
VALUES
    ('6a8b9c1d-1234-4e5f-a6b7-c8d9e0f1a2b3', 'Julia Sanderson', 'julia.sanderson@example.com', '$2a$10$K8q7gvS2K28uhZPSGCIQSetfbw2m5bovBgFGDj6L8qxwZf0yDO4cu'), -- Full name, no password
    ('9d3e2a5b-48b9-4e65-9c2d-7a1b8c7f6d2e', 'Liam Morris', 'liam.morris@example.com', '$2a$10$K8q7gvS2K28uhZPSGCIQSetfbw2m5bovBgFGDj6L8qxwZf0yDO4cu'), -- password123
    ('b1c2d3e4-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 'Zoe Hanley', 'zoe.hanley@example.com', '$2a$10$K8q7gvS2K28uhZPSGCIQSetfbw2m5bovBgFGDj6L8qxwZf0yDO4cu'), -- secure456
    ('fa3d2c1b-4e6f-7a8b-9c0d-1e2f3a4b5c6d', 'Marco Viteri','marco.viteri@example.com', '$2a$10$K8q7gvS2K28uhZPSGCIQSetfbw2m5bovBgFGDj6L8qxwZf0yDO4cu'); -- qwerty789
