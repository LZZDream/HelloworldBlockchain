package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class WalletImpl extends Wallet {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizerDatabaseDefaultImpl.class);

    private static final String WALLET_DATABASE_DIRECT_NAME = "WalletDatabase";
    private static final String WALLET_DATABASE_FILE_NAME = "Wallet.db";

    private String blockchainDataPath;
    private Connection connection;

    public WalletImpl(String blockchainDataPath) {
        this.blockchainDataPath = blockchainDataPath;
        initDatabase();
    }

    private void initDatabase() {
        String createTable1Sql1 = "CREATE TABLE IF NOT EXISTS wallet " +
                "(" +
                "privateKey CHAR(100) PRIMARY KEY NOT NULL," +
                "publicKey CHAR(100)," +
                "address CHAR(100)" +
                ")";
        JdbcUtil.executeSql(connection(),createTable1Sql1);
    }

    @Override
    public List<Account> queryAllAccount() {
        String sql = "SELECT * FROM wallet";
        List<Account> accountList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String privateKey = resultSet.getString("privateKey");
                String publicKey = resultSet.getString("publicKey");
                String address = resultSet.getString("address");
                accountList.add(new Account(privateKey,publicKey,address));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return accountList;
    }

    public Account queryAccountByAddress(String address) {
        String sql = "SELECT * FROM wallet where address = ?" ;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,address);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String privateKey = resultSet.getString("privateKey");
                String publicKey = resultSet.getString("publicKey");
                String addressTemp = resultSet.getString("address");
                return new Account(privateKey,publicKey,addressTemp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
            JdbcUtil.closeResultSet(resultSet);
        }
        return null;
    }

    @Override
    public Account createAccount() {
        Account account = AccountUtil.randomAccount();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        if(queryAccountByAddress(account.getAddress()) != null){
            return;
        }
        String sql = "INSERT INTO wallet (privateKey,publicKey,address) " +
                "VALUES (?,?,?);";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection().prepareStatement(sql);
            preparedStatement.setString(1,account.getPrivateKey());
            preparedStatement.setString(2,account.getPublicKey());
            preparedStatement.setString(3, account.getAddress());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement);
        }
    }

    @Override
    public void deleteAccountByAddress(String address) {
        String sql1 = "DELETE FROM wallet WHERE address = ?";
        PreparedStatement preparedStatement1 = null;
        try {
            preparedStatement1 = connection().prepareStatement(sql1);
            preparedStatement1.setString(1,address);
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.closeStatement(preparedStatement1);
        }
    }


    private synchronized Connection connection() {
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            File walletDatabaseDirect = new File(blockchainDataPath,WALLET_DATABASE_DIRECT_NAME);
            FileUtil.mkdir(walletDatabaseDirect);
            File walletDatabasePath = new File(walletDatabaseDirect,WALLET_DATABASE_FILE_NAME);
            String jdbcConnectionUrl = JdbcUtil.getJdbcConnectionUrl(walletDatabasePath.getAbsolutePath());
            connection = DriverManager.getConnection(jdbcConnectionUrl);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
