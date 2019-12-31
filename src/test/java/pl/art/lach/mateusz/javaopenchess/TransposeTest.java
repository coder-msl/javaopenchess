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
package pl.art.lach.mateusz.javaopenchess;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.Squares;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.createMock;

/**
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
public class TransposeTest {

    private ChessboardView view;

    @Before
    public void setupOnce() {
        Game gameMock = createMock(Game.class);
        Settings settingsMock = createNiceMock(Settings.class);
        Chessboard chessboardMock = createNiceMock(Chessboard.class);

        expect(gameMock.getChessboard()).andReturn(chessboardMock).anyTimes();
        expect(gameMock.getSettings()).andReturn(settingsMock).anyTimes();
        expect(settingsMock.isRenderLabels()).andReturn(true).anyTimes();

        replay(gameMock);
        replay(settingsMock);
        replay(chessboardMock);

        view = new Chessboard2D(gameMock);

    }

    @Test
    public void transposeUpToHalfOfChessboard() {
        assertEquals(Squares.SQ_H.getValue(), view.transposePosition(Squares.SQ_A.getValue()));
        assertEquals(Squares.SQ_G.getValue(), view.transposePosition(Squares.SQ_B.getValue()));
        assertEquals(Squares.SQ_F.getValue(), view.transposePosition(Squares.SQ_C.getValue()));
        assertEquals(Squares.SQ_E.getValue(), view.transposePosition(Squares.SQ_D.getValue()));
    }

    @Test
    public void transposeUpFromHalfOfChessboard() {
        assertEquals(Squares.SQ_D.getValue(), view.transposePosition(Squares.SQ_E.getValue()));
        assertEquals(Squares.SQ_C.getValue(), view.transposePosition(Squares.SQ_F.getValue()));
        assertEquals(Squares.SQ_B.getValue(), view.transposePosition(Squares.SQ_G.getValue()));
        assertEquals(Squares.SQ_A.getValue(), view.transposePosition(Squares.SQ_H.getValue()));
    }
}
