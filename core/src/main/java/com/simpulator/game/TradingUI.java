package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.Widget;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.input.MouseManager.MouseButton;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import com.simpulator.engine.input.MouseManager.MouseMoveEvent;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UiHelper;

public class TradingUI implements Widget, Disposable {

    public enum State {
        HIDDEN,
        CHOOSING_DIALOGUE,
        TRADE_READY,
        TRADE_RESULT,
    }

    public interface TradingUIListener {
        void onDialogueSelected(int optionIndex);
        void onNpcItemChanged(int newIndex);
        void onTradeConfirmed(int itemIndex);
        void onTradeCancelled();
        void onTimeUp();
    }

    private static final int CHOICE_COUNT = 3;

    // TODO: make skin class and skin factory
    // --- Colors for Ancient France Maritime Vibe ---
    private static final Color TEXT_BACKGROUND = new Color(0, 0, 0, 0.6f);
    private static final Color goldTrim = new Color(0.83f, 0.68f, 0.21f, 1f);
    private static final Color navyPanel = new Color(
        0.10f,
        0.15f,
        0.23f,
        0.92f
    );
    private static final Color parchmentText = new Color(
        0.96f,
        0.87f,
        0.70f,
        1f
    );
    private static final Color highlightGold = new Color(
        0.95f,
        0.81f,
        0.24f,
        1f
    ); // for active
    private static final Color crimsonCancel = new Color(0.6f, 0.15f, 0.2f, 1f);
    private static final Color cancelHover = new Color(0.7f, 0.2f, 0.25f, 1f);
    private static final Color seaGreenConfirm = new Color(
        0.17f,
        0.5f,
        0.33f,
        1f
    );
    private static final Color confirmHover = new Color(0.2f, 0.6f, 0.4f, 1f);
    private static final Color BUTTON_HOVER_COLOR = new Color(
        0.16f,
        0.22f,
        0.32f,
        1f
    );

    final float SIDE_MARGIN = 30;
    final float TOP_MARGIN = 70;
    final float FONT_SIZE = 12;

    final float ITEM_BOX_WIDTH = 160;
    final float ITEM_BOX_HEIGHT = 160;
    final float RARITY_BOX_HEIGHT = 40;
    final float LEFT_BOX_GAP = 10;

    final float CHOICE_SPACE = 15;
    final float CHOICE_TOTAL_HEIGHT =
        ITEM_BOX_HEIGHT + LEFT_BOX_GAP + RARITY_BOX_HEIGHT;
    final float CHOICE_WIDTH = 220;
    final float CHOICE_HEIGHT =
        (CHOICE_TOTAL_HEIGHT - (CHOICE_COUNT - 1) * CHOICE_SPACE) /
        CHOICE_COUNT;

    final float CONFIRM_CANCEL_WIDTH = 120;
    final float CONFIRM_CANCEL_HEIGHT = 45;
    final float CONFIRM_CANCEL_GAP = 30;
    final float DIALOGUE_MARGIN = 8;
    final float DIALOGUE_HEIGHT = 120;

    private State state;
    private TradingUIListener listener = null;
    private MouseManager mouse;

    private Viewport viewport;
    private BitmapFont font;
    private UIRoot uiRoot;

    // TODO: encapsulate timer
    private float timeLeft = 0;
    private String[] offeredItemNames = new String[0];
    private String[] offeredItemRarities = new String[0];
    private int offeredItemIndex = 0;
    private int selectedDialogueIndex = -1;

    private Text timerText;
    private Text offeredItemText;
    private Text rarityText;
    private Text choiceTexts[] = new Text[CHOICE_COUNT];
    private Text nameText;
    private Text dialogueText;
    private Box confirmButton;
    private Box cancelButton;

    public TradingUI() {
        this.state = State.HIDDEN;
        this.mouse = new MouseManager();
        this.viewport = new ExtendViewport(720, 480);

        buildUI();
    }

