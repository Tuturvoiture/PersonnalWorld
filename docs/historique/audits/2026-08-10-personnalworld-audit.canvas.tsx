import {
  Callout,
  Card,
  CardBody,
  CardHeader,
  Divider,
  Grid,
  H1,
  H2,
  H3,
  Pill,
  Row,
  Stack,
  Stat,
  Table,
  Text,
  TodoListCard,
  UsageBar,
  useCanvasState,
  useHostTheme,
} from "cursor/canvas";

type Tab = "overview" | "reconcile" | "features" | "roadmap" | "issues";

export default function PersonnalWorldAudit() {
  const theme = useHostTheme();
  const [tab, setTab] = useCanvasState<Tab>("audit-tab-v2", "overview");

  return (
    <Stack gap={20} style={{ padding: 20, maxWidth: 1000 }}>
      <Stack gap={6}>
        <H1>PersonnalWorld — Audit consolidé</H1>
        <Text tone="secondary">
          Croisement : audit ancien dev (10 août 2026, à prendre avec des pincettes) · dépôt
          PersonnalWorld actuel · migration multi-version/multi-loader depuis.
        </Text>
        <Row gap={8} wrap>
          <Pill size="sm" active={tab === "overview"} onClick={() => setTab("overview")}>
            Vue d’ensemble
          </Pill>
          <Pill size="sm" active={tab === "reconcile"} onClick={() => setTab("reconcile")}>
            Ancien vs actuel
          </Pill>
          <Pill size="sm" active={tab === "features"} onClick={() => setTab("features")}>
            Mis en place
          </Pill>
          <Pill size="sm" active={tab === "roadmap"} onClick={() => setTab("roadmap")}>
            Plan d’avancement
          </Pill>
          <Pill size="sm" active={tab === "issues"} onClick={() => setTab("issues")}>
            Problèmes
          </Pill>
        </Row>
      </Stack>

      {tab === "overview" && <Overview />}
      {tab === "reconcile" && <Reconcile />}
      {tab === "features" && <Features />}
      {tab === "roadmap" && <Roadmap />}
      {tab === "issues" && <Issues accent={theme.accent.primary} />}
    </Stack>
  );
}

