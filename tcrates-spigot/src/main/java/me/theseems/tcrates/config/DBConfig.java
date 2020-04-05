package me.theseems.tcrates.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;

public class DBConfig {
  private String url;
  private String type;
  private String user;
  private String password;

  public DBConfig(String url, String type, String user, String password) {
    this.url = url;
    this.type = type;
    this.user = user;
    this.password = password;
  }

  public HikariPool getPool() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("allowMultiQueries", "true");
    config.setMaximumPoolSize(200);
    HikariPool pool = new HikariPool(config);
    return pool;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
