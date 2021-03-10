package servlet;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connect_database.Document;
import connect_database.Selector;
import revertedIndex.Index;
import revertedIndex.SearchType;

@WebServlet("/SearchServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MyServlet() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=utf-8");
		
		String query = req.getParameter("query");
		req.getSession().setAttribute("query", query);
		String searchType = req.getParameter("searchType");
		req.getSession().setAttribute("searchType", searchType);
		Index index = null;
		if (searchType.equals("title")) {
			index = Selector.getIndex(SearchType.title, null);
		}
		else if (searchType.equals("author")) {
			index = Selector.getIndex(SearchType.author, null);
		}
		else if (searchType.equals("journal")) {
			index = Selector.getIndex(SearchType.journal, req.getParameter("journalType"));
		}
		index.countAll();
		TreeMap<Double, Integer> topK = index.getTopK(10, index.getQuery(query));
		TreeMap<Double, Document> result = Selector.getDoc(topK);
		req.getSession().setAttribute("docSet", result);
		resp.sendRedirect("index.jsp");
	}
}
