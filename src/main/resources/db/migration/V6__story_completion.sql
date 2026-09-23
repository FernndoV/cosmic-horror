ALTER TABLE adventure_session ADD COLUMN story_status VARCHAR(20) NOT NULL DEFAULT 'ONGOING';
ALTER TABLE adventure_session ADD COLUMN ending_type VARCHAR(20);