function Overview() {
  return (
    <Stack gap={16}>
      <Grid columns={4} gap={12}>
        <Stat value="1.2.2" label="Version mod (gradle)" />
        <Stat value="1.21.1" label="MC cible (Stonecutter)" />
        <Stat value="DimLib" label="Stack dimensions réelle" tone="warning" />
        <Stat value="Architectury" label="Migration en cours" tone="info" />
      </Grid>

      <Callout tone="info" title="Contexte API (clarifié)">
        LiveWorldAPI = ancien nom de DimensionArchitectAPI (même mod). DimLib était la
        dépendance initiale, mais peu maintenue à jour → idée de créer une API maison pour
        remplacer DimLib. Dans ce dépôt PersonnalWorld, le runtime pointe encore vers DimLib ;
        la bascule vers DimensionArchitectAPI reste le chantier cible.
      </Callout>

      <Callout tone="warning" title="Lire l’ancien audit avec des pincettes">
        L’audit mélange vision cible et état réel. Les % et cases « fait » ne remplacent pas le
        code. DimensionArchitectAPI n’est pas dans ce workspace ; PersonnalWorld dépend encore
        de DimLib en attendant la migration.
      </Callout>

      <Callout tone="info" title="Verdict consolidé">
        Prototype Fabric jouable (bâton, dimension perso, île NBT, retour position) via DimLib.
        Direction stratégique : migrer vers DimensionArchitectAPI (ex-LiveWorldAPI), puis
        fonctionnalités produit. Scaffold multi-version/multi-loader déjà amorcé — à synchroniser
        avec la maturité de l’API maison.
      </Callout>

      <Grid columns={2} gap={12}>
        <Card>
          <CardHeader>Deux couches (vision audit)</CardHeader>
          <CardBody>
            <Stack gap={6}>
              <Text weight="semibold">DimensionArchitectAPI (ex-LiveWorldAPI)</Text>
              <Text tone="secondary" size="small">
                API maison pour remplacer DimLib (peu maintenu) : create / delete / load /
                teleport / presets / lifecycle. Dépôt :
                Tuturvoiture/DimensionArchitectAPI — hors de ce workspace.
              </Text>
              <Divider />
              <Text weight="semibold">PersonnalWorld (ce repo)</Text>
              <Text tone="secondary" size="small">
                Métier joueur : item, monde perso, invitations, GUI. Encore branché sur DimLib
                dans fabric/ ; cible = consommateur de DimensionArchitectAPI.
              </Text>
            </Stack>
          </CardBody>
        </Card>
        <Card>
          <CardHeader>Maturité (estimation actuelle dépôt)</CardHeader>
          <CardBody>
            <UsageBar
              total={500}
              topLeftLabel="Scores relatifs"
              topRightLabel="223 / 500"
              segments={[
                { id: "mvp", value: 72, color: "green" },
                { id: "i18n", value: 85, color: "blue" },
                { id: "api", value: 15, color: "orange" },
                { id: "multiloader", value: 30, color: "yellow" },
                { id: "product", value: 8, color: "purple" },
                { id: "qa", value: 13, color: "red" },
              ]}
            />
            <Text tone="tertiary" size="small" style={{ marginTop: 8 }}>
              Vert MVP · Bleu i18n · Orange API maison · Jaune multi-loader · Violet produit
              (GUI/visiteurs) · Rouge tests/docs
            </Text>
          </CardBody>
        </Card>
      </Grid>

      <Card>
        <CardHeader>Historique des phases</CardHeader>
        <CardBody>
          <Table
            headers={["Phase", "Contenu", "Statut réel"]}
            rows={[
              ["1 — PersonalWorld + DimLib", "Item, TP, monde perso via DimLib", "Encore le runtime actuel"],
              ["2 — LiveWorldAPI", "1er nom de l’API maison (remplacer DimLib)", "Renommé → DimensionArchitectAPI"],
              ["3 — DimensionArchitectAPI", "Même mod, nom actuel ; API générique", "Hors repo ; pas encore branché ici"],
              ["4 — Multi-version/loader", "Stonecutter + Architectury + NeoForge", "Scaffold récent, incomplet"],
              ["5 — Produit v1.3+", "GUI, presets, visiteurs, perms", "Non démarré dans le code"],
            ]}
            rowTone={["success", "info", "warning", "info", "neutral"]}
          />
        </CardBody>
      </Card>
    </Stack>
  );
}

