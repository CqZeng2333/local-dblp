package connect_database;

public class Document {
	private String doc_type;  
    private String authors;  
    private String doc_title;  
    private int doc_year;  
    private String doc_journal;
	private String doc_ee;
	public static class Builder{ 
	    private String authors = "";
	    private String doc_title = "";
	    private int doc_year = 0;
	    private String doc_journal = "";
		private String doc_ee = "";
		
		public Builder authors(String authors) {  
	        this.authors = authors;
	        return this;
	    }  
		public Builder title(String doc_title) {  
	        this.doc_title = doc_title;
	        return this;
	    }  
		public Builder year(int doc_year) {  
	        this.doc_year = doc_year;
	        return this;
	    }
		public Builder journal(String doc_journal) {
			this.doc_journal = doc_journal;
			return this;
		}
		public Builder ee(String doc_ee) {
			this.doc_ee = doc_ee;
			return this;
		}
		public Document build() {
			return new Document(this);
		}
	}
      
	public Document(){  
        this.authors = "";  
        this.doc_title = "";  
        this.doc_year = 0;  
    }
	
	public Document(Builder builder) {
		this.authors = builder.authors;  
        this.doc_title = builder.doc_title;  
        this.doc_year = builder.doc_year; 
        this.doc_journal = builder.doc_journal;
        this.doc_ee = builder.doc_ee;
	}
	
    public String getDoc_type() {  
        return doc_type;  
    }  
  
    public void setDoc_type(String doc_type) {  
        this.doc_type = doc_type;  
    }  
  
    public String getAuthors() {  
        return authors;  
    }  
  
    public void setAuthors(String authors) {  
        this.authors = authors;  
    }  
  
    public String getDoc_title() {  
        return doc_title;  
    }  
  
    public void setDoc_title(String doc_title) {  
        this.doc_title = doc_title;  
    }  
  
    public int getDoc_year() {  
        return doc_year;  
    }  
  
    public void setDoc_year(int doc_year) {  
        this.doc_year = doc_year;  
    } 
    
    public String getDoc_journal() {
		return doc_journal;
	}
    
	public void setDoc_journal(String doc_journel) {
		this.doc_journal = doc_journel;
	}
    
    public String getDoc_ee() {
		return doc_ee;
	}
    
	public void setDoc_ee(String doc_ee) {
		this.doc_ee = doc_ee;
	}

	@Override
    public String toString(){  
        return "#title: " + doc_title + " #authors: " + authors +
        		" #year: " + doc_year + " #journal: " + doc_journal +
        		" #details: " + doc_ee;  
    }  
}
