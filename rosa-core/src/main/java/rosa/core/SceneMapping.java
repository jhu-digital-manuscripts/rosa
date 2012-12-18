package rosa.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rosa.core.util.XMLUtil;

// TODO move

public class SceneMapping {
	/**
	 * Divide into blocks. Each block (except possibly the last) ends with a
	 * known lecoy number. If there is a difference between guess for the end
	 * block and the correct value, spread that difference evenly through the
	 * block.
	 * 
	 * Returns array of start (inclusive), end (exlusive) lecoy numbers parallel
	 * to cols.
	 */

	private static int[] guessLecoy(List<BookStructure.Column> cols,
			boolean usesyncpoints) {
		int[] guess = new int[cols.size() + 1];

		int total_poetry_lines = 0;

		for (BookStructure.Column col : cols) {
			total_poetry_lines += col.linesOfPoetry();
		}

		int avg_col_poetry_lines = Math.round((float) total_poetry_lines
				/ cols.size());

		int start = 0; // start of block inclusive
		int end = cols.size(); // end of block inclusive
		guess[0] = 1;

		for (;;) {
			for (end = start; end < cols.size(); end++) {
				BookStructure.Column col = cols.get(end);

				guess[end + 1] = guess[end] + col.linesOfPoetry();

				if (col.firstLineLecoy() != -1) {
					break;
				}
			}

			if (end == cols.size()) {
				break;
			}

			// TODO spread more uniformly?

			if (usesyncpoints) {
				BookStructure.Column startcol = cols.get(start);
				BookStructure.Column endcol = cols.get(end);

				int diff = guess[end] - endcol.firstLineLecoy();

				// System.err.println(endcol + " " + diff);

				guess[end] = endcol.firstLineLecoy();
				guess[end + 1] = guess[end] + endcol.linesOfPoetry();

				if (diff < 0) {
					diff = -diff;
					// Missing lines, possibly whole leaves
					// Assume missing leaves are in middle of block

					if (diff > avg_col_poetry_lines * 2) {
						System.err
								.println("Probably at least one leaf missing between "
										+ startcol + " and " + endcol);
					}

					int middle = (start + end) / 2;

					for (int i = middle; i < end; i++) {
						guess[i] += diff;
					}
				} else if (diff > 0) {
					// More non-lecoy itmes than expected
					// Assume additional stuff is in middle of block

					int middle = (start + end) / 2;

					for (int i = middle; i < end; i++) {
						guess[i] -= diff;
					}
				}
			}

			start = end + 1;
		}

		// for (int i = 0; i < cols.size(); i++) {
		// BookStructure.Column col = cols.get(i);
		//
		// System.err.println(col
		// + " "
		// + guess[i]
		// + (col.firstLineLecoy() == -1 ? "" : " [lecoy: "
		// + col.firstLineLecoy() + "]"));
		// }

		return guess;
	}

