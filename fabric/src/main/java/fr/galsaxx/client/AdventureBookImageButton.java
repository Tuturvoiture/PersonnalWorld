package fr.galsaxx.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

/**
 * Bouton custom carnet : cadre parchemin + icône, ou mode {@link Style#ICON_ONLY} (île / preset).
 */
public final class AdventureBookImageButton extends ButtonWidget {

	public enum Style {
		PARCHMENT,
		ICON_ONLY
	}

	private static final int FRAME = 0xFF2A1810;
	private static final int FILL = 0xFFFFF6E4;
	private static final int FILL_HOVER = 0xFFFFFFFF;
	private static final int FILL_OFF = 0xFFE4D4BC;
	private static final int LABEL = 0xFF140C06;

	private final @Nullable Identifier icon;
	private final int iconTexW;
	private final int iconTexH;
	private final Style style;
	private final boolean selected;
	private final @Nullable Text lockedBadge;
	private final int labelMaxWidth;

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
		this(x, y, width, height, message, icon, iconTexSize, iconTexSize, Style.PARCHMENT, false, null, width, onPress);
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
		this(x, y, width, height, message, icon, iconTexW, iconTexH, style, false, null, width, onPress);
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
		this(x, y, width, height, message, icon, iconTexW, iconTexH, style, selected, null, width + 10, onPress);
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
			@Nullable Text lockedBadge,
			int labelMaxWidth,
			PressAction onPress
	) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.icon = icon;
		this.iconTexW = iconTexW;
		this.iconTexH = iconTexH;
		this.style = style;
		this.selected = selected;
		this.lockedBadge = lockedBadge;
		this.labelMaxWidth = Math.max(8, labelMaxWidth);
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
		} else if (this.isHovered() && this.active) {
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
		if (!this.active) {
			context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x99000000);
		}
		if (this.lockedBadge != null && !this.active) {
			drawDiagonalBadge(context, this.lockedBadge);
		}
		if (!this.getMessage().getString().isEmpty()) {
			var renderer = MinecraftClient.getInstance().textRenderer;
			Text label = this.getMessage();
			if (renderer.getWidth(label) > this.labelMaxWidth) {
				label = Text.literal(renderer.trimToWidth(label.getString(), this.labelMaxWidth));
			}
			int x = this.getX() + (this.width - renderer.getWidth(label)) / 2;
			int y = this.getY() + this.height + 4;
			int outline = 0xFF000000;
			int fill = this.active ? 0xFFFFFFFF : 0xFF9A9A9A;
			context.drawText(renderer, label, x - 1, y, outline, false);
			context.drawText(renderer, label, x + 1, y, outline, false);
			context.drawText(renderer, label, x, y - 1, outline, false);
			context.drawText(renderer, label, x, y + 1, outline, false);
			context.drawText(renderer, label, x, y, fill, false);
		}
	}

	private void drawDiagonalBadge(DrawContext context, Text badge) {
		var renderer = MinecraftClient.getInstance().textRenderer;
		String raw = badge.getString();
		int cx = this.getX() + this.width / 2;
		int cy = this.getY() + this.height / 2;
		context.getMatrices().push();
		context.getMatrices().translate(cx, cy, 0);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-32f));
		int w = renderer.getWidth(raw);
		int tx = -w / 2;
		int ty = -4;
		int outline = 0xFF000000;
		int fill = 0xFFFFE082;
		context.drawText(renderer, raw, tx - 1, ty, outline, false);
		context.drawText(renderer, raw, tx + 1, ty, outline, false);
		context.drawText(renderer, raw, tx, ty - 1, outline, false);
		context.drawText(renderer, raw, tx, ty + 1, outline, false);
		context.drawText(renderer, raw, tx, ty, fill, false);
		context.getMatrices().pop();
	}

	private void renderParchment(DrawContext context) {
		int fill = !this.active ? FILL_OFF : (this.selected ? 0xFFFFD98A : (this.isHovered() ? FILL_HOVER : FILL));
		int frame = this.selected ? 0xFF140C06 : FRAME;
		context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, fill);
		context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, frame);
		context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, frame);
		context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, frame);
		context.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, frame);

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

		Text label = fittedLabel();
		var renderer = MinecraftClient.getInstance().textRenderer;
		int textY = this.icon != null
				? Math.min(contentTop, this.getY() + this.height - 12)
				: this.getY() + (this.height - 8) / 2;
		int textX = this.getX() + (this.width - renderer.getWidth(label)) / 2;
		context.drawText(renderer, label, textX, textY, this.active ? LABEL : 0xFF4A3C2C, false);
	}

	private Text fittedLabel() {
		Text label = this.getMessage();
		var renderer = MinecraftClient.getInstance().textRenderer;
		int max = Math.max(8, this.width - 6);
		if (renderer.getWidth(label) <= max) {
			return label;
		}
		return Text.literal(renderer.trimToWidth(label.getString(), max));
	}
}
