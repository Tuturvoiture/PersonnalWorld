package fr.galsaxx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Bouton custom carnet : cadre parchemin + icône, ou mode {@link Style#ICON_ONLY} (île / preset).
 */
public final class AdventureBookImageButton extends ButtonWidget {

	public enum Style {
		PARCHMENT,
		ICON_ONLY
	}

	private static final int FRAME = 0xFF5C3A1E;
	private static final int FILL = 0xFFD7C09A;
	private static final int FILL_HOVER = 0xFFE8D4B0;
	private static final int LABEL = 0xFF3A2412;

	private final @Nullable Identifier icon;
	private final int iconTexW;
	private final int iconTexH;
	private final Style style;
	private final boolean selected;

	public AdventureBookImageButton(
			int x,
			int y,
			int width,
			int height,
			Text message,
			@Nullable Identifier icon,
			int iconTexSize,
			PressAction onPress
	) {
		this(x, y, width, height, message, icon, iconTexSize, iconTexSize, Style.PARCHMENT, false, onPress);
	}

	public AdventureBookImageButton(
			int x,
			int y,
			int width,
			int height,
			Text message,
			@Nullable Identifier icon,
			int iconTexW,
			int iconTexH,
			Style style,
			PressAction onPress
	) {
		this(x, y, width, height, message, icon, iconTexW, iconTexH, style, false, onPress);
	}

	public AdventureBookImageButton(
			int x,
			int y,
			int width,
			int height,
			Text message,
			@Nullable Identifier icon,
			int iconTexW,
			int iconTexH,
			Style style,
			boolean selected,
			PressAction onPress
	) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.icon = icon;
		this.iconTexW = iconTexW;
		this.iconTexH = iconTexH;
		this.style = style;
		this.selected = selected;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.style == Style.ICON_ONLY) {
			renderIconOnly(context);
			return;
		}
		renderParchment(context);
	}

	private void renderIconOnly(DrawContext context) {
		if (this.selected) {
			int x = this.getX();
			int y = this.getY();
			context.fill(x - 2, y - 2, x + this.width + 2, y - 1, FRAME);
			context.fill(x - 2, y + this.height + 1, x + this.width + 2, y + this.height + 2, FRAME);
			context.fill(x - 2, y - 2, x - 1, y + this.height + 2, FRAME);
			context.fill(x + this.width + 1, y - 2, x + this.width + 2, y + this.height + 2, FRAME);
		} else if (this.isHovered()) {
			context.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, 0x44F5DEB3);
		}
		if (this.icon != null) {
			AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(this.icon);
			texture.setFilter(false, false);
			context.drawTexture(
					this.icon,
					this.getX(),
					this.getY(),
					this.width,
					this.height,
					0f,
					0f,
					this.iconTexW,
					this.iconTexH,
					this.iconTexW,
					this.iconTexH
			);
		}
		if (!this.getMessage().getString().isEmpty()) {
			context.drawCenteredTextWithShadow(
					MinecraftClient.getInstance().textRenderer,
					this.getMessage(),
					this.getX() + this.width / 2,
					this.getY() + this.height + 2,
					LABEL
			);
		}
	}

	private void renderParchment(DrawContext context) {
		int fill = this.isHovered() ? FILL_HOVER : FILL;
		context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, fill);
		context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, FRAME);
		context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, FRAME);
		context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, FRAME);
		context.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, FRAME);

		int contentTop = this.getY() + 3;
		if (this.icon != null) {
			AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(this.icon);
			texture.setFilter(false, false);
			int iconDraw = Math.min(this.width - 6, this.height - 14);
			int ix = this.getX() + (this.width - iconDraw) / 2;
			context.drawTexture(
					this.icon,
					ix,
					contentTop,
					iconDraw,
					iconDraw,
					0f,
					0f,
					this.iconTexW,
					this.iconTexH,
					this.iconTexW,
					this.iconTexH
			);
			contentTop += iconDraw + 2;
		}

		Text label = this.getMessage();
		int textY = this.icon != null
				? Math.min(contentTop, this.getY() + this.height - 12)
				: this.getY() + (this.height - 8) / 2;
		context.drawCenteredTextWithShadow(
				MinecraftClient.getInstance().textRenderer,
				label,
				this.getX() + this.width / 2,
				textY,
				LABEL
		);
	}
}
