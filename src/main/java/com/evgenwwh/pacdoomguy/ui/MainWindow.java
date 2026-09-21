package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.model.Level;
import com.evgenwwh.pacdoomguy.stats.RecordsRepository;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** The single application window; switches between screens with a {@link CardLayout}. */
public final class MainWindow extends JFrame {
    private static final String MENU = "menu";
    private static final String LEVELS = "levels";
    private static final String RECORDS = "records";
    private static final String GAME = "game";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final MenuScreen menu;
    private final LevelSelectScreen levels;
    private final RecordsScreen records;
    private final GameScreen game;

    public MainWindow() {
        super("Pac-Doom Guy");
        RecordsRepository repository = RecordsRepository.defaultRepository();
        menu = new MenuScreen(this);
        levels = new LevelSelectScreen(this);
        records = new RecordsScreen(this, repository);
        game = new GameScreen(this, repository);

        root.add(menu, MENU);
        root.add(levels, LEVELS);
        root.add(records, RECORDS);
        root.add(game, GAME);
        setContentPane(root);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                game.pauseIfRunning();
            }
        });
        showMenu();
    }

    public void showMenu() {
        game.stop();
        show(MENU, menu);
    }

    public void showLevelSelect() {
        show(LEVELS, levels);
    }

    public void showRecords() {
        show(RECORDS, records);
    }

    public void startGame(Level level) {
        show(GAME, game);
        game.start(level);
    }

    private void show(String card, Screen screen) {
        cards.show(root, card);
        screen.onShown();
    }
}