	/**
	 * Guess narrative mapping. If transcription file not null, add lines from
	 * this transcription. The string trans_prefix will be prepended to each transcription line.
	 */
	public static NarrativeMapping guessNarrativeScenes(NarrativeSections nar,
			BookStructure struct, boolean usesyncpoints, File transcription, String trans_prefix)
			throws IOException, SAXException {
		// Turn guess of column start/end lecoy into guess of scene start/end
		// columns

		List<BookStructure.Column> cols = struct.columns();
		int[] lecoyguess = guessLecoy(cols, usesyncpoints);

		NarrativeSections.Scene[] scenes = nar.asScenes();
		BookStructure.Column[] sceneguess = new BookStructure.Column[scenes.length * 2];

		// line offset into scene, starts at 1, inclusive
		int[] scenelineguess = new int[scenes.length * 2];

		for (int i = 0; i < lecoyguess.length - 1; i++) {
			int start = lecoyguess[i];
			int end = lecoyguess[i + 1];
			BookStructure.Column col = cols.get(i);

			for (int j = 0; j < scenes.length; j++) {
				NarrativeSections.Scene scene = scenes[j];

				if (!scene.isLecoy()) {
					continue;
				}

				if (start <= scene.lecoy_start && end > scene.lecoy_start) {
					sceneguess[j * 2] = col;
					scenelineguess[j * 2] = scene.lecoy_start - start + 1;
				}

				if (start <= scene.lecoy_end && end > scene.lecoy_end) {
					sceneguess[(j * 2) + 1] = col;
					scenelineguess[(j * 2) + 1] = scene.lecoy_end - start + 1;
				}
			}
		}

		NodeList milestones = null;

		if (transcription != null) {
			Document doc = XMLUtil.createDocument(transcription);
			milestones = doc.getElementsByTagName("milestone");
		}

		// Turn scene start/end columns into narrative tagging
		NarrativeMapping guess = new NarrativeMapping();

		for (int j = 0; j < scenes.length; j++) {
			NarrativeSections.Scene scene = scenes[j];
			BookStructure.Column start = sceneguess[j * 2];
			BookStructure.Column end = sceneguess[(j * 2) + 1];
			int startline = scenelineguess[j * 2];
			int endline = scenelineguess[(j * 2) + 1];

			if (start == null || end == null) {
				// System.err.println("Bad error, start or end null for scene "
				// + scene.id);
				continue;
			}

			String start_trans = null;
			
			if (milestones != null) {
				String s = "" + scene.lecoy_start;
				
				for (int i = 0; i < milestones.getLength(); i++) {
					Element m = (Element) milestones.item(i);
					
					if (m.getAttribute("n").equals(s)) {
						// TODO not accurate for all transcriptions
						start_trans = trans_prefix + " " + XMLUtil.extractText(m.getParentNode()).trim();
						break;
					}
				}
			}

			//System.out.println(scene.id + " " + scene.lecoy_start);
			
			guess.scenes().add(
					new NarrativeMapping.Scene(scene.id,
							start.parent().folio(), "" + start.columnLetter(),
							startline, end.parent().folio(), ""
									+ end.columnLetter(), endline, start_trans,
							false, scene.lecoy_start));
		}

		return guess;
	}

	private static int numColumns(String folio, String col) {
		int n = Integer.parseInt(folio.substring(0, folio.length() - 1)) - 1;
		int s = folio.endsWith("r") ? 0 : 1;
		int c = (col.equals("a") || col.equals("c")) ? 0 : 1;

		return (n * 4) + (s * 2) + c;
	}

	public static void printComparison(NarrativeMapping truthnar,
			NarrativeMapping guessnar) {
		int count = 0;
		int columncorrect = 0;
		int pagecorrect = 0;
		int totalcoldiff = 0;
		int withinoncolumn = 0;

		System.out
				.println("Scene,Location,GuessColumn,CorrectColumn,ColumnDiff");

		next: for (NarrativeMapping.Scene truth : truthnar.scenes()) {
			for (NarrativeMapping.Scene guess : guessnar.scenes()) {
				if (guess.id().equals(truth.id())) {
					if (guess.startFolio().equals(truth.startFolio())) {
						pagecorrect++;

						if (guess.startFolioCol().equals(truth.startFolioCol())) {
							columncorrect++;
						}
					}

					int coldiff = numColumns(guess.startFolio(), guess
							.startFolioCol())
							- numColumns(truth.startFolio(), truth
									.startFolioCol());

					totalcoldiff += Math.abs(coldiff);

					if (Math.abs(coldiff) <= 1) {
						withinoncolumn++;
					}

					System.out.println(guess.id()
							+ ", "
							+ guess.startFolio()
							+ "."
							+ guess.startFolioCol()
							+ ", "
							+ truth.startFolio()
							+ "."
							+ truth.startFolioCol()
							+ ", "
							+ numColumns(guess.startFolio(), guess
									.startFolioCol()) + ", " + coldiff);

					count++;
					continue next;
				}
			}

			System.err.println("No guess for " + truth.id());
		}

		System.err.println("Total " + count);
		System.err.println("Correct column: " + columncorrect + " ("
				+ ((float) columncorrect / count) + "%)");
		System.err.println("Correct within one column: " + withinoncolumn
				+ " (" + ((float) withinoncolumn / count) + "%)");
		System.err.println("Correct page: " + pagecorrect + " ("
				+ ((float) pagecorrect / count) + "%)");
		System.err.println("Column diff avg: "
				+ +((float) totalcoldiff / count));
	}
}
