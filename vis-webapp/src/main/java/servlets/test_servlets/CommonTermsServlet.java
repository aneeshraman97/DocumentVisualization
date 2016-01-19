package servlets.test_servlets;

import api.exception.LuceneSearchException;
import api.reader.LuceneIndexReader;
import common.data.ScoredTerm;
import internal.static_util.TermsAnalyzer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import servlets.servlet_util.JsonCreator;
import servlets.servlet_util.RequestUtils;
import servlets.servlet_util.ResponseUtils;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.List;

/**
 * Servlet that is designed to get the most common terms for the document.
 * Created by chris on 10/13/15.
 */
@WebServlet(value = "/common_terms", name = "commonTermsServlet")
public class CommonTermsServlet extends GenericServlet {
    private static final Log log = LogFactory.getLog(CommonTermsServlet.class);
    /**
     * Servlet Service for common terms
     *
     * @param req Required parameters:
     *            docId: The document ID to get the most common terms for
     *            Optional Parameters:
     *            limit: The limit of the number of common terms to return
     * @param res The response contains a JSON object with a List of ScoredTerms.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            int docId = RequestUtils.getIntegerParameter(req, "docId"); // TODO: If docID doesn't exist?
            List<ScoredTerm> terms;
            if (req.getParameterMap().containsKey("limit")) {
                int limit = RequestUtils.getIntegerParameter(req, "limit");
                terms = TermsAnalyzer.getTopTerms(LuceneIndexReader.getInstance().getReader(), docId, limit);
            } else {
                terms = TermsAnalyzer.getTerms(LuceneIndexReader.getInstance().getReader(), docId);
            }

            String response = JsonCreator.toJson(terms);
            ResponseUtils.printResponse(res, response);

        } catch (LuceneSearchException | NumberFormatException e) {
            log.error("Error while searching.", e);
        }
    }
}