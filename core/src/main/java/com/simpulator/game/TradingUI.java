package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TradingUI implements InputProcessor, Renderable {

    public enum State {
        HIDDEN,
        CHOOSING_DIALOGUE,
        TRADE_READY,
        TRADE_RESULT
    }

    public interface TradingUIListener {
        void onDialogueSelected(int optionIndex);
        void onTradeConfirmed(int itemIndex);
        void onTradeCancelled();
        void onTimeUp();
    }

    private State state;
    private TradingUIListener listener;

    private BitmapFont font;
    private GlyphLayout layout;
    private ExtendViewport viewport;
    private TextureRegion white;

    // Timer
    private float timeLeft;
    private boolean timerActive;

    private String npcName = "";
    private String[] dialogueOptions = new String[3];
    private String[] offeredItemNames = new String[3];
    private String[] offeredItemRarities = new String[3];
    private int currentItemIndex = 0;
    private String innerThought = "";
    private String resultText = "";
    private int selectedDialogueIndex = -1;

    // Layout regions
    private Rectangle[] btnRects = new Rectangle[3];
    private Rectangle confirmRect = new Rectangle();
    private Rectangle cancelRect = new Rectangle();
    private Rectangle prevBtnRect = new Rectangle();
    private Rectangle nextBtnRect = new Rectangle();

    public TradingUI(Skin skin) {
        this.state = State.HIDDEN;
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f); // Solid scale perfectly balanced for virtual viewport
        this.layout = new GlyphLayout();
        this.white = new TextureRegion(skin.get("white", Texture.class));
        
        // Use a logical scaling resolution. 
        // Window scales proportionally upwards retaining original item proportions without clipping!
        this.viewport = new ExtendViewport(640, 480);
        
        for (int i = 0; i < 3; i++) {
            btnRects[i] = new Rectangle();
        }
    }

    public void setListener(TradingUIListener listener) {
        this.listener = listener;
    }

    public void show(String npcName, String[] options, String[] itemNames, String[] itemRarities) {
        this.state = State.CHOOSING_DIALOGUE;
        this.timeLeft = 90f; // 1 min 30 sec
        this.timerActive = true;
        this.npcName = npcName;
        for(int i=0; i<3; i++) {
            this.offeredItemNames[i] = (itemNames != null && i < itemNames.length) ? itemNames[i] : "";
            this.offeredItemRarities[i] = (itemRarities != null && i < itemRarities.length) ? itemRarities[i] : "";
        }
        this.currentItemIndex = 0;
        this.innerThought = "";
        this.selectedDialogueIndex = -1;
        
        for (int i = 0; i < 3 && i < options.length; i++) {
            this.dialogueOptions[i] = options[i];
        }
    }

    public void setInnerThoughts(String text) {
        this.innerThought = text;
    }

    public void showTradeResult(String resultText) {
        this.state = State.TRADE_RESULT;
        this.timerActive = false;
        this.resultText = resultText;
    }

    public void hide() {
        this.state = State.HIDDEN;
        this.timerActive = false;
    }

    public boolean isVisible() {
        return state != State.HIDDEN;
    }

    @Override
    public boolean isVisible(Camera camera) {
        return state != State.HIDDEN;
    }

    @Override
    public float getZOrder(Camera camera) {
        return -1;
    }

    public InputProcessor getInputProcessor() {
        return this;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        if (state == State.HIDDEN) return;

        if (timerActive) {
            timeLeft -= Gdx.graphics.getDeltaTime();
            if (timeLeft <= 0) {
                timeLeft = 0;
                timerActive = false;
                if (listener != null) listener.onTimeUp();
            }
        }

        // Apply dynamic projection to prevent maximizing distortion
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        batch.setProjectionMatrix(viewport.getCamera().combined);

        // --- Colors for Ancient France Maritime Vibe ---
        Color goldTrim = new Color(0.83f, 0.68f, 0.21f, 1f);
        Color navyPanel = new Color(0.10f, 0.15f, 0.23f, 0.92f);
        Color parchmentText = new Color(0.96f, 0.87f, 0.70f, 1f);
        Color highlightGold = new Color(0.95f, 0.81f, 0.24f, 1f); // for hovered/active
        Color crimsonCancel = new Color(0.6f, 0.15f, 0.2f, 1f);
        Color seaGreenConfirm = new Color(0.17f, 0.5f, 0.33f, 1f);

        // --- Left Column ---
        float leftMargin = 30;
        float itemBoxY = h / 2 + 10; // moved up
        
        // Item Box
        drawBox(batch, leftMargin, itemBoxY, 160, 160, goldTrim, navyPanel);
        
        // Rarity Box
        drawBox(batch, leftMargin, itemBoxY - 50, 160, 40, goldTrim, navyPanel);

        // Arrow Buttons
        float itemBoxW = 160;
        float itemBoxH = 160;
        prevBtnRect.set(leftMargin - 25, itemBoxY + itemBoxH / 2 - 20, 30, 40);
        nextBtnRect.set(leftMargin + itemBoxW - 5, itemBoxY + itemBoxH / 2 - 20, 30, 40);

        drawBox(batch, prevBtnRect.x, prevBtnRect.y, prevBtnRect.width, prevBtnRect.height, goldTrim, navyPanel);
        drawBox(batch, nextBtnRect.x, nextBtnRect.y, nextBtnRect.width, nextBtnRect.height, goldTrim, navyPanel);

        // --- Bottom Area: Dialogue Box ---
        float dialogueBoxY = 50;
        float dialogueBoxH = 100;
        drawBox(batch, 100, dialogueBoxY, w - 200, dialogueBoxH, goldTrim, navyPanel);

        // --- Right Column: Dialogue Buttons ---
        float rightMargin = w - 280; 
        float btnYStart = h / 2 + 100;
        float btnW = 250;
        float btnH = 60;
        
        for (int i = 0; i < 3; i++) {
            btnRects[i].set(rightMargin, btnYStart - (i * 75), btnW, btnH);
            Color fill = (state == State.TRADE_READY && selectedDialogueIndex == i) ? highlightGold : navyPanel;
            drawBox(batch, btnRects[i].x, btnRects[i].y, btnRects[i].width, btnRects[i].height, goldTrim, fill);
        }

        // --- Center: Confirm/Cancel Buttons ---
        if (state == State.TRADE_READY) {
            float cx = w / 2;
            float cy = 195; 
            float confirmW = 120;
            float confirmH = 45;
            
            // Moved closer to center to avoid overlap with side columns
            confirmRect.set(cx - 130, cy, confirmW, confirmH);
            cancelRect.set(cx + 10, cy, confirmW, confirmH);
            
            drawBox(batch, confirmRect.x, confirmRect.y, confirmRect.width, confirmRect.height, goldTrim, seaGreenConfirm);
            drawBox(batch, cancelRect.x, cancelRect.y, cancelRect.width, cancelRect.height, goldTrim, crimsonCancel);
        }

        // --- Draw Text ---
        // Top text
        font.setColor(goldTrim);
        layout.setText(font, "Trade Offer");
        font.draw(batch, "Trade Offer", w / 2 - layout.width / 2, h - 60); 

        // Timer
        font.setColor(parchmentText);
        int m = (int)(timeLeft / 60);
        int s = (int)(timeLeft % 60);
        String timerStr = String.format("%d:%02d", m, s);
        layout.setText(font, timerStr);
        font.draw(batch, timerStr, w / 2 - layout.width / 2, h - 90); 

        // Left text
        layout.setText(font, "You Receive");
        font.draw(batch, "You Receive", leftMargin + 80 - layout.width/2, itemBoxY + 190);
        
        String currentName = offeredItemNames[currentItemIndex];
        String currentRarity = offeredItemRarities[currentItemIndex];

        layout.setText(font, "Picture of:\n" + currentName);
        font.draw(batch, "Picture of:\n" + currentName, leftMargin + 80 - layout.width/2, itemBoxY + 80 + layout.height/2);
        
        Color rarityColor = parchmentText;
        if (currentRarity != null) {
            String r = currentRarity.toLowerCase();
            if (r.contains("common")) rarityColor = Color.LIGHT_GRAY;
            else if (r.contains("rare")) rarityColor = Color.ROYAL;
            else if (r.contains("epic")) rarityColor = Color.PURPLE;
            else if (r.contains("legendary")) rarityColor = Color.GOLD;
        }
        
        font.setColor(rarityColor);
        layout.setText(font, "Rarity: " + currentRarity);
        font.draw(batch, "Rarity: " + currentRarity, leftMargin + 80 - layout.width/2, itemBoxY - 30 + layout.height/2);
        font.setColor(parchmentText);

        layout.setText(font, "<");
        font.draw(batch, "<", prevBtnRect.x + prevBtnRect.width/2 - layout.width/2, prevBtnRect.y + prevBtnRect.height/2 + layout.height/2);
        layout.setText(font, ">");
        font.draw(batch, ">", nextBtnRect.x + nextBtnRect.width/2 - layout.width/2, nextBtnRect.y + nextBtnRect.height/2 + layout.height/2);

        // Right text
        layout.setText(font, "I Receive");
        font.draw(batch, "I Receive", rightMargin + 125 - layout.width/2, btnYStart + 90);
        
        for (int i = 0; i < 3; i++) {
            if (state == State.TRADE_READY && selectedDialogueIndex == i) font.setColor(Color.BLACK);
            else font.setColor(parchmentText);
            
            layout.setText(font, dialogueOptions[i]);
            font.draw(batch, dialogueOptions[i], btnRects[i].x + btnRects[i].width/2 - layout.width/2, btnRects[i].y + btnRects[i].height/2 + layout.height/2);
        }
        font.setColor(parchmentText);

        // Center text
        if (state == State.TRADE_READY) {
            layout.setText(font, "Confirm");
            font.draw(batch, "Confirm", confirmRect.x + confirmRect.width/2 - layout.width/2, confirmRect.y + confirmRect.height/2 + layout.height/2);
            
            layout.setText(font, "Cancel");
            font.draw(batch, "Cancel", cancelRect.x + cancelRect.width/2 - layout.width/2, cancelRect.y + cancelRect.height/2 + layout.height/2);
        }

        // Bottom text
        if (state == State.TRADE_RESULT) {
            layout.setText(font, resultText);
            font.draw(batch, resultText, w/2 - layout.width/2, dialogueBoxY + 50 + layout.height/2);
        } else {
            String primary = "Trading with " + npcName;
            layout.setText(font, primary);
            font.draw(batch, primary, w/2 - layout.width/2, dialogueBoxY + 65 + layout.height/2);
            
            if (state == State.TRADE_READY && !innerThought.isEmpty()) {
                font.setColor(goldTrim);
                layout.setText(font, innerThought);
                font.draw(batch, innerThought, w/2 - layout.width/2, dialogueBoxY + 35 + layout.height/2);
            }
        }
    }

    private void drawBox(TextureBatch batch, float x, float y, float width, float height, Color border, Color fill) {
        batch.setColor(border);
        batch.draw(white, x - 2, y - 2, width + 4, height + 4);
        batch.setColor(fill);
        batch.draw(white, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        font.dispose();
    }

    // --- Manual GUI Hitbox Detection ---

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO: check for left click
        // TODO: use MouseManager instead of rolling our own input
        if (state == State.HIDDEN || state == State.TRADE_RESULT) return false;

        // Use the viewport to correctly unproject real screen touches to virtual coordinates
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        float x = worldCoords.x;
        float y = worldCoords.y;

        if (state == State.CHOOSING_DIALOGUE || state == State.TRADE_READY) {
            if (prevBtnRect.contains(x, y)) {
                currentItemIndex--;
                if (currentItemIndex < 0) currentItemIndex = 2;
                return true;
            }
            if (nextBtnRect.contains(x, y)) {
                currentItemIndex++;
                if (currentItemIndex > 2) currentItemIndex = 0;
                return true;
            }
        }

        // Check dialogue buttons
        for (int i = 0; i < 3; i++) {
            if (btnRects[i].contains(x, y)) {
                if (state != State.TRADE_READY || selectedDialogueIndex != i) {
                    state = State.TRADE_READY;
                    selectedDialogueIndex = i;
                    if (listener != null) listener.onDialogueSelected(i);
                }
                return true;
            }
        }

        // Check confirm/cancel
        if (state == State.TRADE_READY) {
            if (confirmRect.contains(x, y)) {
                if (listener != null) listener.onTradeConfirmed(currentItemIndex);
                return true;
            }
            if (cancelRect.contains(x, y)) {
                if (listener != null) listener.onTradeCancelled();
                hide();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) { return false; }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }
}
