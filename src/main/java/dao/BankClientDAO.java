package dao;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private static BankClientDAO bankClientDAO;
    private Connection connection;

    private BankClientDAO() {}
    private BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public static BankClientDAO getInstance(Connection connection) {
        if (bankClientDAO == null) {
            bankClientDAO = new BankClientDAO(connection);
        }
        return bankClientDAO;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        List<BankClient> arrayList = new ArrayList<>();
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client");
        ResultSet result = stmt.getResultSet();
        while (result.next()) {
            arrayList.add(new BankClient(result.getLong("id"),
                    result.getString("name"),
                    result.getString("password"),
                    result.getLong("money")));
        }
        result.close();
        stmt.close();

        return arrayList;
    }

    public boolean validateClient(String name, String password) throws SQLException {
        String sql = "select * from bank_client where name = ? and password = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, password);
        ResultSet resultSet = pstmt.executeQuery();
        boolean result = resultSet.next();
        resultSet.close();
        pstmt.close();

        return result;
    }

    public void updateClientsMoney(BankClient sender, BankClient receiver, Long transactValue) throws SQLException {
        connection.setAutoCommit(false);
        String sqlDecMoney = "UPDATE bank_client SET money=money-? WHERE id=?";
        PreparedStatement pstmtDec = connection.prepareStatement(sqlDecMoney);
        pstmtDec.setLong(1, transactValue);
        pstmtDec.setLong(2, sender.getId());
        pstmtDec.executeUpdate();
        pstmtDec.close();

        String sqlIncMoney = "UPDATE bank_client SET money=money+? WHERE id=?";
        PreparedStatement pstmtInc = connection.prepareStatement(sqlIncMoney);
        pstmtInc.setLong(1, transactValue);
        pstmtInc.setLong(2, receiver.getId());
        pstmtInc.executeUpdate();
        pstmtInc.close();
        connection.commit();
        connection.setAutoCommit(true);
    }

    public BankClient getClientById(long id) throws SQLException {
        String sql = "select * from bank_client where id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, id);
        ResultSet result = pstmt.executeQuery();
        result.next();
        String name = result.getString("name");
        String password = result.getString("password");
        Long money = result.getLong("money");
        result.close();
        pstmt.close();
        return new BankClient(id, name, password, money);
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        String sql = "select * from bank_client where name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet result = pstmt.executeQuery();
        if (!result.next()) {
            return false;
        }

        Long realSum = result.getLong("money");
        result.close();
        pstmt.close();
        if (expectedSum > realSum) {
            return false;
        } else {
            return true;
        }
    }

    public long getClientIdByName(String name) throws SQLException {
        String sql = "select * from bank_client where name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet result = pstmt.executeQuery();
        result.next();
        Long id = result.getLong("id");
        result.close();
        pstmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        String sql = "select * from bank_client where name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            Long id = result.getLong("id");
            String password = result.getString("password");
            Long money = result.getLong("money");
            result.close();
            pstmt.close();
            return new BankClient(id, name, password, money);
        } else {
            return null;
        }
    }

    public void addClient(BankClient client) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT bank_client (id, name, password, money) VALUES (" +
                client.getId() + ", '" +
                client.getName() + "', '" +
                client.getPassword() + "', " +
                client.getMoney() + ")");
        stmt.close();
    }

    public void deleteClient(BankClient client) throws SQLException {
        String sql = "DELETE FROM bank_client WHERE id=?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, client.getId());
        pstmt.executeUpdate();
        pstmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
