
package es.espinr.gijonair;


public class AirStation
{

    private String ica[];
    private String indicadores[];
    private String label;
    private String time;
    private String valores[];

    public AirStation(String label, String time, String indicators[], String values[], String icas[])
    {
        this.label = label;
        this.time = time;
        this.valores = values;
        this.ica = icas;
        this.indicadores = indicators;
    }


    public String[] getIca()
    {
        return ica;
    }

    public String[] getIndicadores()
    {
        return indicadores;
    }

    public String getLabel()
    {
        return label;
    }

    public String getTime()
    {
        return time;
    }

    public String[] getValores()
    {
        return valores;
    }

    public void setIca(String as[])
    {
        ica = as;
    }

    public void setIndicadores(String as[])
    {
        indicadores = as;
    }

    public void setLabel(String s)
    {
        label = s;
    }

    public void setTime(String s)
    {
        time = s;
    }

    public void setValores(String as[])
    {
        valores = as;
    }
}
