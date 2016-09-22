package ar.fiuba.tdd.nikoli.rules.reader;


import ar.fiuba.tdd.nikoli.rules.GameRules;
import ar.fiuba.tdd.nikoli.rules.Rule;
import ar.fiuba.tdd.nikoli.rules.exception.GameRulesNotFoundException;
import ar.fiuba.tdd.nikoli.rules.exception.UnknownRuleException;
import ar.fiuba.tdd.nikoli.rules.factory.GameRulesFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase encargada de realizar el procesamiento de reglas
 */
public class GameRulesReader {

    private static GameRulesReader instance;

    private GameRulesReader() { }


    public static GameRulesReader getInstance() {
        if (instance == null) {
            instance = new GameRulesReader();
        }

        return instance;
    }


    /**
     * Genera las reglas de juego a partir de su nombre
     * @param gameName
     * @return
     * @throws GameRulesNotFoundException
     * @throws UnknownRuleException
     */
    public GameRules readGameRules(String gameName)
            throws GameRulesNotFoundException, UnknownRuleException {

        GameRules gameRules = this.readGameRulesFromJson(gameName);
        this.processGameRules(gameRules);

        return gameRules;
    }


    /**
     * Lee las reglas de un juego desde un archivo JSON
     * @param gameName
     * @return
     * @throws GameRulesNotFoundException
     * @throws UnknownRuleException
     */
    private GameRules readGameRulesFromJson(String gameName) throws GameRulesNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

        try {

            String path = System.getProperty("user.dir") + "/gameConfigFiles/" + gameName + "-rules.json";

            InputStreamReader jsonFile = new InputStreamReader(new FileInputStream(path), "UTF-8");

            GameRules rules = gson.fromJson(jsonFile, GameRules.class);
            System.out.println("Reglas del juego \'" + gameName + "\' leidas exitosamente!");

            return rules;

        } catch (IOException e) {
            throw new GameRulesNotFoundException("No se ha encontrado las reglas del juego ");
        }
    }


    /**
     * Procesa los nombres de las reglas encontradas creando objetos de las clases correspondientes
     * @param gameRules
     * @throws UnknownRuleException
     */
    private void processGameRules(GameRules gameRules) throws UnknownRuleException {
        List<Rule> rules = new ArrayList<Rule>();

        for(String name: gameRules.getRulesNames()) {
            try {
                Rule rule = GameRulesFactory.getInstance().createRuleByName(name);
                rules.add(rule);
            } catch (UnknownRuleException e) {
                throw new UnknownRuleException("Regla desconocida (" + name + ")");
            }
        }

        gameRules.setRules(rules);
    }

}
