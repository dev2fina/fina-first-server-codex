package net.fina.server.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;

public class DatabaseMetaDataInfoWork implements Work {

	private DatabaseMetaData databaseMetaData;

	@Override
	public void execute(Connection connection) throws SQLException {
		databaseMetaData = connection.getMetaData();

	}

	public DatabaseMetaData getDatabaseMetaData() {
		return databaseMetaData;
	}

	public void setDatabaseMetaData(DatabaseMetaData databaseMetaData) {
		this.databaseMetaData = databaseMetaData;
	}

}
