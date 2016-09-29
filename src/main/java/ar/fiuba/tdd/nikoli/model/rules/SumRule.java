package ar.fiuba.tdd.nikoli.model.rules;

import ar.fiuba.tdd.nikoli.model.Move;
import ar.fiuba.tdd.nikoli.model.board.Cell;

/**
 * Clase que representa la regla de sumar una cantidad determinada en una fila.
 */
public abstract class SumRule extends Rule {


    @Override
    public boolean isRuleBroken(GameBoardIterator gameBoardIterator, Move move) {

        Cell cell = gameBoardIterator.getCell(move.getPosition());

        int sum = 0;

        //Se recorre para atras hasta encontrar la celda que indica cuanto se debe sumar
        while (!cell.isBorder()) {
            sum += cell.getValue();
            cell = getPreviousCell(gameBoardIterator, cell);
        }

        int sumExpected = getSumExpected(cell);

        //Se recorre para adelante hasta llegar a la ultima celda
        while (hasNext(gameBoardIterator, cell)) {
            cell = getNextCell(gameBoardIterator, cell);
            sum += cell.getValue();
        }

        return (sum == sumExpected);
    }

    protected abstract Cell getPreviousCell(GameBoardIterator gameBoardIterator, Cell cell);

    protected abstract int getSumExpected(Cell cell);

    protected abstract boolean hasNext(GameBoardIterator gameBoardIterator, Cell cell);

    protected abstract Cell getNextCell(GameBoardIterator gameBoardIterator, Cell cell);

}