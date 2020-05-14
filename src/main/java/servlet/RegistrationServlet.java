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

public class RegistrationServlet extends HttpServlet {
    private BankClientService bankClientService = BankClientService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = createPageVariablesMap(req);
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", pageVariables));
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = Collections.synchronizedMap(new HashMap<>());
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String strMoney = req.getParameter("money");

        if (name == null || password == null || strMoney == null) {
            pageVariables.put("message", "Error: Empty data or wrong format.");
            resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            return;
        }

        Long money = Long.parseLong(strMoney);

        try {
            if (bankClientService.addClient(new BankClient(name, password, money))) {
                pageVariables.put("message", "Add client successful");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            } else {
                pageVariables.put("message", "Client not add");
                resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> createPageVariablesMap(HttpServletRequest request) {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("name", request.getParameter("name"));
        pageVariables.put("password", request.getParameter("password"));
        pageVariables.put("money", request.getParameter("money"));
        return pageVariables;
    }
}
