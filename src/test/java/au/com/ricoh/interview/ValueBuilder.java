package au.com.ricoh.interview;

public class ValueBuilder {
	private String id = new String();
	private String value = new String();
	
    public ValueBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ValueBuilder withValue(String value) {
        this.value = value;
        return this;
    }
    
    public Value build() {
    		return new Value(id, value);
    }
}
