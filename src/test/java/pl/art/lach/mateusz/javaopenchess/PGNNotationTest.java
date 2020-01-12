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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.GameBuilder;
import pl.art.lach.mateusz.javaopenchess.core.Squares;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.implementations.PGNNotationDataProcessor;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.tokenizers.PGNTokens;
import pl.art.lach.mateusz.javaopenchess.core.data_transfer.tokenizers.Token;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.DataSyntaxException;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.GameReadException;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Bishop;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Knight;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Queen;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/**
 *
 * @author Mateusz Slawomir Lach (matlak, msl)
 */
public class PGNNotationTest {

    private Game game;

    private Chessboard chessboard;

    private String pgnSimple;

    private String pgnComplex;

    private PGNNotationDataProcessor dataProcessor = new PGNNotationDataProcessor();

    @Before
    public void setupOnce() throws IOException {
        game = new GameBuilder()
                .setBlackPlayerName("")
                .setWhitePlayerName("")
                .setWhitePlayerType(PlayerType.LOCAL_USER)
                .setBlackPlayerType(PlayerType.LOCAL_USER)
                .setGameMode(GameModes.NEW_GAME)
                .setGameType(GameTypes.LOCAL)
                .setPiecesForNewGame(true)
                // .setCreateUi(false)
                .build();

        chessboard = game.getChessboard();
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test1.pgn");
        pgnSimple = IOUtils.toString(is, "UTF-8");
        is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test2.pgn");
        pgnComplex = IOUtils.toString(is, "UTF-8");
    }

    @Test
    public void checkSyntaxValidation() {
        assertNotNull(pgnSimple);
        assertNotNull(dataProcessor);
        assertFalse("".equals(pgnSimple));
        assertTrue(0 == game.getMoves().getMoves().size());

        List<Token> tokens = dataProcessor.parse(pgnSimple);
        
        assertNotNull(tokens);
        assertTrue(tokens.size() >= 4);
        
        assertEquals(PGNTokens.EVENT.ordinal(), tokens.get(0).getTokenNumber());
        assertEquals(PGNTokens.DATE.ordinal(), tokens.get(1).getTokenNumber());
        assertEquals(PGNTokens.WHITE_PLAYER.ordinal(), tokens.get(2).getTokenNumber());
        assertEquals(PGNTokens.BLACK_PLAYER.ordinal(), tokens.get(3).getTokenNumber());
        
    }
    
    @Test(expected = DataSyntaxException.class)
    public void checkInvalidSyntaxInHeadline() throws IOException {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-invalid-syntax-in-headline.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        
        dataProcessor.parse(pgn);
    }

    @Test
    public void checkBasicPNGExport() {
        String pgn = "1. Ng1-f3 Ng8-f6 2. g2-g3 g7-g6 3. Nb1-c3 Bf8-g7";

        chessboard.move(chessboard.getSquare(Squares.SQ_G, Squares.SQ_1),
                chessboard.getSquare(Squares.SQ_F, Squares.SQ_3));
        chessboard.move(chessboard.getSquare(Squares.SQ_G, Squares.SQ_8),
                chessboard.getSquare(Squares.SQ_F, Squares.SQ_6));
        chessboard.move(chessboard.getSquare(Squares.SQ_G, Squares.SQ_2),
                chessboard.getSquare(Squares.SQ_G, Squares.SQ_3));
        chessboard.move(chessboard.getSquare(Squares.SQ_G, Squares.SQ_7),
                chessboard.getSquare(Squares.SQ_G, Squares.SQ_6));
        chessboard.move(chessboard.getSquare(Squares.SQ_B, Squares.SQ_1),
                chessboard.getSquare(Squares.SQ_C, Squares.SQ_3));
        chessboard.move(chessboard.getSquare(Squares.SQ_F, Squares.SQ_8),
                chessboard.getSquare(Squares.SQ_G, Squares.SQ_7));
        assertEquals(pgn, getLastLine(game.exportGame(dataProcessor)));
    }

    @Test
    public void checkBasicPNGImport() throws GameReadException {
        assertNotNull(pgnSimple);
        assertNotNull(dataProcessor);
        assertFalse("".equals(pgnSimple));
        assertTrue(0 == game.getMoves().getMoves().size());
        assertEquals(6, pgnSimple.split("\n").length);

        game.importGame(pgnSimple, dataProcessor);

        Piece piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_7).getPiece();
        assertTrue(piece.getClass() == Bishop.class && piece.getPlayer().getColor() == Colors.BLACK);

        piece = chessboard.getSquare(Squares.SQ_C, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.WHITE);

        piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Pawn.class && piece.getPlayer().getColor() == Colors.BLACK);

        piece = chessboard.getSquare(Squares.SQ_G, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Pawn.class && piece.getPlayer().getColor() == Colors.WHITE);

        piece = chessboard.getSquare(Squares.SQ_F, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.BLACK);

        piece = chessboard.getSquare(Squares.SQ_F, Squares.SQ_3).getPiece();
        assertTrue(piece.getClass() == Knight.class && piece.getPlayer().getColor() == Colors.WHITE);
    }

    @Test
    public void importAndExport() throws GameReadException {
        game.importGame(pgnSimple, dataProcessor);
        String export = game.exportGame(dataProcessor);
        assertEquals(getLastLine(pgnSimple), getLastLine(export));
    }

    @Test
    public void importAndExportComplexPGN() throws GameReadException {
        game.importGame(pgnComplex, dataProcessor);

        Piece piece = chessboard.getSquare(Squares.SQ_A, Squares.SQ_8).getPiece();
        assertTrue(piece.getClass() == King.class && piece.getPlayer().getColor() == Colors.BLACK);

        assertTrue(((King) piece).isChecked());

        piece = chessboard.getSquare(Squares.SQ_A, Squares.SQ_6).getPiece();
        assertTrue(piece.getClass() == Rook.class && piece.getPlayer().getColor() == Colors.WHITE);

        piece = chessboard.getSquare(Squares.SQ_B, Squares.SQ_1).getPiece();
        assertTrue(piece.getClass() == Queen.class && piece.getPlayer().getColor() == Colors.WHITE);

        piece = chessboard.getSquare(Squares.SQ_H, Squares.SQ_1).getPiece();
        assertTrue(piece.getClass() == King.class && piece.getPlayer().getColor() == Colors.WHITE);

        assertFalse(((King) piece).isChecked());

        assertEquals(getLastLine(pgnComplex), getLastLine(game.exportGame(dataProcessor)));
    }

    @Test(expected = GameReadException.class)
    public void testInvalidSyntaxFileEmptyMoveList() throws IOException, GameReadException {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-is1.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataProcessor);
    }

    @Test(expected = GameReadException.class)
    public void testInvalidSyntaxFileWrongOperator() throws IOException, GameReadException {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-is2.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataProcessor);
    }

    @Test(expected = GameReadException.class)
    public void testIllegalMoveCorrectSyntax() throws IOException, GameReadException {
        InputStream is = PGNNotationTest.class.getResourceAsStream("resources/joChess-test-im1.pgn");
        String pgn = IOUtils.toString(is, "UTF-8");
        game.importGame(pgn, dataProcessor);
    }

    private String getLastLine(String str) {
        if (null == str) {
            return null;
        }
        return str.substring(str.lastIndexOf("1. "), str.length() - 1).trim();
    }

}
