package presentation_lanterna;

import com.googlecode.lanterna.screen.Screen;
import domain.service.GameSession;

public class TurnRenderer {

    private final Screen screen;
//        private final MapRenderer mapRenderer;
    private final FogRenderer fogRenderer;
    private final ActorsRenderer actorRenderer;
//    private final BackpackRenderer backpackRenderer;

    public TurnRenderer(Screen screen,  int TERMINAL_WIDTH, int TERMINAL_HEIGHT) {
        this.screen = screen;
        var graphics = screen.newTextGraphics();
        fogRenderer = new FogRenderer(graphics);
//        this.mapRenderer = new MapRenderer(graphics);
        this.actorRenderer = new ActorsRenderer(graphics,TERMINAL_WIDTH, TERMINAL_HEIGHT);
//        this.backpackRenderer = new BackpackRenderer(graphics);
    }

    public void render(GameSession session, int TERMINAl_WIDTH, int TERMINAL_HEIGHT) throws Exception {

//        mapRenderer.render(session.getCurrentLevel());   // 1. тайлы
        fogRenderer.render(session,  TERMINAl_WIDTH, TERMINAL_HEIGHT);
        actorRenderer.render(session);// акторы
//        TextGraphics textGraphics = screen.newTextGraphics();
//        textGraphics.putString((int)(2*TERMINAl_WIDTH/2.5),3*TERMINAl_WIDTH/4,"Level" + session.getCurrentLevelNumber() +"    Hits: " + "?" + "    Gold" + session.getPlayer().getBackpack().getTotalTreasureValue());


//        screen.refresh();                                // 3. flush в терминал
    }
}
