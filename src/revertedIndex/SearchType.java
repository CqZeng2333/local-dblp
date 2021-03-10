package revertedIndex;

public enum SearchType {
	title("title"), author("author"), journal("journal");
	private String type;
	
	SearchType(String type) {
		this.type = new String(type);
	}
	
	public String getType() {
		return type;
	}
}
