package me.theseems.tcrates;

import com.zaxxer.hikari.pool.HikariPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class JDBCKeyStorage implements KeyStorage {
  private HikariPool pool;

  public JDBCKeyStorage(HikariPool pool) {
    this.pool = pool;
    init();
  }

  Connection getConnection() throws SQLException {
    return pool.getConnection();
  }

  private void init() {
    try (Connection connection = getConnection()) {
      Statement statement = connection.createStatement();
      statement.execute(
          "CREATE TABLE IF NOT EXISTS TCrates (Player varchar(100) PRIMARY KEY UNIQUE, Crate VARCHAR(100), Keys integer) ");
    } catch (SQLException e) {
      System.err.println("Error setting up JDBCKeyStorage: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public int getKeysFor(UUID player, String crateName) {
    try (Connection connection = getConnection()) {
      Statement statement = connection.createStatement();
      ResultSet set =
          statement.executeQuery(
              "SELECT Keys FROM TCrates WHERE Player='"
                  + player
                  + "' AND Crate='"
                  + crateName
                  + "'");
      if (set.next()) {
        return set.getInt("Keys");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return 0;
  }

  boolean contains(UUID player, String crateName) {
    try (Connection connection = getConnection()) {
      Statement statement = connection.createStatement();
      ResultSet set =
          statement.executeQuery(
              "SELECT Keys FROM TCrates WHERE Player='"
                  + player
                  + "' AND Crate='"
                  + crateName
                  + "'");
      return set.next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void setKeysFor(UUID player, String crateName, int count) {
    try (Connection connection = getConnection()) {
      Statement statement = connection.createStatement();
      if (!contains(player, crateName))
        statement.execute(
            "INSERT INTO TCrates VALUES ('" + player + "', '" + crateName + "', " + count + ")");
      else
        statement.execute(
            "UPDATE TCrates SET Keys="
                + count
                + " WHERE Player='"
                + player
                + "' AND Crate='"
                + crateName
                + "'");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
