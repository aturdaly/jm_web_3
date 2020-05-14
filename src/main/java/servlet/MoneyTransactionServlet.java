package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {
    private BankClientService bankClientService = BankClientService.getInstance();
    private PageGenerator pageGenerator = PageGenerator.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = createPageVariablesMap(req);
        resp.getWriter().println(pageGenerator.getPage("moneyTransactionPage.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = Collections.synchronizedMap(new HashMap<>());
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String senderName = req.getParameter("senderName");
        String senderPass = req.getParameter("senderPass");
        Long count = Long.parseLong(req.getParameter("count"));
        String nameTo = req.getParameter("nameTo");

        if (senderName == null || senderPass == null || count == null || nameTo == null) {
            pageVariables.put("message", "Error: Empty data or wrong format.");
            resp.getWriter().println(pageGenerator.getPage("resultPage.html", pageVariables));
            return;
        }

        if (count <= 0) {
            pageVariables.put("message", "Error: Empty data or wrong format.");
            resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            return;
        }

        try {
            BankClient sender = bankClientService.getClientByName(senderName);
            sender.setPassword(senderPass);
            if (bankClientService.sendMoneyToClient(sender, nameTo, count)) {
                pageVariables.put("message", "The transaction was successful");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", pageVariables));
            } else {
                pageVariables.put("message", "transaction rejected");
                resp.getWriter().println(pageGenerator.getPage("resultPage.html", pageVariables));
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("senderName", request.getParameter("senderName"));
        pageVariables.put("senderPass", request.getParameter("senderPass"));
        pageVariables.put("count", request.getParameter("count"));
        pageVariables.put("nameTo", request.getParameter("nameTo"));
        return pageVariables;
    }
}