    private void buildUI() {
        this.font = new BitmapFont(Gdx.files.internal("fonts/jp.fnt"));
        uiRoot = new UIRoot();
        UiHelper.setupUiMouseHandlers(mouse, viewport, uiRoot);

        // --- Top ---
        // Title
        uiRoot.addChild(
            new Text(
                "Trade Offer",
                font,
                Text.Alignment.CENTER,
                goldTrim,
                new UIRelativeLayout.Builder()
                    .padTop(60)
                    .height(FONT_SIZE)
                    .getLayout()
            )
        );
        // Timer
        timerText = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            parchmentText,
            new UIRelativeLayout.Builder()
                .padTop(90)
                .height(FONT_SIZE)
                .getLayout()
        );
        uiRoot.addChild(timerText);

        // --- Left Column ---
        uiRoot.addChild(
            new Box(
                0,
                null,
                TEXT_BACKGROUND,
                new UIRelativeLayout.Builder()
                    .padLeft(SIDE_MARGIN - 5)
                    .padTop(TOP_MARGIN - 35)
                    .width(ITEM_BOX_WIDTH + 10)
                    .height(FONT_SIZE + 10)
                    .getLayout()
            ).addChild(
                new Text(
                    "Merchant Receives",
                    font,
                    Text.Alignment.CENTER,
                    parchmentText,
                    new UIRelativeLayout.Builder()
                        .yAlignment(Alignment.CENTER)
                        .height(FONT_SIZE)
                        .getLayout()
                )
            )
        );

        // Item box
        offeredItemText = new Text(
            // TODO: replace with item image
            "",
            font,
            Text.Alignment.CENTER,
            parchmentText,
            new UIRelativeLayout.Builder()
                .yAlignment(Alignment.CENTER)
                .height(FONT_SIZE)
                .getLayout()
        );
        uiRoot.addChild(
            new Box(
                2,
                goldTrim,
                navyPanel,
                new UIRelativeLayout.Builder()
                    .padLeft(SIDE_MARGIN)
                    .padTop(TOP_MARGIN)
                    .width(ITEM_BOX_WIDTH)
                    .height(ITEM_BOX_HEIGHT)
                    .getLayout()
            )
                .addChild(offeredItemText)
                .addChild(makeItemArrowButton(true))
                .addChild(makeItemArrowButton(false))
        );

