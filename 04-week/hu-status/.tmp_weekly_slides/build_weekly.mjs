import fs from "node:fs/promises";
import { Presentation, PresentationFile } from "@oai/artifact-tool";

const OUT = "C:/Users/hp/Documents/sistemas-distribuidos-2026-b-g1/04-week/hu-status/WEEKLY_DI_LUCCA_2026-09-01_MEJORADA.pptx";
const PREVIEW = "C:/Users/hp/Documents/sistemas-distribuidos-2026-b-g1/04-week/hu-status/.tmp_weekly_slides/rendered_mejorada";
const W = 1280, H = 720;
const C = { ink: "#111111", muted: "#5F6670", panel: "#EDEDED", rule: "#B8BCC4", blue: "#3D8DFF", pale: "#D0EDFA", white: "#FFFFFF" };

function box(slide, name, left, top, width, height, fill = "none", line = "none") {
  return slide.shapes.add({ geometry: "rect", name, position: { left, top, width, height }, fill, line: { style: "solid", fill: line, width: line === "none" ? 0 : 1 } });
}

function textBox(slide, name, text, left, top, width, height, fontSize, opts = {}) {
  const s = slide.shapes.add({ geometry: "textbox", name, position: { left, top, width, height }, fill: "none", line: { style: "solid", fill: "none", width: 0 } });
  s.text = text;
  s.text.style = { fontSize, typeface: "Arial", color: opts.color || C.ink, bold: !!opts.bold, alignment: opts.alignment || "left", verticalAlignment: opts.verticalAlignment || "top" };
  return s;
}

function header(slide, title, kicker, num) {
  textBox(slide, `kicker-${num}`, kicker.toUpperCase(), 48, 32, 350, 24, 15, { bold: true, color: C.blue });
  textBox(slide, `title-${num}`, title, 48, 66, 1184, 92, 38, { bold: true });
  box(slide, `rule-${num}`, 48, 162, 1184, 2, C.ink);
  textBox(slide, `page-${num}`, String(num).padStart(2, "0"), 1168, 672, 64, 20, 14, { color: C.muted, alignment: "right" });
}

function addBullet(slide, text, x, y, w, size = 20, color = C.ink) {
  box(slide, `dot-${x}-${y}`, x, y + 8, 8, 8, C.blue);
  textBox(slide, `bullet-${x}-${y}`, text, x + 20, y, w - 20, 58, size, { color });
}

const deck = Presentation.create({ slideSize: { width: W, height: H } });

// 1 — Three-column progress composition inspired by Codex Grid slide 06.
{
  const s = deck.slides.add(); s.background.fill = C.white;
  header(s, "Alineamos el producto y construimos una versión demostrable", "Qué hicimos · 24–30 de agosto", 1);
  textBox(s, "intro-1", "Revisamos las inconsistencias del proyecto para que las reglas del negocio, los requisitos y la aplicación expresaran una misma solución odontológica.", 48, 182, 1120, 58, 22, { color: C.muted });
  const cols = [48, 450, 852];
  const labels = ["COHERENCIA DEL PRODUCTO", "MVP MONOLÍTICO", "DISEÑO Y LÍNEA BASE"];
  const titles = ["Reglas y trazabilidad alineadas", "Una solución para presentar", "Figma y alcance del MVP"];
  const bodies = [
    "Se corrigieron inconformidades entre el alcance, las reglas del negocio y la trazabilidad. También se ajustaron requisitos, historias de usuario y decisiones arquitectónicas para mantener una sola visión del sistema.",
    "Se desarrolló el monolito con Angular, Spring Boot y PostgreSQL. Integra autenticación, pacientes, citas, historias clínicas, facturación, pagos, usuarios y analítica para demostrar los flujos principales.",
    "Se terminó el prototipo en Figma y se preparó la documentación del MVP en un repositorio externo. Esto dejó una referencia común para la interfaz, el alcance aprobado y la implementación."
  ];
  for (let i = 0; i < 3; i++) {
    box(s, `accent-${i}`, cols[i], 274, 352, 8, i === 1 ? C.blue : C.pale);
    textBox(s, `label-${i}`, labels[i], cols[i], 306, 352, 24, 16, { bold: true, color: C.blue });
    textBox(s, `col-title-${i}`, titles[i], cols[i], 350, 352, 66, 25, { bold: true });
    textBox(s, `col-body-${i}`, bodies[i], cols[i], 432, 352, 174, 18, { color: C.muted });
  }
  textBox(s, "evidence-1", "Resultado: reglas, trazabilidad, Figma y monolito quedaron orientados al mismo MVP.", 48, 628, 1050, 28, 18, { bold: true });
}

