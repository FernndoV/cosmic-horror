CREATE TABLE campaign (
  id UUID PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  setting TEXT,
  era VARCHAR(120),
  location VARCHAR(200),
  tone VARCHAR(120),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE player_character (
  id UUID PRIMARY KEY,
  campaign_id UUID NOT NULL REFERENCES campaign(id),
  name VARCHAR(120) NOT NULL,
  concept VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_player_character_campaign ON player_character(campaign_id);