        // Rarity box
        rarityText = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            parchmentText,
            new UIRelativeLayout.Builder()
                .yAlignment(Alignment.CENTER)
                .height(FONT_SIZE)
                .getLayout()
        );
        uiRoot.addChild(
            new Box(
                2,
                goldTrim,
                navyPanel,
                new UIRelativeLayout.Builder()
                    .padLeft(SIDE_MARGIN)
                    .padTop(TOP_MARGIN + ITEM_BOX_HEIGHT + LEFT_BOX_GAP)
                    .width(ITEM_BOX_WIDTH)
                    .height(RARITY_BOX_HEIGHT)
                    .getLayout()
            ).addChild(rarityText)
        );

        // --- Right Column ---
        uiRoot.addChild(
            new Box(
                0,
                null,
                TEXT_BACKGROUND,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.END)
                    .padRight(SIDE_MARGIN + (CHOICE_WIDTH - 110) / 2)
                    .padTop(TOP_MARGIN - 35)
                    .width(110)
                    .height(FONT_SIZE + 10)
                    .getLayout()
            ).addChild(
                new Text(
                    "You Receive",
                    font,
                    Text.Alignment.CENTER,
                    parchmentText,
                    new UIRelativeLayout.Builder()
                        .yAlignment(Alignment.CENTER)
                        .height(FONT_SIZE)
                        .getLayout()
                )
            )
        );

        // Choice buttons
        for (int i = 0; i < choiceTexts.length; i++) {
            Box button = makeDialogueOptionButton(
                TOP_MARGIN + i * (CHOICE_HEIGHT + CHOICE_SPACE),
                i
            );
            choiceTexts[i] = new Text(
                "",
                font,
                Text.Alignment.CENTER,
                parchmentText,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.CENTER)
                    .height(FONT_SIZE)
                    .getLayout()
            );
            button.addChild(choiceTexts[i]);
            uiRoot.addChild(button);
        }

        // --- Bottom Area ---
        // Confirm/cancel buttons
        confirmButton = UiHelper.createButton(
            new Box(
                2,
                goldTrim,
                seaGreenConfirm,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.END)
                    .padBottom(SIDE_MARGIN + DIALOGUE_HEIGHT + 24)
                    .padRight(CONFIRM_CANCEL_WIDTH + CONFIRM_CANCEL_GAP / 2)
                    .width(CONFIRM_CANCEL_WIDTH)
                    .height(CONFIRM_CANCEL_HEIGHT)
                    .getLayout()
            ),
            confirmHover,
            new Text(
                "Confirm",
                font,
                Text.Alignment.CENTER,
                parchmentText,
                new UIRelativeLayout()
            ),
            FONT_SIZE,
            e -> {
                if (!isInteractive()) return false;
                if (listener != null) listener.onTradeConfirmed(
                    offeredItemIndex
                );
                return true;
            }
        );
        confirmButton.setVisible(false);
        uiRoot.addChild(confirmButton);

        cancelButton = UiHelper.createButton(
            new Box(
                2,
                goldTrim,
                crimsonCancel,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.END)
                    .padBottom(SIDE_MARGIN + DIALOGUE_HEIGHT + 24)
                    .padLeft(CONFIRM_CANCEL_WIDTH + CONFIRM_CANCEL_GAP / 2)
                    .width(CONFIRM_CANCEL_WIDTH)
                    .height(CONFIRM_CANCEL_HEIGHT)
                    .getLayout()
            ),
            cancelHover,
            new Text(
                "Cancel",
                font,
                Text.Alignment.CENTER,
                parchmentText,
                new UIRelativeLayout()
            ),
            FONT_SIZE,
            e -> {
                if (!isInteractive()) return false;
                if (listener != null) listener.onTradeCancelled();
                hide();
                return true;
            }
        );
        cancelButton.setVisible(false);
        uiRoot.addChild(cancelButton);

        // Dialogue box
        nameText = new Text(
            "",
            font,
            Text.Alignment.START,
            goldTrim,
            new UIRelativeLayout.Builder()
                .padLeft(DIALOGUE_MARGIN)
                .padTop(DIALOGUE_MARGIN)
                .height(FONT_SIZE)
                .getLayout()
        );
        dialogueText = new Text(
            "",
            font,
            Text.Alignment.START,
            parchmentText,
            new UIRelativeLayout.Builder()
                .padLeft(DIALOGUE_MARGIN)
                .padTop(DIALOGUE_MARGIN + FONT_SIZE + 12)
                .height(FONT_SIZE)
                .getLayout()
        );
        uiRoot.addChild(
            new Box(
                2,
                goldTrim,
                navyPanel,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.END)
                    .padLeft(SIDE_MARGIN)
                    .padRight(SIDE_MARGIN)
                    .padBottom(SIDE_MARGIN)
                    .height(DIALOGUE_HEIGHT)
                    .getLayout()
            )
                .addChild(nameText)
                .addChild(dialogueText)
        );
    }

    private Box makeItemArrowButton(boolean isLeft) {
        final float WIDTH = 30;
        final float HEIGHT = 40;
        final float X_OFFSET = 5;
        final int step = isLeft ? -1 : 1;

        UIRelativeLayout.Builder layout = new UIRelativeLayout.Builder()
            .xAlignment(isLeft ? Alignment.START : Alignment.END)
            .yAlignment(Alignment.CENTER)
            .width(WIDTH)
            .height(HEIGHT);
        if (isLeft) {
            layout.padLeft(-WIDTH + X_OFFSET);
        } else {
            layout.padRight(-WIDTH + X_OFFSET);
        }

        Box button = UiHelper.createButton(
            new Box(2, goldTrim, navyPanel, layout.getLayout()),
            BUTTON_HOVER_COLOR,
            new Text(
                isLeft ? "<" : ">",
                font,
                Text.Alignment.CENTER,
                parchmentText,
                new UIRelativeLayout()
            ),
            12,
            e -> {
                if (!isInteractive()) return false;
                setOfferedItemIndex(offeredItemIndex + step);
                return true;
            }
        );

        return button;
    }

    private Box makeDialogueOptionButton(float y, int index) {
        Box button = new Box(
            2,
            goldTrim,
            navyPanel,
            new UIRelativeLayout.Builder()
                .xAlignment(Alignment.END)
                .padRight(SIDE_MARGIN)
                .padTop(y)
                .width(CHOICE_WIDTH)
                .height(CHOICE_HEIGHT)
                .getLayout()
        );
        button
            .addListener(MouseButtonEvent.class, e -> {
                if (
                    !isInteractive() ||
                    e.button != MouseButton.LEFT.code() ||
                    e.type != ButtonBindType.DOWN ||
                    !button.getBounds().contains(e.x, e.y)
                ) return false;
                if (
                    state != State.TRADE_READY || selectedDialogueIndex != index
                ) {
                    state = State.TRADE_READY;
                    selectedDialogueIndex = index;
                    button.setFillColor(highlightGold);
                    if (listener != null) listener.onDialogueSelected(index);
                }
                confirmButton.setVisible(true);
                cancelButton.setVisible(true);
                return true;
            })
            .addListener(MouseMoveEvent.class, e -> {
                if (!isInteractive()) return false;
                if (
                    state == State.TRADE_READY && selectedDialogueIndex == index
                ) {
                    button.setFillColor(highlightGold);
                } else {
                    button.setFillColor(
                        button.getBounds().contains(e.x, e.y)
                            ? BUTTON_HOVER_COLOR
                            : navyPanel
                    );
                }
                return false;
            });
        return button;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return mouse;
    }

    public void setListener(TradingUIListener listener) {
        this.listener = listener;
    }

    public void show(
        String npcName,
        String[] options,
        String[] itemNames,
        String[] itemRarities
    ) {
        state = State.CHOOSING_DIALOGUE;
        timeLeft = 90f; // 1 min 30 sec
        updateTimer(0);
        offeredItemNames = itemNames.clone();
        offeredItemRarities = itemRarities.clone();
        offeredItemIndex = 0;
        selectedDialogueIndex = -1;

        for (int i = 0; i < choiceTexts.length && i < options.length; i++) {
            choiceTexts[i].setText(options[i]);
        }
        nameText.setText(npcName);

        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        setOfferedItemIndex(offeredItemIndex);
    }

    private void setOfferedItemIndex(int i) {
        if (i < 0) {
            i = (i % offeredItemNames.length) + offeredItemNames.length;
        }
        offeredItemIndex = i % offeredItemNames.length;

        offeredItemText.setText(
            "Picture of:\n" + offeredItemNames[offeredItemIndex]
        );

        String currentRarity =
            offeredItemRarities[offeredItemIndex].toLowerCase();
        Color rarityColor = Color.WHITE;
        if (currentRarity.contains("common")) rarityColor = Color.LIGHT_GRAY;
        else if (currentRarity.contains("rare")) rarityColor = Color.ROYAL;
        else if (currentRarity.contains("epic")) rarityColor = Color.PURPLE;
        else if (currentRarity.contains("legendary")) rarityColor = Color.GOLD;
        rarityText.setColor(rarityColor);
        rarityText.setText("Rarity: " + offeredItemRarities[offeredItemIndex]);

        if (listener != null) listener.onNpcItemChanged(offeredItemIndex);
    }

    private void updateTimer(float deltaTime) {
        timeLeft -= deltaTime;
        if (timeLeft < 0) timeLeft = 0;
        int m = (int) (timeLeft / 60);
        int s = (int) (timeLeft % 60);
        timerText.setText(String.format("%d:%02d", m, s));
    }

    public void setInnerThoughts(String text) {
        dialogueText.setText(text);
    }

    public void showTradeResult(String resultText) {
        state = State.TRADE_RESULT;
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        dialogueText.setText(resultText);
    }

    public void hide() {
        state = State.HIDDEN;
    }

    public boolean isVisible() {
        return state != State.HIDDEN;
    }

    public boolean isInteractive() {
        return state == State.CHOOSING_DIALOGUE || state == State.TRADE_READY;
    }

    @Override
    public void update(float deltaTime) {
        if (state == State.HIDDEN) return;

        mouse.update(deltaTime, Float.NaN);
        uiRoot.update(deltaTime);
        if (isInteractive()) {
            updateTimer(deltaTime);
            if (timeLeft <= 0) {
                state = State.TRADE_RESULT;
                if (listener != null) listener.onTimeUp();
            }
        }
    }

    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        if (!isVisible()) return;

        viewport.update(width, height, true);
        viewport.setScreenPosition(x, y);
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
