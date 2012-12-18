package rosa.search;


// TODO figure out where tests should be. Use GWT stuff?

//public class SearcherTest extends TestCase {
//    private static String[][] charnames = new String[][] { { "Amors", "Amour",
//            "Amor", "Amours", "God of Love", "Love" } };
//
//    private void checkparse(String query, String... result) {
//        List<String> luceneterms = Searcher.parseUserQuery(query);
//
//        assertEquals(result.length, luceneterms.size());
//
//        for (int i = 0; i < result.length; i++) {
//            assertEquals(result[i], luceneterms.get(i));
//        }
//    }
//
//    public void testParser() {
//        checkparse("bla:h~", "bla\\:h\\~");
//        checkparse("b\\", "b");
//        checkparse("\\\"a", "\\\"a");
//        checkparse("+++---45", "+45");
//        checkparse("-bl*a\\?h", "-bl*a\\?h");
//        checkparse("one two-", "one", "two\\-");
//        checkparse("+-\"one two\"", "+\"one two\"");
//        checkparse("\\+\"", "\\+");
//        checkparse("a b\" c", "a", "b", "\" c\"");
//        checkparse("\\+\"one two\" three", "\\+", "\"one two\"", "three");
//        checkparse("test~", "test\\~");
//        checkparse("a? -\"b test\"~33", "a?", "-\"b test\"~33");
//        checkparse("\"test\"~", "\"test\"", "\\~");
//    }
//
//    private void checkexpand(String luceneterm, Searcher.LuceneFieldType type,
//            String result) {
//        StringBuilder sb = new StringBuilder();
//        Searcher.expandLuceneTerm(sb, charnames, luceneterm, type);
//        assertEquals(result, sb.toString().trim());
//    }
//
//    public void testExpandLuceneQuery() {
//        checkexpand("blah", LuceneFieldType.STRING, "(blah)");
//        checkexpand("\"test\"", LuceneFieldType.ENGLISH, "(\"test\" )");
//        checkexpand("\"hickey\"", LuceneFieldType.OLD_FRENCH, "(\"test\" )");
//        
//        checkexpand("\"test\"", LuceneFieldType.OLD_FRENCH, "(\"test\" )");
//        
//        // hmm how to test, expansion order random...
//        //checkexpand("unethical", LuceneFieldType.OLD_FRENCH, "(test )");
//        checkexpand("amour", LuceneFieldType.OLD_FRENCH, "(test )");
//        checkexpand("\"amour blah\"", LuceneFieldType.OLD_FRENCH, "(test )");
//    }
//}
