/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.art.lach.mateusz.javaopenchess.core;

import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import java.awt.*;
import java.awt.image.*;
import javax.swing.JPanel;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import org.apache.log4j.Logger;

/**
 * Class to representing the full game time
 * 
 * @author: Mateusz Slawomir Lach ( matlak, msl )
 * @author: Damian Marciniak
 */
public class GameClock extends JPanel implements Runnable {
    
    private static final int CLOCK_SIZE = 600;

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(GameClock.class);

    private PlayerClock clockWhitePlayer;

    private PlayerClock clockBlackPlayer;

    private PlayerClock activeClock;

    private Settings settings;

    private Thread thread;

    private Game game;

    private String whiteClockString;

    private String blackClockString;

    private BufferedImage background;

    GameClock(Game game) {
        super();
        this.game = game;
        clockWhitePlayer = PlayerClock.getZeroTimeInstance();
        clockBlackPlayer = PlayerClock.getZeroTimeInstance();
        activeClock = clockWhitePlayer;
        settings = game.getSettings();
        background = new BufferedImage(CLOCK_SIZE, CLOCK_SIZE, BufferedImage.TYPE_INT_ARGB);

        int time = settings.getTimeForGame();

        setTimes(time, time);
        setPlayers(settings.getPlayerBlack(), settings.getPlayerWhite());

        thread = new Thread(this);
        if (settings.isTimeLimitSet()) {
            thread.start();
        }
        drawBackground();
        setDoubleBuffered(true);
    }

    /**
     * Method to init game clock
     */
    public void start() {
        thread.start();
    }

    /**
     * Method to stop game clock
     */
    public void stop() {
        activeClock = null;

        try {
            // block this thread
            this.thread.wait();
        } catch (InterruptedException | IllegalMonitorStateException exc) {
            LOG.error("Error blocking thread: ", exc);
        }
    }

    /**
     * Method of drawing graphical background of clock
     */
    void drawBackground() {
        Graphics gr = this.background.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.ITALIC, 20);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(5, 30, 80, 30);
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(85, 30, 90, 30);
        g2d.drawRect(5, 30, 170, 30);
        g2d.drawRect(5, 60, 170, 30);
        g2d.drawLine(85, 30, 85, 90);
        font = new Font("Serif", Font.ITALIC, 16);
        g2d.drawString(settings.getPlayerWhite().getName(), 10, 50);
        g2d.setColor(Color.WHITE);
        g2d.drawString(settings.getPlayerBlack().getName(), 100, 50);
    }

    /**
     * Annotation to superclass Graphics drawing the clock graphics
     * 
     * @param g Graphics2D Capt object to paint
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        whiteClockString = clockWhitePlayer.getAsString();
        blackClockString = clockBlackPlayer.getAsString();
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(background, 0, 0, this);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.ITALIC, 20);
        g2d.drawImage(background, 0, 0, this);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(5, 30, 80, 30);
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(85, 30, 90, 30);
        g2d.drawRect(5, 30, 170, 30);
        g2d.drawRect(5, 60, 170, 30);
        g2d.drawLine(85, 30, 85, 90);
        font = new Font("Serif", Font.ITALIC, 14);
        g2d.drawImage(background, 0, 0, this);
        g2d.setFont(font);
        g.drawString(settings.getPlayerWhite().getName(), 10, 50);
        g.setColor(Color.WHITE);
        g.drawString(settings.getPlayerBlack().getName(), 100, 50);
        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g2d.drawString(whiteClockString, 10, 80);
        g2d.drawString(blackClockString, 90, 80);
    }

    /**
     * Annotation to superclass Graphics updateing clock graphisc
     * 
     * @param g Graphics2D Capt object to paint
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Method of swiching the players clocks
     */
    public void switchActiveClock() {
        if (activeClock == clockWhitePlayer) {
            activeClock = clockBlackPlayer;
        } else {
            activeClock = clockWhitePlayer;
        }
    }

    /**
     * Method with is setting the players clocks time
     * 
     * @param time1 Capt the player time
     * @param time2 Capt the player time
     */
    public void setTimes(int time1, int time2) {
        clockWhitePlayer = PlayerClock.getInstanceWithTimeLeft(time1);
        clockBlackPlayer = PlayerClock.getInstanceWithTimeLeft(time2);
    }

    /**
     * Method with is setting the players clocks
     * 
     * @param player1 Capt player information
     * @param player2 Capt player information
     */
    private void setPlayers(Player player1, Player player2) {
        if (player1.getColor() == Colors.WHITE) {
            clockWhitePlayer.setPlayer(player1);
            clockBlackPlayer.setPlayer(player2);
        } else {
            clockWhitePlayer.setPlayer(player2);
            clockBlackPlayer.setPlayer(player1);
        }
    }

    /**
     * Method with is running the time on clock
     */
    @Override
    public void run() {
        while (true) {
            if (activeClock != null) {
                if (activeClock.decrementOneSecond()) {
                    repaint();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOG.error("Some error in gameClock thread: " + e);
                    }
                }
                if (isTimeOver()) {
                    timeOver();
                }
            }
        }
    }

    private boolean isTimeOver() {
        return activeClock != null 
                && activeClock.getLeftTime() <= 0;
    }

    /**
     * Method of checking is the time of the game is not over
     */
    private void timeOver() {
        String color = new String();
        if (clockWhitePlayer.getLeftTime() == 0) {
            color = clockBlackPlayer.getPlayer().getColor().toString();
        } else if (clockBlackPlayer.getLeftTime() == 0) {
            color = clockWhitePlayer.getPlayer().getColor().toString();
        } else {
            LOG.debug("Time over called when player got time 2 play");
        }
        game.endGame("Time is over! " + color + " player win the game.");
        stop();
    }
}