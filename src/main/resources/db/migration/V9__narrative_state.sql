ALTER TABLE campaign ADD COLUMN central_threat VARCHAR(500) NOT NULL DEFAULT '';
ALTER TABLE campaign ADD COLUMN player_objective VARCHAR(500) NOT NULL DEFAULT '';
ALTER TABLE adventure_session ADD COLUMN narrative_state JSONB;
