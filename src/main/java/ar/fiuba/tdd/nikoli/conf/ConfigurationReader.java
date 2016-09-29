package ar.fiuba.tdd.nikoli.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import ar.fiuba.tdd.nikoli.conf.exception.GameConfigurationException;
import ar.fiuba.tdd.nikoli.conf.exception.GameConfigurationNotFoundException;
import ar.fiuba.tdd.nikoli.model.board.Cell;
import ar.fiuba.tdd.nikoli.model.board.GameBoard;
import ar.fiuba.tdd.nikoli.model.board.Position;
import ar.fiuba.tdd.nikoli.model.rules.GameRules;
import ar.fiuba.tdd.nikoli.model.rules.Rule;
import ar.fiuba.tdd.nikoli.model.rules.exception.UnknownRuleException;
import ar.fiuba.tdd.nikoli.model.rules.factory.GameRulesFactory;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa la lectura de configuraciones (tableros/reglas) de los juegos.
 */
public class ConfigurationReader {


    private static final String CONFIGURATION_FILES_BASE_PATH = "configurationFiles/";
    private static final String RULES_CONFIGURATION_TYPE = "rules";
    private static final String BOARD_CONFIGURATION_TYPE = "board";
    private static final String FILE_EXTENSION = ".json";


    /**
     * Lee las reglas de un juego dado su nombre.
     * @param gameName nombre del juego.
     * @return una instancia de {@link GameRules}.
     * @throws GameConfigurationException si se produce un error en la lectura de reglas del juego.
     */
    public GameRules readGameRulesConfiguration(String gameName) throws GameConfigurationException {
        List<String> rulesNames = this.readGameRulesFromJson(gameName);
        GameRules gameRules = this.processGameRules(gameName, rulesNames);
        return gameRules;
    }

    /**
     * Lee el tablero de un juego dado su nombre
     * @param gameName nombre del juego.
     * @return una instancia de {@link GameBoard}
     * @throws GameConfigurationException si se produce un error en la lectura del tablero del juego.
     */
    public GameBoard readGameBoardConfiguration(String gameName) throws  GameConfigurationException {
        List<List<LinkedTreeMap>> matrix = this.readGameBoardFromJson(gameName);
        GameBoard gameBoard = this.processGameBoard(gameName, matrix);
        return gameBoard;
    }

    /**
     * Lee las reglas de un juego desde un archivo JSON.
     * @param gameName nombre del juego
     * @return una instacia de {@link GameRules} obtenida de la lectura del JSON de configuracion
     * @throws GameConfigurationNotFoundException si no se encuentra archivo de configuracion de juego
     */
    private List<String> readGameRulesFromJson(String gameName) throws GameConfigurationNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        InputStreamReader jsonFile = this.getConfigurationFile(gameName, RULES_CONFIGURATION_TYPE);

        List<String> rulesNames = gson.fromJson(jsonFile, List.class);

        return rulesNames;
    }

    /**
     * Lee el tablero de un juego desde un archivo JSON.
     * @param gameName nombre del juego.
     * @return una matriz de celdas genericas del tablero del juego.
     * @throws GameConfigurationNotFoundException si no encontro el archivo de configuracion de tablero de juego.
     */
    private List<List<LinkedTreeMap>> readGameBoardFromJson(String gameName) throws GameConfigurationNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        InputStreamReader jsonFile = this.getConfigurationFile(gameName, BOARD_CONFIGURATION_TYPE);

        List<List<LinkedTreeMap>> boardMatrix = gson.fromJson(jsonFile, List.class);

        return boardMatrix;
    }

    /**
     * Retorna un archivo de configuracion.
     * @param gameName nombre del juego.
     * @param configurationType tipo de configuracion (reglas o tablero)
     * @return un {@link InputStreamReader} del archivo de configuracion
     * @throws GameConfigurationNotFoundException si no se encontro el archivo de configuracion del juego pedido.
     */
    private InputStreamReader getConfigurationFile(String gameName, String configurationType) throws GameConfigurationNotFoundException {

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = classLoader.getResource(CONFIGURATION_FILES_BASE_PATH
                    + gameName + "/" + gameName + "-" + configurationType + FILE_EXTENSION)
                    .getPath();
            InputStreamReader jsonFile = new InputStreamReader(new FileInputStream(path), "UTF-8");

            return jsonFile;
        } catch (NullPointerException | IOException e) {
            throw new GameConfigurationNotFoundException(gameName, configurationType);
        }
    }

    /**
     * Procesa la lista de nombres de reglas de un juego.
     * @param gameName nombre del juego
     * @param gameRulesNames lista de nombre de las reglas del juego
     * @returnuna instancia de {@link GameRules}
     * @throws GameConfigurationException si se produjo un error en el procesamiento de las reglas del juego
     */
    private GameRules processGameRules(String gameName, List<String> gameRulesNames) throws GameConfigurationException {
        GameRules gameRules = new GameRules();
        gameRules.setGameName(gameName);

        List<Rule> rules = new ArrayList<Rule>();

        for (String name: gameRulesNames) {
            try {
                Rule rule = GameRulesFactory.getInstance().createRuleByName(name);
                rules.add(rule);
            } catch (UnknownRuleException e) {
                throw new GameConfigurationException(e.getMessage());
            }
        }

        gameRules.setRules(rules);

        return gameRules;
    }

    /**
     * Procesa la matriz de celdas genericas del tablero de un juego.
     * @param gameName nombre del juego.
     * @param boardMatrix matriz generica de tablero de juego.
     * @return una instancia de {@link GameBoard}
     */
    private GameBoard processGameBoard(String gameName, List<List<LinkedTreeMap>> boardMatrix) {

        GameBoard gameBoard = new GameBoard();

        List<List<Cell>> matrix = new ArrayList<List<Cell>>();

        for (List<LinkedTreeMap> row: boardMatrix) {

            List<Cell> rowColumns = new ArrayList<Cell>();

            for (LinkedTreeMap genericCell: row) {
                int posX = ((Double)genericCell.get("positionX")).intValue();
                int posY = ((Double)genericCell.get("positionY")).intValue();
                int value = genericCell.get("value") != null ? ((Double)genericCell.get("value")).intValue() : 0;
                int rowValue = genericCell.get("rowValue") != null ? ((Double)genericCell.get("rowValue")).intValue() : 0;
                int columnValue = genericCell.get("columnValue") != null ? ((Double)genericCell.get("columnValue")).intValue() : 0;
                Position position = new Position(posX, posY);
                Cell cell = new Cell(position, Integer.valueOf(value), Integer.valueOf(rowValue), Integer.valueOf(columnValue));

                rowColumns.add(cell);
            }

            matrix.add(rowColumns);
        }

        gameBoard.setGameMatrix(matrix);

        return gameBoard;
    }

}
