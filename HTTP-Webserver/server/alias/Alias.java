package server.alias;

import java.util.Objects;

public class Alias {
    
    private final String absolutePath;
    
    
    public Alias(String absolutePath) {
        this.absolutePath = absolutePath;
    }
    
    
    public String getAbsolutePath() {
        return absolutePath;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alias alias = (Alias) o;
        return getAbsolutePath().equals(alias.getAbsolutePath());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getAbsolutePath());
    }
}
