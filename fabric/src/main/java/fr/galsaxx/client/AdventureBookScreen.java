package fr.galsaxx.client;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.PersonnalWorld;
import fr.galsaxx.network.CloseAdventureBookPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Interface test du carnet — style parchemin / carte ancienne.
 * <p>
 * Calques : fond assombri → parchemin/texte → widgets (boutons custom au premier plan).
 * Onglet Îles : boutons image ({@link AdventureBookImageButton}) — base pour presets / permissions.
 */
public class AdventureBookScreen extends Screen {

	private static final Identifier PARCHMENT =
			Identifier.of(PersonnalWorld.MOD_ID, "textures/gui/adventure_book_parchment.png");
	private static final Identifier ISLAND_BUTTON =
			Identifier.of(PersonnalWorld.MOD_ID, "textures/gui/island_button.png");

	/** Texture logo île (512×512, fond transparent). */
	private static final int ISLAND_TEX = 512;
	/** Taille affichée d’un bouton île dans le carnet. */
	private static final int ISLAND_BTN = 48;
	/** Décalage vertical du bouton central (forme en V). */
	private static final int ISLAND_V_DROP = 26;

	private static final int TEX_W = 256;
	private static final int TEX_H = 192;
	private static final int MARGIN = 8;

	private static final float PANEL_Z = 50f;
	private static final float WIDGET_Z = 300f;

	private static final int INK = 0xFF3A2412;
	private static final int INK_MUTED = 0xFF6B4A2E;
	private static final int TAB_ACTIVE = 0xFF5C3A1E;
	private static final int TAB_IDLE = 0xFF8B6840;

	private enum Tab {
		MAP,
		ISLANDS,
		NOTES
	}

	private boolean leaveNotified;
	private Tab tab = Tab.MAP;
	/** 0 = gauche, 1 = centre (bas), 2 = droite — test sélection preset. */
	private int selectedIsland = -1;

	private int panelX;
	private int panelY;
	private int panelW;
	private int panelH;

	public AdventureBookScreen() {
		super(Text.translatable("screen.personnalworld.adventure_book"));
	}

	@Override
	protected void init() {
		this.clearChildren();
		layoutPanel();
		rebuildChrome();
		rebuildTabContent();
	}

	private void setTab(Tab next) {
		if (this.tab == next) {
			return;
		}
		this.tab = next;
		this.clearChildren();
		rebuildChrome();
		rebuildTabContent();
	}

	/** Onglets + fermer (toujours visibles). */
	private void rebuildChrome() {
		int tabY = this.panelY + 8;
		int tabH = 18;
		int gap = 4;
		int tabW = (this.panelW - 24 - gap * 2) / 3;
		int tabX0 = this.panelX + 12;

		addTabButton(tabX0, tabY, tabW, tabH, Tab.MAP, "screen.personnalworld.adventure_book.tab.map");
		addTabButton(tabX0 + tabW + gap, tabY, tabW, tabH, Tab.ISLANDS, "screen.personnalworld.adventure_book.tab.islands");
		addTabButton(tabX0 + (tabW + gap) * 2, tabY, tabW, tabH, Tab.NOTES, "screen.personnalworld.adventure_book.tab.notes");

		int btnW = Math.min(120, this.panelW - 40);
		int btnH = 20;
		this.addDrawableChild(new AdventureBookImageButton(
				this.panelX + (this.panelW - btnW) / 2,
				this.panelY + this.panelH - btnH - 8,
				btnW,
				btnH,
				Text.translatable("screen.personnalworld.adventure_book.close"),
				null,
				0,
				b -> this.close()
		));
	}

	/**
	 * Onglet Îles : 3 boutons logo en V (gauche / centre bas / droite).
	 */
	private void rebuildTabContent() {
		if (this.tab != Tab.ISLANDS) {
			return;
		}
		int margin = 14;
		int topY = this.panelY + 46;
		int centerY = topY + ISLAND_V_DROP;
		int leftX = this.panelX + margin;
		int rightX = this.panelX + this.panelW - margin - ISLAND_BTN;
		int centerX = this.panelX + (this.panelW - ISLAND_BTN) / 2;

		addIslandButton(leftX, topY, 0, "screen.personnalworld.adventure_book.island.slot_left");
		addIslandButton(centerX, centerY, 1, "screen.personnalworld.adventure_book.island.slot_center");
		addIslandButton(rightX, topY, 2, "screen.personnalworld.adventure_book.island.slot_right");
	}

	private void addIslandButton(int x, int y, int index, String labelKey) {
		this.addDrawableChild(new AdventureBookImageButton(
				x,
				y,
				ISLAND_BTN,
				ISLAND_BTN,
				Text.translatable(labelKey),
				ISLAND_BUTTON,
				ISLAND_TEX,
				ISLAND_TEX,
				AdventureBookImageButton.Style.ICON_ONLY,
				this.selectedIsland == index,
				b -> {
					this.selectedIsland = index;
					this.init();
				}
		));
	}