// 2 — Evidence + checklist composition inspired by Codex Grid slide 10.
{
  const s = deck.slides.add(); s.background.fill = C.white;
  header(s, "El mayor obstáculo fue mantener la congruencia del proyecto", "Obstáculos encontrados", 2);
  textBox(s, "lead-2", "Había diferencias entre lo escrito, lo diseñado y lo implementado. La comunicación del equipo y la interpretación de algunos términos —incluida la brecha de contenido en inglés— dificultaron integrar una visión común.", 48, 178, 650, 108, 21, { color: C.muted });
  box(s, "panel-2", 48, 322, 642, 286, C.panel);
  textBox(s, "panel-title-2", "Integrar significaba volver a revisar", 76, 350, 580, 38, 25, { bold: true });
  textBox(s, "panel-body-2", "Cada cambio en una regla del negocio afectaba requisitos, trazabilidad, pantallas y comportamiento del monolito. El reto fue comunicar esos cambios y comprobar que todos los entregables siguieran siendo congruentes entre sí.", 76, 410, 566, 150, 20, { color: C.muted });
  textBox(s, "side-title-2", "Brechas que enfrentamos", 770, 204, 410, 36, 25, { bold: true });
  addBullet(s, "Interpretaciones distintas de las reglas", 770, 272, 420, 20);
  addBullet(s, "Contenido y términos sin unificar", 770, 346, 420, 20);
  addBullet(s, "Dudas sobre la integración entre módulos", 770, 420, 420, 20);
  addBullet(s, "Trazabilidad que debía actualizarse", 770, 494, 420, 20);
  textBox(s, "note-2", "Aprendizaje: comunicar cada cambio y validar su impacto en todo el producto.", 770, 580, 420, 58, 18, { bold: true, color: C.blue });
}

// 3 — Three-step timeline inspired by Codex Grid slide 17.
{
  const s = deck.slides.add(); s.background.fill = C.white;
  header(s, "Cerraremos el corte 1 y prepararemos la evolución", "Qué haremos esta semana", 3);
  textBox(s, "lead-3", "La prioridad es entregar el monolito como cierre del primer corte, corregir las brechas restantes y usar el MVP como base para la evaluación arquitectónica.", 48, 176, 1080, 52, 21, { color: C.muted });
  box(s, "timeline", 72, 332, 1110, 2, C.ink);
  const xs = [72, 482, 892];
  const phase = ["01 · ENTREGAR", "02 · INTEGRAR", "03 · EVOLUCIONAR"];
  const title = ["Cerrar el monolito", "Asegurar los flujos", "Preparar microservicios"];
  const body = [
    "Presentar y entregar el MVP monolítico para cerrar el corte 1 con una versión funcional y demostrable.",
    "Corregir brechas, revisar la comunicación entre módulos y comprobar los recorridos completos del sistema.",
    "Evaluar cómo separar capacidades del MVP e iniciar una transición gradual hacia una arquitectura de microservicios."
  ];
  for (let i = 0; i < 3; i++) {
    box(s, `node-${i}`, xs[i], 324, 18, 18, i === 0 ? C.blue : C.ink);
    textBox(s, `phase-${i}`, phase[i], xs[i], 270, 330, 24, 16, { bold: true, color: C.blue });
    textBox(s, `step-title-${i}`, title[i], xs[i], 378, 330, 58, 26, { bold: true });
    textBox(s, `step-body-${i}`, body[i], xs[i], 456, 330, 130, 20, { color: C.muted });
  }
  box(s, "closing-band", 48, 620, 1134, 40, C.pale);
  textBox(s, "closing-3", "Resultado esperado: corte 1 cerrado y una ruta clara para evolucionar el MVP sin perder coherencia.", 68, 628, 1094, 28, 18, { bold: true });
}

await fs.mkdir(PREVIEW, { recursive: true });
for (const [i, slide] of deck.slides.items.entries()) {
  const png = await deck.export({ slide, format: "png", scale: 1 });
  await fs.writeFile(`${PREVIEW}/slide-${i + 1}.png`, new Uint8Array(await png.arrayBuffer()));
  const layout = await slide.export({ format: "layout" });
  await fs.writeFile(`${PREVIEW}/slide-${i + 1}.layout.json`, await layout.text());
}
const montage = await deck.export({ format: "webp", montage: true, scale: 1 });
await fs.writeFile(`${PREVIEW}/montage.webp`, new Uint8Array(await montage.arrayBuffer()));
const pptx = await PresentationFile.exportPptx(deck);
await pptx.save(OUT);
console.log(OUT);
