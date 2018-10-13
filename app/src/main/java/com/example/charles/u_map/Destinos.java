package com.example.charles.u_map;


public class Destinos {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    public String getId() { return id; }
    public final void setId(String newId) { id = newId; }

    @com.google.gson.annotations.SerializedName("Edificio")
    private String mEdificio;
    public String getEdificio() { return mEdificio; }
    public final void setEdificio(String newEdificio) { mEdificio = newEdificio; }

    @com.google.gson.annotations.SerializedName("Salon")
    private int mSalon;
    public int getSalon() { return mSalon; }
    public final void setSalon(int newSalon) { mSalon = newSalon; }


    @com.google.gson.annotations.SerializedName("Latitud")
    private double mLatitud;
    public double getLatitud() { return mLatitud; }
    public final void setLatitud(double newLatitud) { mLatitud = newLatitud; }

    @com.google.gson.annotations.SerializedName("Longitud")
    private double mLongitud;
    public double getLongitud() { return mLongitud; }
    public final void setLongitud(double newLongitud) { mLongitud = newLongitud; }

    public Destinos() { }

    public Destinos(String edificio, int salon, double latitud, double longitud, String id){
        this.setEdificio(edificio);
        this.setSalon(salon);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.setId(id);
    }
}
