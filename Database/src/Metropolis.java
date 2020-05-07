public class Metropolis {
    private String metropolis;
    private String continent;
    private String population;


    public static final String []columnNames =  {"metropolis","continent","population"};

    public Metropolis(String name, String continent, String population){
        this.metropolis = name.trim();
        this.continent = continent.trim();
        this.population = population.trim();
    }

    public Metropolis(Metropolis metropolis){
        this(metropolis.getMetropolis(), metropolis.getContinent(), metropolis.getPopulation());
    }

    public String getPopulation(){
        return population;
    }

    public String getMetropolis(){
        return metropolis;
    }

    public String getContinent(){
        return continent;
    }
    public Object getByColumn(int col){
        switch (col){
            case 0:
                return getMetropolis();
            case 1:
                return getContinent();
            case 2:
                return getPopulation();
            default:
                return null;
        }
    }
    public void setPopulation(String val){
        population = val;
    }

    public void setMetropolis(String val){

        metropolis = val;
    }

    public void setContinent(String val){

        continent = val;
    }
}
