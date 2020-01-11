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
package pl.art.lach.mateusz.javaopenchess.core.moves;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.exceptions.ReadGameError;
import pl.art.lach.mateusz.javaopenchess.core.pieces.KingState;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Pawn;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;

/**
 * Class representing the players moves, it's also checking that the moves taken
 * by player are correct. All moves which was taken by current player are saving
 * as List of Strings The history of moves is printing in a table
 * 
 * @author Mateusz Slawomir Lach (matlak, msl)
 * @author Damian Marciniak
 */
//TODO: REFACTOR HERE!!
public class MovesHistory extends AbstractTableModel {

    private static final String DOT = ".";

    private static final String SPACE = " ";

    private static final int CHAR_8 = 56;

    private static final int CHAR_1 = 49;

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(MovesHistory.class);

    private static final int CHAR_TINY_X_ASCII = 120;

    private static final int CHAR_HYPHEN_ASCII = 45;

    private static final int CHAR_R_ASCII = 82;

    private static final int CHAR_Q_ASCII = 81;

    private static final int CHAR_N_ASCII = 78;

    private static final int CHAR_K_ASCII = 75;

    private static final int CHAR_B_ASCII = 66;

    private static final int CHAR_TINY_H_ASCII = 104;

    public static final String SYMBOL_CHECK = "+";

    public static final String SYMBOL_CHECK_MATE = "#";

    private static final int CHAR_TINY_A_ASCII = 97;

    public static final String SYMBOL_NORMAL_MOVE = "-";

    public static final String SYMBOL_PIECE_TAKEN = "x";

    public static final String SYMBOL_EN_PASSANT = "(e.p)";

    private ArrayList<String> moves = new ArrayList<>();

    private int columnsNum = 3;

    private int rowsNum = 0;

    private String[] names = new String[] { Settings.lang("white"), Settings.lang("black") };

    private NotEditableTableModel tableModel;

    private JScrollPane scrollPane;

    private JTable table;

    private boolean enterBlack = false;

    private Game game;

    private Stack<Move> moveBackStack = new Stack<>();

    protected Stack<Move> moveForwardStack = new Stack<>();

    private int fiftyMoveRuleCounter = 0;

    public MovesHistory(Game game) {
        super();
        this.tableModel = new NotEditableTableModel();
        this.table = new JTable(this.tableModel);
        this.scrollPane = new JScrollPane(this.table);

        this.scrollPane.setMaximumSize(new Dimension(100, 100));
        this.table.setMinimumSize(new Dimension(100, 100));
        this.game = game;

        this.tableModel.addColumn(this.names[0]);
        this.tableModel.addColumn(this.names[1]);
        this.addTableModelListener(null);
        this.tableModel.addTableModelListener(null);
        this.scrollPane.setAutoscrolls(true);
    }

    public void draw() {
    }

    @Override
    public String getValueAt(int x, int y) {
        return this.moves.get((y * 2) - 1 + (x - 1));
    }

    @Override
    public int getRowCount() {
        return this.rowsNum;
    }

    @Override
    public int getColumnCount() {
        return this.columnsNum;
    }

    protected void addRow() {
        this.tableModel.addRow(new String[2]);
    }

    protected void addCastling(String move) {
        this.moves.remove(this.moves.size() - 1);// remove last element (moves of Rook)
        if (!this.enterBlack) {
            this.tableModel.setValueAt(move, this.tableModel.getRowCount() - 1, 1);// replace last value
        } else {
            this.tableModel.setValueAt(move, this.tableModel.getRowCount() - 1, 0);// replace last value
        }
        this.moves.add(move);// add new moves (O-O or O-O-O)
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }

