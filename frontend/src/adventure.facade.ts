import { Injectable, inject, signal } from "@angular/core";
import { HttpErrorResponse } from "@angular/common/http";
import { ApiService, Campaign, Character, Session, SkillCheckResult, Step } from "./api.service";

export type CampaignForm = { name: string; setting: string; era: string; location: string; tone: string };
export type CharacterForm = { name: string; concept: string };

@Injectable({ providedIn: "root" })
export class AdventureFacade {
  private readonly api = inject(ApiService);

  readonly step = signal<Step>("campaign");
  readonly campaign = signal<Campaign | null>(null);
  readonly character = signal<Character | null>(null);
  readonly session = signal<Session | null>(null);
  readonly busy = signal(false);
  readonly error = signal("");
  readonly portraitPreview = signal<string | null>(null);
  readonly portraitBusy = signal(false);
  readonly portraitError = signal("");
  readonly portraitDirty = signal(false);
  readonly skillCheck = signal<SkillCheckResult | null>(null);
  readonly skillCheckBusy = signal(false);
  private readonly portraitCache = new Map<string, string>();

  createCampaign(form: CampaignForm, defaultCharacter?: CharacterForm): void {
    this.busy.set(true);
    this.error.set("");
    this.api.createCampaign(form).subscribe({
      next: campaign => {
        this.campaign.set(campaign);
        this.step.set("character");
        this.busy.set(false);
        if (defaultCharacter) this.refreshPortrait(defaultCharacter);
      },
      error: error => this.fail(error, "No se pudo crear la campaña.")
    });
  }

  createCharacter(form: CharacterForm): void {
    const campaign = this.campaign();
    if (!campaign) return;
    this.busy.set(true);
    this.error.set("");
    this.api.createCharacter(campaign.id, { ...form, portraitUrl: this.portraitPreview() }).subscribe({
      next: character => {
        this.character.set(character);
        this.portraitDirty.set(false);
        this.api.createSession(campaign.id, character.id).subscribe({
          next: session => {
            this.session.set(session);
            this.step.set("session");
            this.busy.set(false);
          },
          error: error => this.fail(error, "El personaje se creó, pero no se pudo iniciar la sesión.")
        });
      },
      error: error => this.fail(error, "No se pudo crear el personaje.")
    });
  }

  markPortraitDirty(): void {
    this.portraitDirty.set(true);
    this.portraitError.set("");
  }

  refreshPortrait(form: CharacterForm): void {
    const campaign = this.campaign();
    if (!campaign || form.name.trim().length < 2 || this.portraitBusy()) return;
    this.portraitError.set("");
    const key = [campaign.id, form.name.trim(), form.concept.trim()].join("|");
    const cached = this.portraitCache.get(key);
    if (cached) {
      this.portraitPreview.set(cached);
      this.portraitDirty.set(false);
      return;
    }

    this.portraitBusy.set(true);
    this.api.previewCharacterPortrait(campaign.id, { name: form.name.trim(), concept: form.concept.trim() }).subscribe({
      next: result => {
        if (result.portraitUrl) {
          this.portraitCache.set(key, result.portraitUrl);
          this.portraitPreview.set(result.portraitUrl);
        }
        this.portraitDirty.set(false);
        this.portraitBusy.set(false);
      },
      error: () => {
        this.portraitBusy.set(false);
        this.portraitError.set("No se pudo actualizar el retrato; puedes continuar sin él.");
      }
    });
  }
  rollSkill(skill: string): void {
    const session = this.session();
    if (!session || this.skillCheckBusy()) return;
    this.skillCheckBusy.set(true);
    this.api.rollSkillCheck(session.sessionId, skill).subscribe({
      next: result => { this.skillCheck.set(result); this.skillCheckBusy.set(false); },
      error: () => { this.skillCheckBusy.set(false); this.error.set("No se pudo realizar la prueba."); }
    });
  }

  skillEntries(): Array<{ key: string; value: number }> {
    return Object.entries(this.character()?.skills ?? {}).map(([key, value]) => ({ key, value }));
  }

  skillLabel(skill: string): string {
    const labels: Record<string, string> = { investigacion: "Investigación", percepcion: "Percepción", voluntad: "Voluntad", fuerza: "Fuerza", destreza: "Destreza", persuasion: "Persuasión" };
    return labels[skill] ?? skill.replaceAll("_", " ");
  }

  submitTurn(action: string): void {
    const session = this.session();
    if (!session || !action.trim()) return;
    this.busy.set(true);
    this.error.set("");
    this.api.submitTurn(session.sessionId, crypto.randomUUID(), session.version, action).subscribe({
      next: result => {
        this.session.set(result.state);
        this.busy.set(false);
      },
      error: error => this.fail(error, "La acción no pudo resolverse.")
    });
  }

  storyPhase(version: number): string {
    return version <= 2 ? "Inicio" : version <= 5 ? "Desarrollo" : "Desenlace";
  }

  objectiveStatusLabel(status: string): string { const labels: Record<string, string> = { ACTIVE: "Activo", RESOLVED: "Resuelto", ABANDONED: "Abandonado", TRANSFORMED: "Transformado" }; return labels[status] ?? status; }

  endingLabel(type: string | null | undefined): string { const labels: Record<string, string> = { VICTORY: "Victoria", DEFEAT: "Derrota", SACRIFICE: "Sacrificio", ESCAPE: "Huida", AMBIGUOUS: "Final inquietante" }; return labels[type ?? ""] ?? "Desenlace"; }

  busyMessage(): string {
    if (this.step() === "campaign") return "El director está preparando la campaña…";
    if (this.step() === "character") return "El director está dando forma a tu personaje…";
    return "El director está interpretando tu acción…";
  }
  goTo(step: Step): void {
    if (step === "campaign" || (step === "character" && this.campaign()) || (step === "session" && this.session())) {
      this.step.set(step);
    }
  }

  clueLabel(clue: string): string {
    const labels: Record<string, string> = {
      impossible_map: "Mapa imposible",
      impossible_staircase: "Escalera imposible",
      three_knocks: "Tres golpes en la pared",
      salt_key: "Llave cubierta de sal negra"
    };
    return labels[clue] ?? clue.replaceAll("_", " ").replace(/^./, letter => letter.toUpperCase());
  }

  clueDescription(clue: string): string {
    const descriptions: Record<string, string> = {
      impossible_map: "Un trazado que cambia cuando intentas copiarlo.",
      impossible_staircase: "Una escalera cuya profundidad contradice el edificio.",
      three_knocks: "Tres golpes que imitan el ritmo del personaje.",
      salt_key: "Una llave fría cubierta de sal negra, de procedencia desconocida."
    };
    return descriptions[clue] ?? "Hecho confirmado durante la investigación.";
  }

  private fail(error: unknown, fallback: string): void {
    const status = error instanceof HttpErrorResponse ? error.status : 0;
    const code = error instanceof HttpErrorResponse && typeof error.error?.code === "string" ? error.error.code : "";
    const message = code === "NARRATIVE_PROVIDER_UNAVAILABLE" || status === 503
      ? "El proveedor narrativo no está disponible."
      : code === "SESSION_VERSION_CONFLICT" || status === 409
        ? "La partida cambió en otra ventana. Recarga el estado antes de continuar."
        : fallback;
    this.error.set(message);
    this.busy.set(false);
  }
}
