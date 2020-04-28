package simplifier;

import java.io.IOException;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;

import corpus_reader.Corpus;
import preprocessing.ExpressionIdentifier;

public class MainServer extends NanoHTTPD{

	private ExpressionIdentifier dictionariesExpressions;
	private String[] resourcesExpressionIdentifier = {"resources/simpleAprenent.csv", "resources/expressio_vocabulary.txt"};
	private Simplifier simplifier;
	public MainServer(int port)  throws IOException {
		super(port);
		dictionariesExpressions = new ExpressionIdentifier(resourcesExpressionIdentifier);
		this.simplifier = new Simplifier();
		
		
		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
	}
	
	@Override
    public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
        String uri = session.getUri();
//        HelloServer.LOG.info(method + " '" + uri + "' ");

        String msg = "<html><body><h1>Hello there!</h1>\n";
        Map<String, String> parms = session.getParms();
        System.out.println(parms);
        if (parms.get("corpus") == null) {
//            msg += "<form action='?' method='get'>\n" 
//            			+ "<p>Parsed text:</p>\n"
//            			+ " <textarea id='text' name='parser' rows='4' cols='50'></textarea>"
//            			+ "<p>Corefed text:</p>\n"
//            			+ " <textarea id='text' name='coref' rows='4' cols='50'></textarea>"
//            			+ "<input type='submit'>"
//            			+ "</form>\n";
            msg += "<form action='?' method='get'>\n" 
        			+ "<p>Text:</p>\n"
        			+ "<select id='corpus' name='corpus'>"
                    + "<option value='Cendrillon'>Cendrillon</option>"
                    + "<option value='Pierre'>Pierre</option>"
                    + "</select>"
        			+ "<input type='submit'>"
        			+ "</form>\n";
        } else {
            msg += "<p>Hello!</p>";
            String parsedcorpus = null; 
            String corefcorpus = null;
            if(parms.get("corpus").equals("Cendrillon")){
            	parsedcorpus = "corpus/cendrillon_1.conll"; 
            	corefcorpus = "corpus/corpus_coref_pred_selected.conll";
            }else if(parms.get("corpus").equals("Pierre")){
            	parsedcorpus = "corpus/pierrelapin_1.conll"; 
            	corefcorpus = "corpus/corpus_coref_pred_selected.conll";
            }
            Corpus corpus = simplifier.preprocessing(parsedcorpus, corefcorpus, dictionariesExpressions);
            msg += corpus.toString();
        }
        
        msg += "</body></html>\n";

        return Response.newFixedLengthResponse(msg);
    }
	
	public static void main(String[] args) throws IOException {
		new MainServer(8080);
	}

}
