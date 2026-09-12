package fr.galsaxx.client;

import dev.architectury.networking.NetworkManager;
import fr.galsaxx.network.CloseAdventureBookPayload;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Interface temporaire du carnet d'aventurier.
 * Fermeture → envoie CLOSE_BOOK_PACKET pour déclencher l'animation de fermeture.
 */
public class AdventureBookScreen extends Screen {

    public AdventureBookScreen() {
        super(Text.translatable("screen.personnalworld.adventure_book"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Titre centré
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height / 2 - 20,
                0xF5DEB3
        );
        // Sous-titre provisoire
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.personnalworld.adventure_book.wip"),
                this.width / 2,
                this.height / 2,
                0xAAAAAA
        );
    }

    @Override
    public void close() {
        super.close();
        // Prévenir le serveur pour déclencher l'anim de fermeture
        NetworkManager.sendToServer(new CloseAdventureBookPayload());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
