package interfaces;

/**
 * Created by Paul on 18.01.2016.
 */
public interface Gui {
    void showTruppSelection(String title, int min, int max);
    void showEndScreen(boolean Player1Win);
    void setOpponent(NPC opponent);
}