function Reconcile() {
  return (
    <Stack gap={16}>
      <H2>Ancien audit vs dépôt actuel</H2>
      <Text tone="secondary">
        Table de confrontation. « Audit » = affirmation de l’ancien document. « Code » =
        constat dans PersonnalWorld aujourd’hui.
      </Text>

      <Table
        headers={["Sujet", "Audit ancien", "Code actuel", "Verdict"]}
        rows={[
          [
            "Stack dimensions",
            "Cible = DimensionArchitectAPI (ex-LiveWorldAPI) pour remplacer DimLib",
            "DimLib encore branché (qouteall)",
            "Migration API non faite",
          ],
          [
            "MC / Java",
            "Figer 1.21.1 + Java 21",
            "1.21.1 / Java 21 + scaffold Stonecutter",
            "Cible OK ; multi-version amorcée",
          ],
          [
            "Objet + cooldown 40 ticks",
            "FAIT",
            "Présent dans PersonnalWorldItem",
            "Confirmé",
          ],
          [
            "Plateforme pierre « supprimée »",
            "FAIT",
            "Clear void platform + fallback 3×3 pierre si NBT absent",
            "Partiel / nuancé",
          ],
          [
            "Traductions 10 langues",
            "FAIT",
            "10 fichiers lang présents",
            "Confirmé",
          ],
          [
            "Position persistante",
            "PARTIEL / à vérifier",
            "Mixin + ReturnPositionSaver NBT",
            "Implémenté (à tester restart)",
          ],
          [
            "Taille île 200×200",
            "Spécification à confirmer",
            "Structure NBT ile_1, spawn hardcodé",
            "Non formalisé",
          ],
          [
            "GUI / presets / visiteurs / perms",
            "v1.3.0 prévu, MANQUANT",
            "Absent (dossier invite/ vide)",
            "Aligné (manquant)",
          ],
          [
            "Multi-loader",
            "Phase tardive (après API stable)",
            "Scaffold Architectury/NeoForge déjà poussé",
            "Ordre inversé vs audit",
          ],
          [
            "Tests / docs API",
            "MANQUANT critique",
            "0 tests ; README décalé",
            "Confirmé",
          ],
          [
            "Persistance propriétaire UUID",
            "Critique à définir",
            "Id dimension perso_<uuid> ; pas de WorldData riche",
            "Minimal seulement",
          ],
          [
            "Lifecycle dimension",
            "REQUESTED→DELETED formalisé",
            "ensurePersonalWorld + PersistentState init",
            "Ad hoc, pas d’API",
          ],
        ]}
        rowTone={[
          "danger",
          "success",
          "success",
          "warning",
          "success",
          "info",
          "warning",
          "neutral",
          "warning",
          "danger",
          "warning",
          "warning",
        ]}
      />

      <Grid columns={2} gap={12}>
        <Card>
          <CardHeader>Ce que l’audit apporte (utile)</CardHeader>
          <CardBody>
            <Stack gap={4}>
              <Text>• Vision API vs mod métier (bonne séparation)</Text>
              <Text>• MVP minimal avant GUI / NeoForge</Text>
              <Text>• Checklist restart serveur / multi-joueurs</Text>
              <Text>• Risques : persistance, mixins, multi-loader tôt</Text>
              <Text>• Roadmap produit v1.3+ (GUI, presets, visiteurs)</Text>
              <Text>• Contrats d’erreur / lifecycle / versioning données</Text>
            </Stack>
          </CardBody>
        </Card>
        <Card>
          <CardHeader>Ce qu’il faut relativiser</CardHeader>
          <CardBody>
            <Stack gap={4}>
              <Text>• Pourcentages d’avancement (§92) — subjectifs</Text>
              <Text>• Cases « FAIT » non revalidées sur le dépôt</Text>
              <Text>• LiveWorldAPI ≠ projet mort : c’est l’ancien nom de DimensionArchitectAPI</Text>
              <Text>• HotDimensions / datapack — contexte historique de l’API</Text>
              <Text>• Multi-loader scaffoldé ici avant bascule DimLib → API maison</Text>
            </Stack>
          </CardBody>
        </Card>
      </Grid>

      <Callout tone="info" title="Direction confirmée (intention)">
        Remplacer DimLib par DimensionArchitectAPI (ex-LiveWorldAPI). En pratique : avancer
        l’API jusqu’à un contrat utilisable, brancher PersonnalWorld dessus, geler NeoForge /
        multi-MC tant que la bascule + MVP restart ne sont pas verts. Le scaffold Architectury
        actuel peut servir, mais ne doit pas précéder la dépendance API.
      </Callout>
    </Stack>
  );
}

