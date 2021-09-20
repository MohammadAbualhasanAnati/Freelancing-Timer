import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class DBSqlite {
	
	private String dbFilePath;
	private Connection connection;
	private String tableName;
	
	public DBSqlite(String dbFilePath) {
		this.dbFilePath=dbFilePath;
	}
	
	private Connection connect() throws SQLException {
		String url="jdbc:sqlite:"+dbFilePath;
		Connection connection=DriverManager.getConnection(url);
		return connection;
	}
	
	public void checkTable(String tableName, String tableAttributes) {
		try{
			connection=connect();
			
			String sql="CREATE TABLE IF NOT EXISTS "+tableName+" ("+tableAttributes+")";
			
			Statement statement=connection.createStatement();
			statement.execute(sql);
			
			this.tableName=tableName;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void dropTable(String tableName) {
		try{
			connection=connect();
			
			String sql="DROP TABLE IF EXISTS "+tableName;
			
			Statement statement=connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void insert(String columns,String data) {
		try {
			connection=connect();
			
			String sql="INSERT OR IGNORE INTO "+tableName+" ("+columns+") VALUES ("+data+")";
			
			PreparedStatement statement=connection.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void update(String column,String data,String[] columnsConditional,String[] values) {
		try {
			connection=connect();
			
			String sql="UPDATE "+tableName+" SET "+column+"="+data+" WHERE (";
			
			for(int i=0;i<columnsConditional.length;i++) {
				sql+=columnsConditional[i]+" = "+values[i]+" ";
				if(i!=columnsConditional.length-1) {
					sql+="AND ";
				}
			}
			
			sql+=")";
			
			PreparedStatement statement=connection.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void dropAllData() {
		try {
			connection=connect();
			
			String sql="DELETE FROM "+tableName;
			
			PreparedStatement statement=connection.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void deleteRows(String[] columnsConditional,String[] values) {
		try {
			connection=connect();
			
			String sql="DELETE FROM "+tableName+" WHERE (";
			
			for(int i=0;i<columnsConditional.length;i++) {
				sql+=columnsConditional[i]+" = "+values[i]+" ";
				if(i!=columnsConditional.length-1) {
					sql+="AND ";
				}
			}
			
			sql+=")";
			
			PreparedStatement statement=connection.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<String[]> selectAll(String[] columnsRequired,String[] columnsTypes) {
		ArrayList<String[]> result=new ArrayList<String[]>();
		try {
			connection=connect();
			
			String sql="SELECT * FROM "+tableName;
			
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(sql);
			
			while(results.next()) {
				String[] resultSub=new String[columnsRequired.length];
				
				
				for(int i=0;i<columnsRequired.length;i++) {
					String resultReq=columnsRequired[i];
					String resultType=columnsTypes[i];
					
					switch(resultType) {
						case "int":
							resultSub[i]=String.valueOf(results.getInt(resultReq));
							break;
						case "double":
							resultSub[i]=String.valueOf(results.getDouble(resultReq));
							break;
						case "string":
							resultSub[i]=String.valueOf(results.getString(resultReq));
							break;
							
					}
				}
				
				result.add(resultSub);
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public String[] selectAllColumn(String columnName,String columnType) {
		String[] result=null;
		ArrayList<String> resultA=new ArrayList<String>();
		try {
			connection=connect();
			
			String sql="SELECT * FROM "+tableName;
			
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(sql);
			
			while(results.next()) {
				switch(columnType) {
					case "int":
						resultA.add(String.valueOf(results.getInt(columnName)));
						break;
					case "double":
						resultA.add(String.valueOf(results.getDouble(columnName)));
						break;
					case "string":
						resultA.add(String.valueOf(results.getString(columnName)));
						break;
						
				}
			}
			
			result=new String[resultA.size()];
			result=resultA.toArray(result);
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public ArrayList<String[]> select(String[] columnsConditional,String[] values,String[] columnsRequired,String[] columnsTypes) {
		ArrayList<String[]> result=new ArrayList<String[]>();
		try {
			connection=connect();
			
			String sql="SELECT * FROM "+tableName+" WHERE (";
			
			for(int i=0;i<columnsConditional.length;i++) {
				sql+=columnsConditional[i]+" = "+values[i]+" ";
				if(i!=columnsConditional.length-1) {
					sql+="AND ";
				}
			}
			
			sql+=")";
			
			
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(sql);
			
			while(results.next()) {
				String[] resultSub=new String[columnsRequired.length];
				
				for(int i=0;i<columnsRequired.length;i++) {
					String resultReq=columnsRequired[i];
					String resultType=columnsTypes[i];
					
					switch(resultType) {
						case "int":
							resultSub[i]=String.valueOf(results.getInt(resultReq));
							break;
						case "double":
							resultSub[i]=String.valueOf(results.getDouble(resultReq));
							break;
						case "string":
							resultSub[i]=String.valueOf(results.getString(resultReq));
							break;
							
					}
				}
				
				result.add(resultSub);
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public boolean isTupleExists(String[] columnsConditional,String[] values) {
		boolean result=false;
		try {
			connection=connect();
			
			String sql="SELECT * FROM "+tableName+" WHERE (";
			
			for(int i=0;i<columnsConditional.length;i++) {
				sql+=columnsConditional[i]+" = "+values[i]+" ";
				if(i!=columnsConditional.length-1) {
					sql+="AND ";
				}
			}
			
			sql+=")";
			
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(sql);
			while(results.next()) {
				result=true;
				break;
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(connection!=null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	

}
