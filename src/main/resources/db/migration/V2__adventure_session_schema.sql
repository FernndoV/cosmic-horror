CREATE TABLE adventure_session (
  id UUID PRIMARY KEY,
  campaign_id UUID REFERENCES campaign(id),
  character_id UUID REFERENCES player_character(id),
  version INTEGER NOT NULL,
  sanity INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE adventure_turn (
  id UUID PRIMARY KEY,
  session_id UUID NOT NULL REFERENCES adventure_session(id) ON DELETE CASCADE,
  request_id UUID NOT NULL UNIQUE,
  action TEXT NOT NULL,
  narrative TEXT NOT NULL,
  version INTEGER NOT NULL
);
CREATE TABLE session_clue (
  session_id UUID NOT NULL REFERENCES adventure_session(id) ON DELETE CASCADE,
  clue VARCHAR(200) NOT NULL,
  PRIMARY KEY(session_id, clue)
);
