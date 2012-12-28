package rosa.gwt.common.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping between user actions and history tokens.
 */
public enum Action {
	HOME("home"), SEARCH("search"), BROWSE_BOOK("browse"), SELECT_BOOK("select"), READ_BOOK(
			"read"), VIEW_BOOK("book"), VIEW_NARRATIVE_SECTIONS("sections"), VIEW_PARTNERS(
			"partners"), VIEW_ROSE_HISTORY("rose"), VIEW_PROJECT_HISTORY(
			"project"), VIEW_CONTACT("contact"), VIEW_CORPUS("corpus"), VIEW_TERMS(
			"terms"), VIEW_DONATION("donation"), VIEW_COLLECTION_DATA("data"), VIEW_CHARACTER_NAMES(
			"chars"), VIEW_ILLUSTRATION_TITLES("illustrations"), VIEW_BOOK_BIB("bib");

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
		if (args.length == 0) {
			return prefix;
		}

		StringBuilder sb = new StringBuilder(prefix);

		// Escape ; with ;;

		for (String arg : args) {
			if (arg == null) {
				continue;
			}

			sb.append(';');

			if (arg.contains(";")) {
				sb.append(arg.replaceAll(";", ";;"));
			} else {
				sb.append(arg);
			}
		}

		return sb.toString();
	}

	public static List<String> tokenArguments(String token) {
		List<String> args = new ArrayList<String>(4);

		StringBuilder arg = new StringBuilder();
		boolean foundsemicolon = false;
		int i = token.indexOf(';');

		if (i == -1) {
			return args;
		}

		for (i++; i < token.length(); i++) {
			char c = token.charAt(i);

			if (foundsemicolon) {
				if (c != ';') {
					args.add(arg.toString());
					arg.setLength(0);
				}

				arg.append(c);
				foundsemicolon = false;
			} else {
				if (c == ';') {
					foundsemicolon = true;
				} else {
					arg.append(c);
				}
			}
		}

		if (arg.length() > 0) {
			args.add(arg.toString());
		}

		return args;
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
