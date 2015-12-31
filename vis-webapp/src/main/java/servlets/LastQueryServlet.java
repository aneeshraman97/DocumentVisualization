package servlets;

import util.JsonCreator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by chris on 12/31/15.
 */
@WebServlet(value = "/last_query", name = "LastQueryServlet")
public class LastQueryServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException{
        Object attr = req.getSession().getAttribute("queries");
        String response = "null";
        if(attr != null){
            response = JsonCreator.toJson(attr);
        }
        res.getOutputStream().println(response);
    }
}
