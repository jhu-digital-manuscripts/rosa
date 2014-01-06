package pizan.gwt.client;

import java.util.List;

import rosa.gwt.common.client.Util;

// TODO refactor this and analytics, don't use enum so extensible

/**
 * Mapping between user actions and history tokens.
 */
public enum Action {
    HOME("home"), SEARCH("search"), BROWSE_BOOK("browse"), SELECT_BOOK("select"), READ_BOOK(
            "read"), VIEW_BOOK("book"), VIEW_PARTNERS("partners"), VIEW_PIZAN(
            "pizan"), VIEW_CONTACT("contact"), VIEW_WORKS(
            "works"), VIEW_TERMS("terms"), VIEW_PROPER_NAMES("names");

    private final String prefix;

    Action(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Build up a token with the given arguments. Null arguments are ignored.
     * 
     * @param args
     * @return
     */

    public String toToken(String... args) {
       return Util.toToken(prefix, args);
    }

    public static List<String> tokenArguments(String token) {
        return Util.parseTokenArguments(token);
    }

    /**
     * @param token
     * @return corresponding value or null if the token is invalid
     */
    public static Action fromToken(String token) {
        int i = token.indexOf(';');

        if (i != -1) {
            token = token.substring(0, i);
        }

        for (Action state : Action.values()) {
            if (state.prefix.equals(token)) {
                return state;
            }
        }

        return null;
    }
}
