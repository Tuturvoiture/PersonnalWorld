package fr.galsaxx.client;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.island.IslandPageLayout;
import fr.galsaxx.network.CloseAdventureBookPayload;
import fr.galsaxx.network.IslandBookNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Carnet fonctionnel : Mes îles / Îles invitées. Le rendu reste le parchemin existant.
 */
public class AdventureBookScreen extends net.minecraft.client.gui.screen.Screen {
	private static final Identifier PARCHMENT = Identifier.of(PersonnalWorld.MOD_ID, "textures/gui/adventure_book_parchment.png");
	private static final Identifier ISLAND_BUTTON = Identifier.of(PersonnalWorld.MOD_ID, "textures/gui/island_button.png");
	private static final int ISLAND_TEX = 512;
	private static final int ISLAND_BTN = 36;
	private static final int ISLAND_V_DROP = 18;
	private static final int TEX_W = 256;
	private static final int TEX_H = 192;
	private static final int MARGIN = 8;
	private static final float PANEL_Z = 50f;
	private static final float WIDGET_Z = 300f;

	private enum Mode {
		OWNED,
		INVITED,
		PRESET,
		SETTINGS,
		RIGHTS,
		ADD_INVITE,
		INVITED_INFO
	}

	private boolean leaveNotified;
	private Mode mode = Mode.OWNED;
	private int ownedPage = 1;
	private int invitedPage = 1;
	private String selectedDim = "";
	private String selectedPreset = "classic";
	private int panelX;
	private int panelY;
	private int panelW;
	private int panelH;
	private TextFieldWidget nameField;
	private TextFieldWidget jumpField;
	private boolean spawnConfirm;
	private fr.galsaxx.invite.IslandRole inviteRole = fr.galsaxx.invite.IslandRole.VISITOR;
	private int memberPage = 1;
	private int onlinePage = 1;
	private int membersGen = -1;
	/** Rôle au moment d’ouvrir Invitations. */
	private final java.util.Map<java.util.UUID, fr.galsaxx.invite.IslandRole> roleBaseline = new java.util.LinkedHashMap<>();
	/** Dernier rôle choisi dans la page, dans l’ordre des clics. */
	private final java.util.Map<java.util.UUID, fr.galsaxx.invite.IslandRole> roleChosen = new java.util.LinkedHashMap<>();
	private final java.util.Map<java.util.UUID, String> roleNames = new java.util.HashMap<>();

	public AdventureBookScreen() {
		super(Text.translatable("screen.personnalworld.adventure_book"));
	}

	private boolean requested;

	@Override
	protected void init() {
		if (!this.requested) {
			this.requested = true;
			IslandBookNetworking.request("request", "", "", "", "", "");
		}
		rebuild();
	}

	public void refresh() {
		if (this.client != null) {
			rebuild();
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.mode != Mode.RIGHTS) {
			return;
		}
		int gen = fr.galsaxx.network.IslandMembersClientCache.generation();
		if (gen != this.membersGen) {
			this.membersGen = gen;
			rebuild();
		}
	}

	private void rebuild() {
		this.clearChildren();
		layoutPanel();
		if (this.mode == Mode.OWNED || this.mode == Mode.INVITED) {
			buildList();
		} else if (this.mode == Mode.PRESET) {
			buildPreset();
		} else if (this.mode == Mode.SETTINGS) {
			buildSettings();
		} else if (this.mode == Mode.RIGHTS) {
			buildRights();
		} else if (this.mode == Mode.ADD_INVITE) {
			buildAddInvite();
		} else {
			buildInvitedInfo();
		}
	}

	private boolean placedOnActive;

