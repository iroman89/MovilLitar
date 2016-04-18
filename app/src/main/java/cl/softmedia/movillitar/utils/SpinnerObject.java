package cl.softmedia.movillitar.utils;

public class SpinnerObject {

    private int dataBaseId;
    private String dataBaseValue;

    public SpinnerObject(int dataBaseId, String dataBaseValue) {

        this.dataBaseId = dataBaseId;
        this.dataBaseValue = dataBaseValue;
    }

    public int getDataBaseId() {
        return dataBaseId;
    }

    public String getDataBaseValue() {
        return dataBaseValue;
    }

    @Override
    public String toString() {
        return dataBaseValue;
    }

}
