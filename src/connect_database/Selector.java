package connect_database;

import java.sql.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import revertedIndex.Index;
import revertedIndex.SearchType;

public class Selector {
	private static Connection conn = Connector.getConn();
    
	/*
	 * Get all the records containing title
	 * and id into result set
	 * return: the result set
	 */
    private static ResultSet getAll() {
    	Statement stmt = null;
		ResultSet rset = null;
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery("SELECT id, title FROM dblp;");
			return rset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return rset;
    }
    
    /*
	 * Get all the records containing author
	 * and id  into result set
	 * para: author - the authors to find
	 * return: the result set
	 */
    private static ResultSet getAuthor() {
    	Statement stmt = null;
		ResultSet rset = null;
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery("SELECT id, author FROM dblp;");
			return rset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return rset;
    }
    
    /*
	 * Get all the records containing title
	 * and id with this journal into result set
	 * para: journal - the journal to find
	 * return: the result set
	 */
    private static ResultSet getJournal(String journal) {
    	Statement stmt = null;
		ResultSet rset = null;
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rset = stmt.executeQuery("SELECT id, title FROM dblp "
					+ "WHERE journal LIKE '%" + journal + "%';");
			return rset;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return rset;
    }
    
    /*
	 * Get the index according to search type
	 * and the queries to search
	 * para: type - searching type, including
	 * 				"title", "author" and "journal"
	 * 		 query - journal to search
	 * return: the result set, or null if no record
	 */
    public static Index getIndex(SearchType st, String query) {
    	String type = st.getType();
    	if (type.equals("title")) {
    		ResultSet rset = getAll();
			try {
				//at least one record in database
				if (rset.next()) {
					Index index = new Index();
					index.add(rset.getString("title"), rset.getInt("id"));
					while (rset.next()){
						index.add(rset.getString("title"), rset.getInt("id"));
					}
					return index;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	else if (type.equals("author")) {
    		ResultSet rset = Selector.getAuthor();
			try {
				//at least one record with this author in database
				if (rset.next()) {
					Index index = new Index();
					index.add(rset.getString("author"), rset.getInt("id"));
					while (rset.next()){
						index.add(rset.getString("author"), rset.getInt("id"));
					}
					return index;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	else if (type.equals("journal")) {
    		ResultSet rset = Selector.getJournal(query);
			try {
				if (rset.next()) {
					//at least one record with this author in database
					Index index = new Index();
					index.add(rset.getString("title"), rset.getInt("id"));
					while (rset.next()){
						index.add(rset.getString("title"), rset.getInt("id"));
					}
					return index;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    	//no record
    	return null;
    }
    
    /*
     * Get a map of rank, document
     * according to document id
     * para: id - a TreeMap of scores->document id
     * return: a TreeMap of scores->document information
     */
    public static TreeMap<Double, Document> getDoc(TreeMap<Double, Integer> id) {
    	Statement stmt = null;
		ResultSet rset = null;
		String str = null;
		TreeMap<Double, Document> docSet = new TreeMap<>(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o2.compareTo(o1);
			}});
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			str = "SELECT * FROM dblp WHERE id = ";
			Iterator<Double> iter = id.keySet().iterator();
			Double d;
			while (iter.hasNext()) {
				d = iter.next();
				rset = stmt.executeQuery(str + id.get(d).toString());
				if (rset.next()) {
					Document doc = new Document.Builder()
									.title(rset.getString("title"))
									.authors(rset.getString("author"))
									.year(rset.getInt("year"))
									.journal(rset.getString("journal"))
									.ee(rset.getString("url")).build();
					docSet.put(d, doc);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return docSet;
    }
    
    /*
     * Get a map of docID - year gap between the 
     * published year of the doc and 2018
     * according to document id
     * para: id - a Set of document id
     * return: a TreeMap of docID->year gap
     */
    public static TreeMap<Integer, Integer> getYearGap(Collection<Integer> ID) {
    	Statement stmt = null;
		ResultSet rset = null;
		String str = null;
		TreeMap<Integer, Integer> yearGap = new TreeMap<>();
    	try {
    		stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			str = "SELECT year FROM dblp WHERE id = ";
			Iterator<Integer> iter = ID.iterator();
			Integer id;
			while (iter.hasNext()) {
				id = iter.next();
				rset = stmt.executeQuery(str + id.toString());
				if (rset.next()) {
					yearGap.put(id, 2018 - Integer.parseInt(rset.getString("year")));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return yearGap;
    }
}