	private void buildList() {
		boolean owned = this.mode == Mode.OWNED;
		addTab(8, Mode.OWNED, "screen.personnalworld.adventure_book.tab.owned");
		addTab(102, Mode.INVITED, "screen.personnalworld.adventure_book.tab.invited");
		List<IslandBookNetworking.Card> cards = owned ? IslandBookClient.owned : IslandBookClient.invited;
		int pages = IslandPageLayout.pageCount(cards.size());
		int page = owned ? this.ownedPage : this.invitedPage;
		if (owned && !this.placedOnActive && !cards.isEmpty()) {
			this.placedOnActive = true;
			page = pageOfActive(cards, pages);
		}
		page = Math.min(Math.max(1, page), pages);
		if (owned) {
			this.ownedPage = page;
		} else {
			this.invitedPage = page;
		}
		IslandPageLayout.Anchors anchors = IslandPageLayout.anchors(page);
		int start = (page - 1) * IslandPageLayout.PAGE_SIZE;
		int top = this.panelY + 52;
		int[] xs = {this.panelX + 28, this.panelX + (this.panelW - ISLAND_BTN) / 2, this.panelX + this.panelW - 28 - ISLAND_BTN};
		int[] biases = {anchors.leftBias(), anchors.centerBias(), anchors.rightBias()};
		for (int slot = 0; slot < IslandPageLayout.PAGE_SIZE; slot++) {
			int index = start + slot;
			if (index >= cards.size()) {
				continue;
			}
			IslandBookNetworking.Card card = cards.get(index);
			int y = top + biases[slot] * ISLAND_V_DROP;
			this.addDrawableChild(new AdventureBookImageButton(
					xs[slot], y, ISLAND_BTN, ISLAND_BTN,
					Text.empty(),
					ISLAND_BUTTON, ISLAND_TEX, ISLAND_TEX,
					AdventureBookImageButton.Style.ICON_ONLY,
					card.active(),
					b -> openCard(card, owned)));
		}
		int foot = this.panelY + this.panelH - 22;
		boolean single = pages <= 1;
		this.addDrawableChild(button(this.panelX + 8, foot, 36, Text.translatable("screen.personnalworld.adventure_book.prev"), single, b -> changePage(-1)));
		this.addDrawableChild(button(this.panelX + 46, foot, 36, Text.translatable("screen.personnalworld.adventure_book.next"), single, b -> changePage(1)));
		this.jumpField = new TextFieldWidget(this.textRenderer, this.panelX + 84, foot, 24, 16, Text.empty());
		this.jumpField.setText(Integer.toString(page));
		this.jumpField.setEditable(!single);
		this.addDrawableChild(this.jumpField);
		this.addDrawableChild(button(this.panelX + 110, foot, 36, Text.translatable("screen.personnalworld.adventure_book.jump"), single, b -> jump()));
		if (owned) {
			boolean capped = IslandBookClient.maxIslands > 0 && IslandBookClient.owned.size() >= IslandBookClient.maxIslands;
			this.addDrawableChild(button(this.panelX + this.panelW - 78, foot, 70,
					Text.translatable("screen.personnalworld.adventure_book.create"),
					capped,
					b -> {
						this.mode = Mode.PRESET;
						this.selectedPreset = IslandBookClient.presets.stream()
								.filter(IslandBookNetworking.PresetInfo::unlocked)
								.map(IslandBookNetworking.PresetInfo::id)
								.findFirst()
								.orElse("classic");
						this.init();
					}));
		}
		this.addDrawableChild(button(this.panelX + this.panelW - 52, this.panelY + 6, 44,
				Text.translatable("screen.personnalworld.adventure_book.close"), false, b -> this.close()));
	}

	private Mode nameFieldMode;