	private void addTabButton(int x, int y, int w, int h, Tab target, String key) {
		this.addDrawableChild(new AdventureBookImageButton(
				x, y, w, h,
				Text.translatable(key),
				null,
				0,
				b -> setTab(target)
		));
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
		drawTabHighlight(context);
		drawBody(context);
		context.getMatrices().pop();

		// Widgets au premier plan (au-dessus du parchemin)
		context.getMatrices().push();
		context.getMatrices().translate(0f, 0f, WIDGET_Z);
		super.render(context, mouseX, mouseY, delta);
		context.getMatrices().pop();
	}

	private void drawParchment(DrawContext context) {
		context.fill(this.panelX - 2, this.panelY - 2, this.panelX + this.panelW + 2, this.panelY + this.panelH + 2, 0x66000000);

		AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(PARCHMENT);
		texture.setFilter(false, false);

		if (this.panelW == TEX_W && this.panelH == TEX_H) {
			context.drawTexture(PARCHMENT, this.panelX, this.panelY, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);
		} else {
			int u = (TEX_W - this.panelW) / 2;
			int v = (TEX_H - this.panelH) / 2;
			context.drawTexture(
					PARCHMENT,
					this.panelX,
					this.panelY,
					u,
					v,
					this.panelW,
					this.panelH,
					TEX_W,
					TEX_H
			);
		}
	}

	private void drawTabHighlight(DrawContext context) {
		int gap = 4;
		int tabW = (this.panelW - 24 - gap * 2) / 3;
		int tabX0 = this.panelX + 12;
		int idx = this.tab.ordinal();
		int x = tabX0 + idx * (tabW + gap);
		int y = this.panelY + 26;
		context.fill(x + 4, y, x + tabW - 4, y + 2, TAB_ACTIVE);
	}

	private void drawBody(DrawContext context) {
		int titleY = this.panelY + 32;
		context.drawCenteredTextWithShadow(
				this.textRenderer,
				this.title,
				this.panelX + this.panelW / 2,
				titleY,
				INK
		);

		if (this.tab == Tab.ISLANDS) {
			Text hint = this.selectedIsland >= 0
					? Text.translatable(
					"screen.personnalworld.adventure_book.body.islands_selected",
					Text.translatable(switch (this.selectedIsland) {
						case 0 -> "screen.personnalworld.adventure_book.island.slot_left";
						case 1 -> "screen.personnalworld.adventure_book.island.slot_center";
						default -> "screen.personnalworld.adventure_book.island.slot_right";
					})
			)
					: Text.translatable("screen.personnalworld.adventure_book.body.islands_hint");
			context.drawCenteredTextWithShadow(
					this.textRenderer,
					hint,
					this.panelX + this.panelW / 2,
					this.panelY + this.panelH - 36,
					INK_MUTED
			);
			return;
		}

		int contentTop = titleY + 14;
		int contentBottom = this.panelY + this.panelH - 36;
		int contentLeft = this.panelX + 16;
		int contentWidth = this.panelW - 32;

		context.fill(contentLeft, contentTop - 4, contentLeft + contentWidth, contentTop - 3, 0x553A2412);

		Text body = switch (this.tab) {
			case MAP -> Text.translatable("screen.personnalworld.adventure_book.body.map");
			case NOTES -> Text.translatable("screen.personnalworld.adventure_book.body.notes");
			case ISLANDS -> Text.empty();
		};

		List<OrderedText> lines = this.textRenderer.wrapLines(body, contentWidth);
		int maxLines = Math.max(1, (contentBottom - contentTop) / (this.textRenderer.fontHeight + 2));
		int y = contentTop;
		for (int i = 0; i < Math.min(lines.size(), maxLines); i++) {
			context.drawText(this.textRenderer, lines.get(i), contentLeft, y, INK_MUTED, false);
			y += this.textRenderer.fontHeight + 2;
		}

		drawCompassHint(context, this.panelX + this.panelW - 28, contentBottom - 6);
	}

	private void drawCompassHint(DrawContext context, int cx, int cy) {
		context.fill(cx - 1, cy - 6, cx + 1, cy + 6, TAB_IDLE);
		context.fill(cx - 6, cy - 1, cx + 6, cy + 1, TAB_IDLE);
		context.fill(cx - 1, cy - 7, cx + 1, cy - 4, TAB_ACTIVE);
	}

	@Override
	public void close() {
		notifyLeave();
		super.close();
	}

	@Override
	public void removed() {
		notifyLeave();
		super.removed();
	}

	private void notifyLeave() {
		if (this.leaveNotified) {
			return;
		}
		this.leaveNotified = true;
		AdventureBookClientPose.setLocalReading(false);
		NetworkManager.sendToServer(new CloseAdventureBookPayload());
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
