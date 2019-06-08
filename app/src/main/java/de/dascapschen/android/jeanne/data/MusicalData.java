package de.dascapschen.android.jeanne.data;

public abstract class MusicalData
{
    protected int id;
    protected String name;

    MusicalData(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public abstract String getDescriptionTitle();
    public abstract String getDescriptionSubtitle();
    /*TODO: public abstract ? getImage(); */
}