	private void buildPreset() {
		String typed = this.nameFieldMode == Mode.PRESET && this.nameField != null ? this.nameField.getText() : null;
		final int icon = 32;
		final int sidePad = 12;
		int count = Math.max(1, IslandBookClient.presets.size());
		int usable = this.panelW - 2 * sidePad;
		int step = usable / count;
		int i = 0;
		for (IslandBookNetworking.PresetInfo preset : IslandBookClient.presets) {
			IslandBookNetworking.PresetInfo current = preset;
			boolean unlocked = preset.unlocked();
			Text badge = unlocked ? null : Text.translatable("preset.personnalworld.locked_overlay");
			int slotX = this.panelX + sidePad + i * step;
			int btnX = slotX + (step - icon) / 2;
			AdventureBookImageButton btn = new AdventureBookImageButton(
					btnX, this.panelY + 34, icon, icon,
					presetLabel(preset),
					presetIcon(preset.icon()), ISLAND_TEX, ISLAND_TEX,
					AdventureBookImageButton.Style.ICON_ONLY,
					unlocked && preset.id().equals(this.selectedPreset),
					badge,
					step - 4,
					b -> {
						if (!current.unlocked()) {
							return;
						}
						this.selectedPreset = current.id();
						if (this.nameField != null) {
							this.nameField.setText(presetLabel(current).getString());
						}
						this.init();
					});
			btn.active = unlocked;
			this.addDrawableChild(btn);
			i++;
		}
		if (IslandBookClient.presets.stream().noneMatch(p -> p.id().equals(this.selectedPreset) && p.unlocked())) {
			this.selectedPreset = IslandBookClient.presets.stream()
					.filter(IslandBookNetworking.PresetInfo::unlocked)
					.map(IslandBookNetworking.PresetInfo::id)
					.findFirst()
					.orElse("classic");
		}
		this.nameField = new TextFieldWidget(this.textRenderer, this.panelX + 16, this.panelY + this.panelH - 58, this.panelW - 32, 16, Text.empty());
		String presetName = IslandBookClient.presets.stream()
				.filter(p -> p.id().equals(this.selectedPreset))
				.map(p -> presetLabel(p).getString())
				.findFirst()
				.orElse("");
		this.nameField.setText(typed != null ? typed : presetName);
		this.nameFieldMode = Mode.PRESET;
		this.addDrawableChild(this.nameField);
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 36, 70,
				Text.translatable("screen.personnalworld.adventure_book.confirm"), false, b -> {
					IslandBookNetworking.request("create", "", this.selectedPreset, this.nameField.getText(), "", "");
					this.mode = Mode.OWNED;
					this.init();
				}));
		this.addDrawableChild(button(this.panelX + 96, this.panelY + this.panelH - 36, 70,
				Text.translatable("screen.personnalworld.adventure_book.back"), false, b -> {
					this.mode = Mode.OWNED;
					this.init();
				}));
	}

	private void buildSettings() {
		IslandBookNetworking.DetailPayload info = IslandBookClient.detail;
		String typed = this.nameFieldMode == Mode.SETTINGS && this.nameField != null ? this.nameField.getText() : null;
		this.nameField = new TextFieldWidget(this.textRenderer, this.panelX + 16, this.panelY + 28, this.panelW - 112, 16, Text.empty());
		this.nameField.setText(typed != null ? typed : (info == null ? "" : info.displayName()));
		this.nameFieldMode = Mode.SETTINGS;
		this.addDrawableChild(this.nameField);
		this.addDrawableChild(button(this.panelX + this.panelW - 92, this.panelY + 28, 76,
				Text.translatable("screen.personnalworld.adventure_book.rename"), false,
				b -> IslandBookNetworking.request("rename", this.selectedDim, "", this.nameField.getText(), "", "")));
		boolean here = inSelectedDimension();
		if (this.spawnConfirm && here) {
			this.addDrawableChild(button(this.panelX + 16, this.panelY + 80, 70,
					Text.translatable("screen.personnalworld.adventure_book.confirm"), false, b -> {
						this.spawnConfirm = false;
						IslandBookNetworking.request("refreshSpawn", this.selectedDim, "", "", "", "");
					}));
			this.addDrawableChild(button(this.panelX + 96, this.panelY + 80, 70,
					Text.translatable("screen.personnalworld.adventure_book.cancel"), false, b -> {
						this.spawnConfirm = false;
						this.init();
					}));
		} else {
			this.spawnConfirm = false;
			AdventureBookImageButton spawn = button(this.panelX + 16, this.panelY + 80, 150,
					Text.translatable("screen.personnalworld.adventure_book.spawn.edit"), !here, b -> {
						this.spawnConfirm = true;
						this.init();
					});
			if (!here) {
				spawn.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
						Text.translatable("screen.personnalworld.adventure_book.spawn.need_dim")));
			}
			this.addDrawableChild(spawn);
		}
		if (!(this.spawnConfirm && here)) {
			AdventureBookImageButton weather = button(this.panelX + 16, this.panelY + 128, 110,
					Text.translatable("screen.personnalworld.adventure_book.weather.label"), true, b -> {});
			weather.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
					Text.translatable("screen.personnalworld.adventure_book.weather.soon")));
			this.addDrawableChild(weather);
			boolean grief = info != null && info.mobGriefing();
			boolean fire = info != null && info.fire();
			boolean pvp = info != null && info.pvp();
			this.addDrawableChild(button(this.panelX + 16, this.panelY + 108, 72,
					Text.translatable("screen.personnalworld.adventure_book.protect.grief_state", stateWord(grief)), false,
					b -> IslandBookNetworking.request("setOverlay", this.selectedDim, "", "", "mobGriefing", Boolean.toString(!grief))));
			this.addDrawableChild(button(this.panelX + 92, this.panelY + 108, 72,
					Text.translatable("screen.personnalworld.adventure_book.protect.fire_state", stateWord(fire)), false,
					b -> IslandBookNetworking.request("setOverlay", this.selectedDim, "", "", "doFireTick", Boolean.toString(!fire))));
			this.addDrawableChild(button(this.panelX + 168, this.panelY + 108, 72,
					Text.translatable("screen.personnalworld.adventure_book.protect.pvp_state", stateWord(pvp)), false,
					b -> IslandBookNetworking.request("setOverlay", this.selectedDim, "", "", "pvp", Boolean.toString(!pvp))));
		}
		boolean active = IslandBookClient.owned.stream().anyMatch(card -> card.dimensionId().equals(this.selectedDim) && card.active());
		if (!active) {
			this.addDrawableChild(button(this.panelX + 132, this.panelY + 128, 108,
					Text.translatable("screen.personnalworld.adventure_book.set_active"), false,
					b -> IslandBookNetworking.request("setActive", this.selectedDim, "", "", "", "")));
		}
		this.addDrawableChild(button(this.panelX + 16, this.panelY + 150, 110,
				Text.translatable("screen.personnalworld.adventure_book.rights"), false, b -> openRights()));
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 22, 70,
				Text.translatable("screen.personnalworld.adventure_book.back"), false, b -> {
					this.mode = Mode.OWNED;
					this.init();
				}));
	}

	private void openRights() {
		this.mode = Mode.RIGHTS;
		this.memberPage = 1;
		this.membersGen = -1;
		this.roleBaseline.clear();
		this.roleChosen.clear();
		this.roleNames.clear();
		IslandBookNetworking.request("members", this.selectedDim, "", "", "", "");
		this.init();
	}

	private void buildRights() {
		java.util.List<fr.galsaxx.invite.IslandMemberEntry> members = rightsMembers();
		for (fr.galsaxx.invite.IslandMemberEntry entry : members) {
			this.roleBaseline.putIfAbsent(entry.uuid(), entry.role());
			String name = entry.nameHint().isBlank() ? entry.uuid().toString() : entry.nameHint();
			this.roleNames.putIfAbsent(entry.uuid(), name);
		}
		int pages = Math.max(1, (members.size() + 2) / 3);
		this.memberPage = Math.min(Math.max(1, this.memberPage), pages);
		int from = (this.memberPage - 1) * 3;
		for (int i = 0; i < 3 && from + i < members.size(); i++) {
			fr.galsaxx.invite.IslandMemberEntry entry = members.get(from + i);
			int y = this.panelY + 36 + i * 22;
			String targetId = entry.uuid().toString();
			fr.galsaxx.invite.IslandRole shown = shownRole(entry);
			boolean locked = shown == fr.galsaxx.invite.IslandRole.TEMP_VISITOR
					|| shown == fr.galsaxx.invite.IslandRole.BANNED;
			if (!locked) {
				this.addDrawableChild(button(this.panelX + 100, y, 76,
						Text.translatable("screen.personnalworld.adventure_book.role." + shown.name().toLowerCase(java.util.Locale.ROOT)),
						false, b -> {
							this.roleChosen.put(entry.uuid(), nextRole(shownRole(entry)));
							this.roleNames.put(entry.uuid(), entry.nameHint().isBlank() ? targetId : entry.nameHint());
							this.init();
						}));
			}
			this.addDrawableChild(button(this.panelX + 180, y, 60,
					Text.translatable("screen.personnalworld.adventure_book.rights.kick"), false,
					b -> IslandBookNetworking.request("kick", this.selectedDim, "", targetId, "", "")));
		}
		if (pages > 1) {
			this.addDrawableChild(button(this.panelX + 16, this.panelY + 108, 40,
					Text.translatable("screen.personnalworld.adventure_book.prev"), this.memberPage <= 1, b -> {
						this.memberPage--;
						this.init();
					}));
			this.addDrawableChild(button(this.panelX + 60, this.panelY + 108, 40,
					Text.translatable("screen.personnalworld.adventure_book.next"), this.memberPage >= pages, b -> {
						this.memberPage++;
						this.init();
					}));
		}
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 40, 90,
				Text.translatable("screen.personnalworld.adventure_book.rights.add"), false, b -> {
					this.onlinePage = 1;
					this.mode = Mode.ADD_INVITE;
					this.init();
				}));
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 22, 70,
				Text.translatable("screen.personnalworld.adventure_book.back"), false, b -> {
					this.flushRoleEdits();
					boolean ownedHere = IslandBookClient.owned.stream()
							.anyMatch(card -> card.dimensionId().equals(this.selectedDim));
					this.mode = ownedHere ? Mode.SETTINGS : Mode.INVITED_INFO;
					this.init();
				}));
	}

	private fr.galsaxx.invite.IslandRole shownRole(fr.galsaxx.invite.IslandMemberEntry entry) {
		return this.roleChosen.getOrDefault(entry.uuid(), entry.role());
	}

	/** Envoie les rôles modifiés sans message, puis une seule ligne de tchat pour le dernier changement réel. */
	private void flushRoleEdits() {
		java.util.UUID lastId = null;
		fr.galsaxx.invite.IslandRole lastRole = null;
		for (java.util.Map.Entry<java.util.UUID, fr.galsaxx.invite.IslandRole> edit : this.roleChosen.entrySet()) {
			fr.galsaxx.invite.IslandRole start = this.roleBaseline.get(edit.getKey());
			if (start == null || start == edit.getValue()) {
				continue;
			}
			IslandBookNetworking.request(
					"setMemberRole", this.selectedDim, "", edit.getKey().toString(), edit.getValue().name(), "quiet");
			lastId = edit.getKey();
			lastRole = edit.getValue();
		}
		if (lastId != null && lastRole != null && this.client != null && this.client.inGameHud != null) {
			String name = this.roleNames.getOrDefault(lastId, lastId.toString());
			this.client.inGameHud.getChatHud().addMessage(Text.translatable(
					"message.personnalworld.pw.role_ok",
					name,
					Text.translatable("screen.personnalworld.adventure_book.role." + lastRole.name().toLowerCase(java.util.Locale.ROOT))));
		}
		this.roleBaseline.clear();
		this.roleChosen.clear();
		this.roleNames.clear();
	}

	private void buildAddInvite() {
		java.util.List<String> online = onlineNames();
		int pages = IslandPageLayout.pageCount(online.size());
		this.onlinePage = Math.min(Math.max(1, this.onlinePage), pages);
		int from = (this.onlinePage - 1) * IslandPageLayout.PAGE_SIZE;
		for (int i = 0; i < IslandPageLayout.PAGE_SIZE && from + i < online.size(); i++) {
			String name = online.get(from + i);
			int y = this.panelY + 28 + i * 22;
			this.addDrawableChild(button(this.panelX + 16, y, this.panelW - 32, Text.literal(trim(name, 24)), false, b -> {
				if (this.nameField != null) {
					this.nameField.setText(name);
				}
			}));
		}
		if (pages > 1) {
			this.addDrawableChild(button(this.panelX + 16, this.panelY + 96, 40,
					Text.translatable("screen.personnalworld.adventure_book.prev"), this.onlinePage <= 1, b -> {
						this.onlinePage--;
						this.init();
					}));
			this.addDrawableChild(button(this.panelX + 60, this.panelY + 96, 40,
					Text.translatable("screen.personnalworld.adventure_book.next"), this.onlinePage >= pages, b -> {
						this.onlinePage++;
						this.init();
					}));
		}
		String typed = this.nameFieldMode == Mode.ADD_INVITE && this.nameField != null ? this.nameField.getText() : "";
		this.nameField = new TextFieldWidget(this.textRenderer, this.panelX + 16, this.panelY + this.panelH - 22, this.panelW - 32, 16, Text.empty());
		this.nameField.setMaxLength(64);
		this.nameField.setText(typed);
		this.nameField.setPlaceholder(Text.translatable("screen.personnalworld.adventure_book.rights.player"));
		this.nameFieldMode = Mode.ADD_INVITE;
		this.addDrawableChild(this.nameField);
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 42, 90,
				Text.translatable("screen.personnalworld.adventure_book.rights.refresh"), false, b -> this.init()));
		this.addDrawableChild(button(this.panelX + 112, this.panelY + this.panelH - 42, 80,
				Text.translatable("screen.personnalworld.adventure_book.rights.add"), false, b -> {
					IslandBookNetworking.request("invite", this.selectedDim, "", this.nameField.getText(), this.inviteRole.name(), "");
					this.mode = Mode.RIGHTS;
					this.init();
				}));
		this.addDrawableChild(button(this.panelX + this.panelW - 78, this.panelY + 6, 62,
				Text.translatable("screen.personnalworld.adventure_book.back"), false, b -> {
					this.mode = Mode.RIGHTS;
					this.init();
				}));
	}

	private java.util.List<String> onlineNames() {
		if (this.client == null || this.client.getNetworkHandler() == null) {
			return java.util.List.of();
		}
		String self = this.client.player == null ? "" : this.client.player.getGameProfile().getName();
		return this.client.getNetworkHandler().getPlayerList().stream()
				.map(entry -> entry.getProfile().getName())
				.filter(name -> name != null && !name.isBlank() && !name.equalsIgnoreCase(self))
				.distinct()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();
	}

	private Text stateWord(boolean on) {
		return Text.translatable(on
				? "screen.personnalworld.adventure_book.state.on"
				: "screen.personnalworld.adventure_book.state.off");
	}

	private static Text presetLabel(IslandBookNetworking.PresetInfo preset) {
		String key = "preset.personnalworld." + preset.id();
		String translated = Text.translatable(key).getString();
		if (translated.equals(key)) {
			return Text.literal(preset.name());
		}
		return Text.translatable(key);
	}

	private static fr.galsaxx.invite.IslandRole nextRole(fr.galsaxx.invite.IslandRole role) {
		return switch (role) {
			case VISITOR -> fr.galsaxx.invite.IslandRole.BUILDER;
			case BUILDER -> fr.galsaxx.invite.IslandRole.CO_CREATOR;
			default -> fr.galsaxx.invite.IslandRole.VISITOR;
		};
	}

	private static String trim(String value, int max) {
		if (value.length() <= max) {
			return value;
		}
		return value.substring(0, max);
	}

	private boolean inSelectedDimension() {
		return this.client != null
				&& this.client.world != null
				&& this.client.world.getRegistryKey().getValue().toString().equals(this.selectedDim);
	}

	private void buildInvitedInfo() {
		this.addDrawableChild(button(this.panelX + 16, this.panelY + 110, 90,
				Text.translatable("screen.personnalworld.adventure_book.join"), false,
				b -> {
					IslandBookNetworking.request("visit", this.selectedDim, "", "", "", "");
					this.close();
				}));
		boolean coCreator = IslandBookClient.invited.stream()
				.anyMatch(card -> card.dimensionId().equals(this.selectedDim) && "CO_CREATOR".equals(card.role()));
		if (coCreator) {
			this.addDrawableChild(button(this.panelX + 112, this.panelY + 110, 110,
					Text.translatable("screen.personnalworld.adventure_book.rights"), false, b -> openRights()));
		}
		this.addDrawableChild(button(this.panelX + 16, this.panelY + this.panelH - 22, 70,
				Text.translatable("screen.personnalworld.adventure_book.back"), false, b -> {
					this.mode = Mode.INVITED;
					this.init();
				}));
	}

	private static Identifier presetIcon(String icon) {
		if (icon == null || icon.isBlank() || "textures/gui/island_button.png".equals(icon)) {
			return ISLAND_BUTTON;
		}
		Identifier parsed = Identifier.tryParse(icon.contains(":") ? icon : PersonnalWorld.MOD_ID + ":" + icon);
		return parsed == null ? ISLAND_BUTTON : parsed;
	}

	private void openCard(IslandBookNetworking.Card card, boolean owned) {
		this.selectedDim = card.dimensionId();
		if (owned) {
			this.mode = Mode.SETTINGS;
			IslandBookNetworking.request("detail", card.dimensionId(), "", "", "", "");
		} else {
			this.mode = Mode.INVITED_INFO;
		}
		this.init();
	}

	private void changePage(int delta) {
		if (this.mode == Mode.OWNED) {
			int pages = IslandPageLayout.pageCount(IslandBookClient.owned.size());
			this.ownedPage = delta < 0
					? IslandPageLayout.tryPrevious(this.ownedPage, pages)
					: IslandPageLayout.tryNext(this.ownedPage, pages);
		} else {
			int pages = IslandPageLayout.pageCount(IslandBookClient.invited.size());
			this.invitedPage = delta < 0
					? IslandPageLayout.tryPrevious(this.invitedPage, pages)
					: IslandPageLayout.tryNext(this.invitedPage, pages);
		}
		this.init();
	}

	private void jump() {
		if (this.jumpField == null) {
			return;
		}
		int target;
		try {
			target = Integer.parseInt(this.jumpField.getText().trim());
		} catch (NumberFormatException e) {
			return;
		}
		if (this.mode == Mode.OWNED) {
			int pages = IslandPageLayout.pageCount(IslandBookClient.owned.size());
			this.ownedPage = IslandPageLayout.tryJump(this.ownedPage, pages, target);
		} else {
			int pages = IslandPageLayout.pageCount(IslandBookClient.invited.size());
			this.invitedPage = IslandPageLayout.tryJump(this.invitedPage, pages, target);
		}
		this.init();
	}

	private int pageOfActive(List<IslandBookNetworking.Card> cards, int pages) {
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).active()) {
				int page = IslandPageLayout.pageOfIsland(i);
				return Math.min(page, pages);
			}
		}
		return 1;
	}

	private void addTab(int x, Mode target, String key) {
		boolean current = this.mode == target;
		AdventureBookImageButton tab = new AdventureBookImageButton(
				this.panelX + x, this.panelY + 6, 88, 16,
				Text.translatable(key), null, 0, 0,
				AdventureBookImageButton.Style.PARCHMENT, current,
				b -> {
					if (!current) {
						this.mode = target;
						this.init();
					}
				});
		this.addDrawableChild(tab);
	}

	private AdventureBookImageButton button(int x, int y, int w, Text text, boolean inactive, net.minecraft.client.gui.widget.ButtonWidget.PressAction action) {
		AdventureBookImageButton widget = new AdventureBookImageButton(x, y, w, 16, text, null, 0, action);
		widget.active = !inactive;
		return widget;
	}

	private void layoutPanel() {
		int availW = Math.max(160, this.width - MARGIN * 2);
		int availH = Math.max(120, this.height - MARGIN * 2);
		this.panelW = Math.min(TEX_W, availW);
		this.panelH = Math.min(TEX_H, availH);
		this.panelX = (this.width - this.panelW) / 2;
		this.panelY = (this.height - this.panelH) / 2;
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, this.width, this.height, 0xB0101010);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		context.getMatrices().push();
		context.getMatrices().translate(0f, 0f, PANEL_Z);
		drawParchment(context);
		context.getMatrices().pop();
		context.getMatrices().push();
		context.getMatrices().translate(0f, 0f, WIDGET_Z);
		super.render(context, mouseX, mouseY, delta);
		context.getMatrices().pop();
		context.getMatrices().push();
		context.getMatrices().translate(0f, 0f, WIDGET_Z + 50f);
		drawLabels(context);
		context.getMatrices().pop();
	}

	private void drawParchment(DrawContext context) {
		AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(PARCHMENT);
		texture.setFilter(false, false);
		if (this.panelW == TEX_W && this.panelH == TEX_H) {
			context.drawTexture(PARCHMENT, this.panelX, this.panelY, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
		} else {
			context.drawTexture(PARCHMENT, this.panelX, this.panelY, 0, 0, this.panelW, this.panelH, TEX_W, TEX_H);
		}
	}

	private void drawOutlined(DrawContext context, Text text, int centerX, int y) {
		drawOutlinedAt(context, text, centerX - this.textRenderer.getWidth(text) / 2, y);
	}

	private void drawOutlinedAt(DrawContext context, Text text, int x, int y) {
		int outline = 0xFF000000;
		context.drawText(this.textRenderer, text, x - 1, y, outline, false);
		context.drawText(this.textRenderer, text, x + 1, y, outline, false);
		context.drawText(this.textRenderer, text, x, y - 1, outline, false);
		context.drawText(this.textRenderer, text, x, y + 1, outline, false);
		context.drawText(this.textRenderer, text, x, y, 0xFFFFFFFF, false);
	}

	private void drawLabels(DrawContext context) {
		if (this.mode == Mode.OWNED || this.mode == Mode.INVITED) {
			boolean owned = this.mode == Mode.OWNED;
			List<IslandBookNetworking.Card> cards = owned ? IslandBookClient.owned : IslandBookClient.invited;
			int pages = IslandPageLayout.pageCount(cards.size());
			int page = owned ? this.ownedPage : this.invitedPage;
			drawOutlined(context, Text.literal(IslandPageLayout.label(page, pages)), this.panelX + this.panelW / 2, this.panelY + 26);
			if (cards.isEmpty()) {
				drawOutlined(context,
						Text.translatable(owned
								? "screen.personnalworld.adventure_book.empty.owned"
								: "screen.personnalworld.adventure_book.empty.invited"),
						this.panelX + this.panelW / 2, this.panelY + 80);
				return;
			}
			drawOutlined(context,
					Text.translatable(owned
							? "screen.personnalworld.adventure_book.hint.owned"
							: "screen.personnalworld.adventure_book.hint.invited"),
					this.panelX + this.panelW / 2, this.panelY + 38);
			int start = (page - 1) * IslandPageLayout.PAGE_SIZE;
			IslandPageLayout.Anchors anchors = IslandPageLayout.anchors(page);
			int[] xs = {this.panelX + 28, this.panelX + (this.panelW - ISLAND_BTN) / 2, this.panelX + this.panelW - 28 - ISLAND_BTN};
			int[] biases = {anchors.leftBias(), anchors.centerBias(), anchors.rightBias()};
			for (int slot = 0; slot < IslandPageLayout.PAGE_SIZE; slot++) {
				int index = start + slot;
				if (index >= cards.size()) {
					continue;
				}
				IslandBookNetworking.Card card = cards.get(index);
				int y = this.panelY + 52 + biases[slot] * ISLAND_V_DROP + ISLAND_BTN + 6;
				String name = card.displayName().isBlank() ? "?" : card.displayName();
				if (this.textRenderer.getWidth(name) > 64) {
					name = this.textRenderer.trimToWidth(name, 64);
				}
				drawOutlined(context, Text.literal(name), xs[slot] + ISLAND_BTN / 2, y);
				int next = y + 9;
				if (!owned && card.ownerName() != null && !card.ownerName().isBlank()) {
					String owner = card.ownerName();
					if (this.textRenderer.getWidth(owner) > 64) {
						owner = this.textRenderer.trimToWidth(owner, 64);
					}
					drawOutlined(context, Text.literal(owner), xs[slot] + ISLAND_BTN / 2, next);
					next += 9;
				}
				if (owned && card.active()) {
					drawOutlined(context,
							Text.translatable("screen.personnalworld.adventure_book.active_mark"),
							xs[slot] + ISLAND_BTN / 2, next);
				}
				if (!owned) {
					drawOutlined(context,
							Text.translatable("screen.personnalworld.adventure_book.role." + card.role().toLowerCase(java.util.Locale.ROOT)),
							xs[slot] + ISLAND_BTN / 2, next);
				}
			}
		} else if (this.mode == Mode.PRESET) {
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.preset.title"), this.panelX + 16, this.panelY + 8);
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.preset.name"), this.panelX + 16, this.panelY + this.panelH - 70);
		} else if (this.mode == Mode.INVITED_INFO) {
			IslandBookNetworking.Card card = IslandBookClient.invited.stream().filter(c -> c.dimensionId().equals(this.selectedDim)).findFirst().orElse(null);
			if (card != null) {
				drawOutlined(context, Text.literal(card.displayName()), this.panelX + this.panelW / 2, this.panelY + 40);
				drawOutlined(context,
						Text.translatable("screen.personnalworld.adventure_book.role." + card.role().toLowerCase(java.util.Locale.ROOT)),
						this.panelX + this.panelW / 2, this.panelY + 56);
				drawOutlined(context,
						Text.translatable("screen.personnalworld.adventure_book.owner_line", card.ownerName()),
						this.panelX + this.panelW / 2, this.panelY + 70);
				drawOutlined(context,
						Text.translatable("screen.personnalworld.adventure_book.invited.readonly"),
						this.panelX + this.panelW / 2, this.panelY + 90);
			}
		} else if (this.mode == Mode.SETTINGS && IslandBookClient.detail != null) {
			IslandBookNetworking.DetailPayload info = IslandBookClient.detail;
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.section.name"), this.panelX + 16, this.panelY + 16);
			String coords = info.hasSpawn() ? info.x() + " " + info.y() + " " + info.z() : "-";
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.spawn", coords), this.panelX + 16, this.panelY + 50);
			if (!inSelectedDimension()) {
				drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.spawn.need_dim"), this.panelX + 16, this.panelY + 64);
			} else if (this.spawnConfirm) {
				drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.spawn.confirm"), this.panelX + 16, this.panelY + 64);
			}
			boolean active = IslandBookClient.owned.stream().anyMatch(card -> card.dimensionId().equals(this.selectedDim) && card.active());
			if (active) {
				drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.is_active"), this.panelX + 132, this.panelY + 132);
			}
		} else if (this.mode == Mode.RIGHTS) {
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.rights.title"), this.panelX + 16, this.panelY + 8);
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.rights.hint"), this.panelX + 16, this.panelY + 22);
			java.util.List<fr.galsaxx.invite.IslandMemberEntry> members = rightsMembers();
			int pages = Math.max(1, (members.size() + 2) / 3);
			int page = Math.min(Math.max(1, this.memberPage), pages);
			int from = (page - 1) * 3;
			if (members.isEmpty()) {
				drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.rights.empty"), this.panelX + 16, this.panelY + 48);
			}
			for (int i = 0; i < 3 && from + i < members.size(); i++) {
				fr.galsaxx.invite.IslandMemberEntry entry = members.get(from + i);
				String who = entry.nameHint().isBlank() ? entry.uuid().toString() : entry.nameHint();
				int row = this.panelY + 40 + i * 22;
				drawOutlinedAt(context, Text.literal(trim(who, 12)), this.panelX + 16, row);
				boolean locked = entry.role() == fr.galsaxx.invite.IslandRole.TEMP_VISITOR
						|| entry.role() == fr.galsaxx.invite.IslandRole.BANNED;
				if (locked) {
					drawOutlinedAt(context,
							Text.translatable("screen.personnalworld.adventure_book.role." + entry.role().name().toLowerCase(java.util.Locale.ROOT)),
							this.panelX + 100, row);
				}
			}
		} else if (this.mode == Mode.ADD_INVITE) {
			drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.rights.online"), this.panelX + 16, this.panelY + 8);
			java.util.List<String> online = onlineNames();
			if (online.isEmpty()) {
				drawOutlinedAt(context, Text.translatable("screen.personnalworld.adventure_book.rights.online_empty"), this.panelX + 16, this.panelY + 36);
			}
			int pages = IslandPageLayout.pageCount(online.size());
			if (pages > 1) {
				drawOutlinedAt(context, Text.literal(IslandPageLayout.label(this.onlinePage, pages)), this.panelX + 110, this.panelY + 100);
			}
		}
	}

	private java.util.List<fr.galsaxx.invite.IslandMemberEntry> rightsMembers() {
		return fr.galsaxx.network.IslandMembersClientCache
				.forDimension(this.selectedDim)
				.map(fr.galsaxx.network.SyncIslandMembersPayload::members)
				.orElse(java.util.List.of())
				.stream()
				.filter(entry -> entry.role() != fr.galsaxx.invite.IslandRole.OWNER)
				.toList();
	}

	@Override
	public void close() {
		this.flushRoleEdits();
		notifyLeave();
		super.close();
	}

	@Override
	public void removed() {
		this.flushRoleEdits();
		notifyLeave();
		super.removed();
	}

	private void notifyLeave() {
		if (this.leaveNotified) {
			return;
		}
		this.leaveNotified = true;
		if (MinecraftClient.getInstance().player != null) {
			AdventureBookClientPose.beginClosing(MinecraftClient.getInstance().player.getUuid(), true);
		} else {
			AdventureBookClientPose.setLocalReading(false);
		}
		NetworkManager.sendToServer(new CloseAdventureBookPayload());
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
