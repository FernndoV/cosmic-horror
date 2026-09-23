import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { AdventureFacade, CampaignForm, CharacterForm } from "./adventure.facade";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [FormsModule],
  templateUrl: "./app.component.html"
})
export class AppComponent {
  constructor(readonly adventure: AdventureFacade) {}
  action = "";
  portraitExpanded = false;
  campaignForm: CampaignForm = this.generateCampaignForm();
  characterForm: CharacterForm = this.generateCharacterForm();

  private generateCampaignForm(): CampaignForm {
    const settings = ["Una ciudad portuaria donde los sueños llegan con la niebla.", "Un observatorio abandonado recibe señales desde una estrella imposible.", "En un pueblo minero, los muertos han comenzado a llamar por teléfono.", "Una expedición polar encuentra una arquitectura anterior a la humanidad.", "Los archivos de una universidad contienen mapas que cambian cada noche."];
    const eras = ["Años 20", "Finales del siglo XIX", "Años 30", "Futuro cercano", "Época victoriana"];
    const locations = ["Innsmouth", "Arkham", "Kingsport", "Dunwich", "La costa de Nueva Inglaterra", "Una estación polar"];
    const tones = ["Opresivo y lento", "Investigación paranoica", "Melancólico y onírico", "Supervivencia desesperada", "Misterioso y decadente"];
    const names = ["La señal bajo la marea", "El atlas de los sueños", "Las voces del abismo", "El archivo imposible", "La geometría de la noche"];
    const pick = <T>(values: T[]): T => values[Math.floor(Math.random() * values.length)];
    return { name: pick(names), setting: pick(settings), era: pick(eras), location: pick(locations), tone: pick(tones) };
  }

  private generateCharacterForm(): CharacterForm {
    const profiles: CharacterForm[] = [
      { name: "Evelyn Marsh", concept: "Investigadora de archivos, escéptica hasta anoche." },
      { name: "Mara Calder", concept: "Médica rural perseguida por sueños que no le pertenecen." },
      { name: "Silas Ward", concept: "Cartógrafo de costas que desaparecen con la marea." },
      { name: "Agnes Bell", concept: "Periodista que encontró su propio obituario en un archivo antiguo." },
      { name: "Thomas Vale", concept: "Anticuario heredero de una llave que nadie recuerda haber forjado." },
      { name: "Nora Whitlock", concept: "Astrónoma aficionada que reconoce una constelación imposible." }
    ];
    return profiles[Math.floor(Math.random() * profiles.length)];
  }

  openingParagraphs(narrative: string): string[] {
    return narrative.split(/\r?\n\s*\r?\n/).map(paragraph => paragraph.trim()).filter(Boolean);
  }

  submitFromKeyboard(event: Event): void {
    const keyboardEvent = event as KeyboardEvent;
    if (!keyboardEvent.shiftKey) {
      event.preventDefault();
      this.submitAction();
    }
  }

  submitAction(): void {
    const value = this.action.trim();
    if (!value || this.adventure.busy()) return;
    this.action = "";
    this.adventure.submitTurn(value);
  }
}