function Features() {
  return (
    <Stack gap={16}>
      <H2>Mis en place (dépôt PersonnalWorld)</H2>
      <Table
        headers={["Système", "État", "Notes"]}
        rows={[
          ["Item bâton + cooldown 2s", <Pill tone="success" size="sm">Fait</Pill>, "PersonnalWorldItem"],
          ["Dimension perso DimLib", <Pill tone="success" size="sm">Fait</Pill>, "perso_<uuid>"],
          ["Init one-shot île", <Pill tone="success" size="sm">Fait</Pill>, "PersistentState"],
          ["Île NBT + StructureCopier", <Pill tone="warning" size="sm">Fragile</Pill>, "namespace minecraft:"],
          ["Position retour", <Pill tone="success" size="sm">Fait</Pill>, "À valider restart"],
          ["/returnworld", <Pill tone="success" size="sm">Fait</Pill>, "≠ README /retourmonde"],
          ["Recette + modèle 3D", <Pill tone="success" size="sm">Fait</Pill>, ""],
          ["i18n 10 langues", <Pill tone="success" size="sm">Fait</Pill>, "Clés futures GUI non prêtes"],
          ["Common Architectury", <Pill tone="deleted" size="sm">Stub</Pill>, "Migration annoncée"],
          ["NeoForge", <Pill tone="deleted" size="sm">Stub</Pill>, "Sans DimLib"],
          [
            "DimensionArchitectAPI (ex-LiveWorldAPI)",
            <Pill tone="warning" size="sm">Cible</Pill>,
            "Remplacement DimLib — hors ce repo",
          ],
          ["GUI / presets / visiteurs", <Pill tone="neutral" size="sm">Absent</Pill>, "v1.3+ audit"],
          ["Tests auto", <Pill tone="deleted" size="sm">Absent</Pill>, ""],
        ]}
      />

      <Divider />
      <H3>MVP audit (§86) — checklist</H3>
      <Table
        headers={["Critère MVP", "Statut estimé"]}
        rows={[
          ["Joueur possède un monde", "Oui (par UUID)"],
          ["Monde créé automatiquement", "Oui"],
          ["Téléportation", "Oui (risque race île)"],
          ["Monde sauvegardé / restart", "À valider manuellement"],
          ["Position conservée", "Oui en code ; à valider"],
          ["Suppression propre", "Non"],
        ]}
        rowTone={["success", "success", "warning", "warning", "info", "danger"]}
      />
    </Stack>
  );
}

function Roadmap() {
  return (
    <Stack gap={16}>
      <H2>Plan d’avancement consolidé</H2>
      <Callout tone="warning" title="Ordre recommandé (audit + réalité)">
        Ne pas empiler GUI + NeoForge + presets + multi-MC en parallèle. Stabiliser le MVP
        Fabric restart-proof, trancher DimLib vs API maison, puis seulement élargir loaders /
        versions.
      </Callout>

      <TodoListCard
        defaultExpanded
        todos={[
          {
            id: "decide-stack",
            content:
              "0 — Direction : remplacer DimLib par DimensionArchitectAPI (ex-LiveWorldAPI) — avancer l’API puis rebrancher PersonnalWorld",
            status: "in_progress",
          },
          {
            id: "p0-runtime",
            content:
              "1 — P0 runtime : retirer Thread.sleep serveur ; sync TP ↔ fin génération île ; StructureCopier + namespace",
            status: "pending",
          },
          {
            id: "p0-mvp-test",
            content:
              "2 — Valider MVP audit : create → build → quit → restart serveur → return (checklist §87)",
            status: "pending",
          },
          {
            id: "p1-cleanup",
            content:
              "3 — Phase 0 audit : nettoyer stubs, Mixins exemples, code mort, README, logging structuré",
            status: "pending",
          },
          {
            id: "p1-owner-data",
            content:
              "4 — WorldData minimal : owner UUID, presetId, data_version (base persistance audit)",
            status: "pending",
          },
          {
            id: "p1-delete",
            content: "5 — Suppression dimension : logique vs physique + confirmation",
            status: "pending",
          },
          {
            id: "p2-freeze-neoforge",
            content:
              "6 — Geler NeoForge / multi-MC tant que MVP Fabric non vert (sauf scaffold déjà là)",
            status: "in_progress",
          },
          {
            id: "p2-api-contract",
            content:
              "7 — DimensionArchitectAPI : figer contrat DimensionManager / Request / Teleport / errors ; retirer DimLib de PersonnalWorld",
            status: "pending",
          },
          {
            id: "p3-presets",
            content: "8 — Presets extensibles (default / forest / desert…) avant GUI lourde",
            status: "pending",
          },
          {
            id: "p3-gui",
            content: "9 — GUI : Overview → Island → Visitors → Permissions → Settings",
            status: "pending",
          },
          {
            id: "p3-mp",
            content: "10 — Multijoueur : invite / permissions granulaires / anti-fuite mondes",
            status: "pending",
          },
          {
            id: "p4-neoforge",
            content: "11 — NeoForge réel + tests dédiés (phase tardive audit §80)",
            status: "pending",
          },
          {
            id: "p4-publish",
            content: "12 — Docs API, CHANGELOG, versioning, Modrinth/CurseForge",
            status: "pending",
          },
        ]}
      />

      <Grid columns={3} gap={12}>
        <Card>
          <CardHeader trailing={<Pill size="sm">Maintenant</Pill>}>Fondations</CardHeader>
          <CardBody>
            <Text size="small">
              Bugs runtime, MVP restart, décision DimLib/API, nettoyage. Critère : checklist
              §87 verte sur Fabric 1.21.1.
            </Text>
          </CardBody>
        </Card>
        <Card>
          <CardHeader trailing={<Pill size="sm">Ensuite</Pill>}>Produit</CardHeader>
          <CardBody>
            <Text size="small">
              WorldData, delete, presets, puis GUI et visiteurs. Critère : 2 joueurs, mondes
              isolés, invite basique.
            </Text>
          </CardBody>
        </Card>
        <Card>
          <CardHeader trailing={<Pill size="sm">Plus tard</Pill>}>Scale</CardHeader>
          <CardBody>
            <Text size="small">
              NeoForge, multi-MC Stonecutter, backup/clone, presets externes. Critère : API
              versionnée + CI verte.
            </Text>
          </CardBody>
        </Card>
      </Grid>
    </Stack>
  );
}

