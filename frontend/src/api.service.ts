import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

export type Campaign = { id: string; name: string; setting?: string; era?: string; location?: string; tone?: string };
export type Character = { id: string; campaignId: string; name: string; concept?: string; portraitUrl?: string | null; skills: Record<string, number> };
export type NarrativeObjective = { id: string; text: string; status: "ACTIVE" | "RESOLVED" | "ABANDONED" | "TRANSFORMED" };
export type Session = { sessionId: string; campaignId?: string; characterId?: string; openingNarrative: string; version: number; sanity: number; storyStatus: "ONGOING" | "CONCLUDED"; endingType?: string | null; objective: string; objectiveStatus: "ACTIVE" | "RESOLVED" | "ABANDONED" | "TRANSFORMED"; objectives: NarrativeObjective[]; clues: string[]; turns: Turn[] };
export type SkillCheckResult = { skill: string; modifier: number; dieOne: number; dieTwo: number; total: number; threshold: number; success: boolean };
export type Step = "campaign" | "character" | "session";
export type Turn = { id: string; requestId: string; action: string; narrative: string; version: number };

@Injectable({ providedIn: "root" })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  createCampaign(form: { name: string; setting: string; era: string; location: string; tone: string }): Observable<Campaign> {
    return this.http.post<Campaign>("/api/campaigns", form);
  }

  createCharacter(campaignId: string, form: { name: string; concept: string; portraitUrl?: string | null }): Observable<Character> {
    return this.http.post<Character>("/api/campaigns/" + campaignId + "/characters", form);
  }

  previewCharacterPortrait(campaignId: string, form: { name: string; concept: string }): Observable<{ portraitUrl: string | null }> {
    return this.http.post<{ portraitUrl: string | null }>("/api/campaigns/" + campaignId + "/characters/portrait-preview", form);
  }

  createSession(campaignId: string, characterId: string): Observable<Session> {
    return this.http.post<Session>("/api/sessions", { campaignId, characterId });
  }

  rollSkillCheck(sessionId: string, skill: string): Observable<SkillCheckResult> {
    return this.http.post<SkillCheckResult>("/api/sessions/" + sessionId + "/checks", { skill });
  }

  submitTurn(sessionId: string, requestId: string, expectedVersion: number, action: string): Observable<{ state: Session }> {
    return this.http.post<{ state: Session }>("/api/sessions/" + sessionId + "/turns", {
      requestId, expectedVersion, action
    });
  }
}