    /**
     * Method of adding new moves to the table
     * 
     * @param str String which in is saved player moves
     */
    protected void addMove2Table(String str) {
        try {
            if (!this.enterBlack) {
                this.addRow();
                this.rowsNum = this.tableModel.getRowCount() - 1;
                this.tableModel.setValueAt(str, rowsNum, 0);
            } else {
                this.tableModel.setValueAt(str, rowsNum, 1);
                this.rowsNum = this.tableModel.getRowCount() - 1;
            }
            this.enterBlack = !this.enterBlack;
            this.table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));// scroll to down

        } catch (ArrayIndexOutOfBoundsException exc) {
            if (this.rowsNum > 0) {
                this.rowsNum--;
                addMove2Table(str);
            }
        }
    }

    /**
     * Method to add new moves
     * 
     * @param move String which in is capt player moves
     */
    public void addMove(String move) {
        if (isPgnStringMoveSyntaxValid(move)) {
            this.moves.add(move);
            this.addMove2Table(move);
            this.moveForwardStack.clear();
        }

    }

    public void addMove(Square begin, Square end, boolean registerInHistory, Castling castlingMove,
            boolean wasEnPassant, Piece promotedPiece) {
        String locMove = begin.getPiece().getSymbol();

        if (game.getSettings().isUpsideDown()) {
            locMove = addMoveHandleUpsideDown(locMove, begin);
        } else {
            locMove = addMoveHandleNormalSetup(locMove, begin);
        }

        if (end.piece != null) {
            locMove += SYMBOL_PIECE_TAKEN;
        } else {
            locMove += SYMBOL_NORMAL_MOVE;
        }

        if (game.getSettings().isUpsideDown()) {
            locMove = addMoveHandleUpsideDown(locMove, end);
        } else {
            locMove = addMoveHandleNormalSetup(locMove, end);
        }

        if (Pawn.class == begin.getPiece().getClass() && begin.getPozX() - end.getPozX() != 0 && end.piece == null) {
            locMove += SYMBOL_EN_PASSANT;// pawn take down opponent en passant
            wasEnPassant = true;
        }
        if (isBlackOrWhiteKingCheck()) {
            if (isBlackOrWhiteKingCheckmatedOrStalemated()) {
                locMove += SYMBOL_CHECK_MATE;
            } else {
                locMove += SYMBOL_CHECK;
            }
        }
        if (castlingMove != Castling.NONE) {
            this.addCastling(castlingMove.getSymbol());
        } else {
            this.moves.add(locMove);
            this.addMove2Table(locMove);
        }
        this.scrollPane.scrollRectToVisible(new Rectangle(0, this.scrollPane.getHeight() - 2, 1, 1));

        if (registerInHistory) {
            Move moveToAdd = new Move(new Square(begin), new Square(end), begin.piece, end.piece, castlingMove,
                    wasEnPassant, promotedPiece);
            this.getMoveBackStack().add(moveToAdd);
        }
    }

    private boolean isBlackOrWhiteKingCheckmatedOrStalemated() {
        return (!this.enterBlack && this.game.getChessboard().getKingBlack().getKingState() == KingState.CHECKMATED)
                || (this.enterBlack && this.game.getChessboard().getKingWhite().getKingState() == KingState.CHECKMATED);
    }

    private boolean isBlackOrWhiteKingCheck() {
        return (!this.enterBlack && this.game.getChessboard().getKingBlack().isChecked())
                || (this.enterBlack && this.game.getChessboard().getKingWhite().isChecked());
    }

    private String addMoveHandleNormalSetup(String locMove, Square begin) {
        locMove += Character.toString((char) (begin.getPozX() + CHAR_TINY_A_ASCII));// add letter of Square from which
                                                                                    // moves was made
        locMove += Integer.toString(Chessboard.NUMBER_OF_SQUARES - begin.getPozY());// add number of Square from which
                                                                                    // moves was made
        return locMove;
    }

    private String addMoveHandleUpsideDown(String locMove, Square begin) {
        locMove += Character.toString((char) ((Chessboard.BOTTOM - begin.getPozX()) + CHAR_TINY_A_ASCII));// add letter
                                                                                                          // of Square
                                                                                                          // from which
                                                                                                          // moves was
                                                                                                          // made
        locMove += Integer.toString(begin.getPozY() + 1);// add number of Square from which moves was made
        return locMove;
    }

    public void clearMoveForwardStack() {
        this.moveForwardStack.clear();
    }

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public ArrayList<String> getMoves() {
        return this.moves;
    }

    public synchronized Move getLastMoveFromHistory() {
        try {
            Move last = this.getMoveBackStack().get(this.getMoveBackStack().size() - 1);
            return last;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    public synchronized Move getNextMoveFromHistory() {
        try {
            Move next = this.moveForwardStack.get(this.moveForwardStack.size() - 1);
            return next;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: ", exc);
            return null;
        }

    }

    public synchronized Move undo() {
        try {
            Move last = this.getMoveBackStack().pop();
            if (last != null) {
                if (this.game.getSettings().getGameType() == GameTypes.LOCAL) // moveForward / redo available only for
                                                                              // LOCAL game
                {
                    this.moveForwardStack.push(last);
                }
                if (this.enterBlack) {
                    this.tableModel.setValueAt("", this.tableModel.getRowCount() - 1, 0);
                    this.tableModel.removeRow(this.tableModel.getRowCount() - 1);

                    if (this.rowsNum > 0) {
                        this.rowsNum--;
                    }
                } else {
                    if (this.tableModel.getRowCount() > 0) {
                        this.tableModel.setValueAt("", this.tableModel.getRowCount() - 1, 1);
                    }
                }
                this.moves.remove(this.moves.size() - 1);
                this.enterBlack = !this.enterBlack;
            }
            return last;
        } catch (EmptyStackException exc) {
            LOG.error("EmptyStackException: ", exc);
            this.enterBlack = false;
            return null;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: ", exc);
            return null;
        }
    }

    public synchronized Move redo() {
        try {
            if (this.game.getSettings().getGameType() == GameTypes.LOCAL) {
                Move first = this.moveForwardStack.pop();
                this.getMoveBackStack().push(first);

                return first;
            }
            return null;
        } catch (EmptyStackException exc) {
            LOG.error("redo: EmptyStackException: ", exc);
            return null;
        }
    }

    static public boolean isPgnStringMoveSyntaxValid(String move) {
        if (isCastling(move)) {
            return true;
        }
        try {
            int from = 0;
            int sign = move.charAt(from);// get First
            switch (sign) // if sign of piece, get next
            {
            case CHAR_B_ASCII:
            case CHAR_K_ASCII:
            case CHAR_N_ASCII:
            case CHAR_Q_ASCII:
            case CHAR_R_ASCII:
                from = 1;
                break;
            }
            sign = move.charAt(from);
            LOG.debug("isMoveCorrect/sign: " + sign);
            if (sign < CHAR_TINY_A_ASCII || sign > CHAR_TINY_H_ASCII) {
                return false;
            }
            sign = move.charAt(from + 1);
            if (sign < CHAR_1 || sign > CHAR_8) {
                return false;
            }
            if (move.length() > 3) // if is equal to 3 or lower, than it's in short notation, no more checking
                                   // needed
            {
                sign = move.charAt(from + 2);
                if (sign != CHAR_HYPHEN_ASCII && sign != CHAR_TINY_X_ASCII) // if isn't '-' and 'x'
                {
                    return false;
                }
                sign = move.charAt(from + 3);
                if (sign < CHAR_TINY_A_ASCII || sign > CHAR_TINY_H_ASCII) // if lower than 'a' or higher than 'h'
                {
                    return false;
                }
                sign = move.charAt(from + 4);
                if (sign < CHAR_1 || sign > CHAR_8) {
                    return false;
                }
            }
        } catch (StringIndexOutOfBoundsException exc) {
            LOG.error("isMoveCorrect/StringIndexOutOfBoundsException: ", exc);
            return false;
        }

        return true;
    }

    private static boolean isCastling(String move) {
        return move.equals(Castling.SHORT_CASTLING.getSymbol()) || move.equals(Castling.LONG_CASTLING.getSymbol());
    }

    public void addMoves(ArrayList<String> list) {
        for (String singleMove : list) {
            if (isPgnStringMoveSyntaxValid(singleMove)) {
                this.addMove(singleMove);
            }
        }
    }

    /**
     * Method of getting the moves in string
     * 
     * @return str String which in is capt player moves
     */
    public String getMovesInString() {
        int n = 1;
        int i = 0;
        String str = new String();
        for (String locMove : this.getMoves()) {
            if (i % 2 == 0) {
                str += n + ". ";
                n += 1;
            }
            str += locMove + SPACE;
            i += 1;
        }
        return str;
    }

    /**
     * Method to set all moves from String with validation test (usefoul for NETWORK
     * game)
     * 
     * @param moves String to set in String like PGN with full-notation format
     * @throws ReadGameError in case if something is wrong when reading PGN notation
     */
    public void setMoves(String moves) throws ReadGameError {
        int from = 0;
        int to = 0;
        int n = 1;
        String currentMove = "";
        List<String> tempArray = new ArrayList<>();
        int tempStrSize = moves.length() - 1;
        while (true) {
            int next = from + 1;
            from = moves.indexOf(SPACE, from);
            to = moves.indexOf(SPACE, next);
            if (from < 0 || to < 0) {
                break;
            }
            try {
                currentMove = moves.substring(next, to).trim();
                tempArray.add(currentMove);
                LOG.debug(String.format("Processed following move in PGN: %s", currentMove));
            } catch (StringIndexOutOfBoundsException exc) {
                LOG.error("setMoves/StringIndexOutOfBoundsException: error parsing file to load: ", exc);
                break;
            }
            if (n % 2 == 0) {
                from = moves.indexOf(DOT, to);
                if (from < to) {
                    break;
                }
            } else {
                from = to;
            }
            n += 1;
            if (from > tempStrSize || to > tempStrSize) {
                break;
            }
        }
        for (String locMove : tempArray) {
            if (!MovesHistory.isPgnStringMoveSyntaxValid(locMove.trim())) {
                String message = String.format(Settings.lang("invalid_file_to_load"), locMove);
                throw new ReadGameError(message, locMove);
            }
        }
        boolean canMove = false;
        for (String move : tempArray) {
            if (Castling.isCastling(move)) {
                canMove = processCastling(move);
                continue;
            }
            from = 0;
            int num = move.charAt(from);
            if (num <= 90 && num >= 65) {
                from = 1;
            }
            int xFrom = 9; // set to higher value than chessboard has fields, to cause error if piece won't
                           // be found
            int yFrom = 9;
            int xTo = 9;
            int yTo = 9;
            boolean pieceFound = false;
            if (move.length() <= 3) {
                Square[][] squares = game.getChessboard().getSquares();
                xTo = move.charAt(from) - CHAR_TINY_A_ASCII;
                yTo = Chessboard.BOTTOM - (move.charAt(from + 1) - 49);
                for (int i = 0; i < squares.length && !pieceFound; i++) {
                    for (int j = 0; j < squares[i].length && !pieceFound; j++) {
                        Piece piece = squares[i][j].piece;
                        Colors activePlayerColor = game.getActivePlayer().getColor();
                        if (isPieceNullOrDifferentColor(piece, activePlayerColor)) {
                            continue;
                        }
                        Set<Square> pieceMoves = squares[i][j].getPiece().getAllMoves();
                        for (Object square : pieceMoves) {
                            Square currSquare = (Square) square;
                            if (currSquare.getPozX() == xTo && currSquare.getPozY() == yTo) {
                                xFrom = squares[i][j].getPiece().getSquare().getPozX();
                                yFrom = squares[i][j].getPiece().getSquare().getPozY();
                                pieceFound = true;
                            }
                        }
                    }
                }
            } else {
                xFrom = move.charAt(from) - CHAR_TINY_A_ASCII;// from ASCII
                yFrom = Chessboard.BOTTOM - (move.charAt(from + 1) - 49);// from ASCII
                xTo = move.charAt(from + 3) - CHAR_TINY_A_ASCII;// from ASCII
                yTo = Chessboard.BOTTOM - (move.charAt(from + 4) - 49);// from ASCII
            }
            canMove = this.game.simulateMove(xFrom, yFrom, xTo, yTo, null);
            if (!canMove) {
                this.game.getChessboard().resetActiveSquare();
                throw new ReadGameError(String.format(Settings.lang("illegal_move_on"), move), move);
            }
        }
    }

    private boolean isPieceNullOrDifferentColor(Piece piece, Colors activePlayerColor) {
        return null == piece || activePlayerColor != piece.getPlayer().getColor();
    }

    //TODO: refactor this crap. It returns true if move is possible and trhows exception if not. 
    private boolean processCastling(String move) throws ReadGameError {
        boolean canMove;
        int[] values = new int[4];
        Colors color = game.getActivePlayer().getColor();
        if (Castling.LONG_CASTLING.getSymbol().equals(move)) {
            values = Castling.LONG_CASTLING.getMove(color);
        } else if (Castling.SHORT_CASTLING.getSymbol().equals(move)) {
            values = Castling.SHORT_CASTLING.getMove(color);
        }
        canMove = game.simulateMove(values[0], values[1], values[2], values[3], null);
        if (!canMove) {
            String message = String.format(Settings.lang("illegal_move_on"), move);
            throw new ReadGameError(message, move);
        }
        return canMove;
    }

    /**
     * @return the moveBackStack
     */
    public Stack<Move> getMoveBackStack() {
        return moveBackStack;
    }

    public void decrementFiftyMoveRule() {
        fiftyMoveRuleCounter--;
    }

    public void incrementFiftyMoveRule(Move move) {
        if (!(move.getMovedPiece() instanceof Pawn) && null == move.getTakenPiece()) {
            fiftyMoveRuleCounter++;
        }
    }

    /**
     * @return the fiftyMoveRuleCounter
     */
    public int getFiftyMoveRuleCounter() {
        return fiftyMoveRuleCounter;
    }

    /**
     * @param fiftyMoveRuleCounter the fiftyMoveRuleCounter to set
     */
    public void setFiftyMoveRuleCounter(int fiftyMoveRuleCounter) {
        this.fiftyMoveRuleCounter = fiftyMoveRuleCounter;
    }
}