function Issues({ accent }: { accent: string }) {
  return (
    <Stack gap={16}>
      <H2>Problèmes & améliorations</H2>

      <H3 style={{ color: accent }}>Critique (code actuel)</H3>
      <Table
        headers={["ID", "Problème", "Action"]}
        rows={[
          ["C1", "Thread.sleep sur thread serveur (création dim)", "Attente non bloquante"],
          ["C2", "Race TP vs génération île async", "Attendre flag initialized"],
          ["C3", "StructureCopier chemin DEV + namespace minecraft:", "Path fabric/ + ns perso"],
          ["C4", "Pas de suppression propre de dimension", "API delete soft/hard"],
        ]}
        rowTone={["danger", "danger", "danger", "warning"]}
      />

      <H3>Écarts architecture (audit)</H3>
      <Table
        headers={["ID", "Problème", "Détail"]}
        rows={[
          [
            "A1",
            "Pas d’API publique locale",
            "Logique dans Item/Util ; audit exige DimensionManager",
          ],
          [
            "A2",
            "Multi-loader avant contrat API",
            "Risque §84.5 de l’audit déjà engagé",
          ],
          [
            "A3",
            "WorldData riche absent",
            "Pas preset/biome/permissions/visiteurs persistés",
          ],
          [
            "A4",
            "Lifecycle non formalisé",
            "Pas d’états REQUESTED…DELETED / erreurs typées",
          ],
          ["A5", "Sécurité multi-joueurs", "Aucune permission ; accès dimension non gated"],
          ["A6", "Stratégie tests absente", "Restart / delete / 2 joueurs non automatisés"],
        ]}
        rowTone={["warning", "warning", "warning", "info", "danger", "danger"]}
      />

      <H3>Dette locale + docs</H3>
      <Table
        headers={["ID", "Sujet", "Détail"]}
        rows={[
          ["D1", "README obsolète", "V1, /retourmonde, recette « plus tard »"],
          ["D2", "CI vs chiseledBuild", "Artefacts / DimLib JAR local"],
          ["D3", "Stubs", "Client typo, ExampleClientMixin, TestCopieNBT, code commenté"],
          ["D4", "System.out / catch vides", "Logs non structurés (cf. audit §59)"],
          ["D5", "Hardcodes", "Spawn 24,68,17 · clear Y · cooldown"],
          ["D6", "Mixins", "Zone à risque audit §34 — limiter + documenter"],
        ]}
        rowTone={["warning", "warning", "info", "info", "info", "warning"]}
      />

      <Callout tone="danger" title="Top 3 immédiat">
        1) Avancer DimensionArchitectAPI jusqu’à un remplacement viable de DimLib · 2)
        Stabiliser création/TP/île (sans sleep) sur le chemin API · 3) Valider MVP restart
        avant NeoForge / multi-MC.
      </Callout>
    </Stack>
  );
}
