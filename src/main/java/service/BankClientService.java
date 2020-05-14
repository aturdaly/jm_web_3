package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {
    private static BankClientService bankClientService;

    private BankClientService() {
    }

    public static BankClientService getInstance() {
        if (bankClientService == null) {
            bankClientService = new BankClientService();
        }
        return bankClientService;
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            return null;
        }
    }

    public List<BankClient> getAllClient() {
        try {
            return getBankClientDAO().getAllBankClient();
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean deleteClient(String name) throws DBException {
        BankClientDAO dao = getBankClientDAO();
        BankClient bankClient = this.getClientByName(name);
        try {
            dao.deleteClient(bankClient);
            return !dao.validateClient(bankClient.getName(), bankClient.getPassword());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean addClient(BankClient client) throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            if (dao.getClientByName(client.getName()) != null) {
                return false;
            }
            dao.addClient(client);
            return dao.validateClient(client.getName(), client.getPassword());
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            if (!dao.validateClient(sender.getName(), sender.getPassword())) {
                return false;
            }
            if (!dao.isClientHasSum(sender.getName(), value)) {
                return false;
            }
            BankClient receiver = dao.getClientByName(name);
            dao.updateClientsMoney(sender, receiver, value);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException{
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=Abus0lih").       //password
                    append("&serverTimezone=UTC");

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return BankClientDAO.getInstance(getMysqlConnection());
    }
}
