package fis_control_trafico;

import interfaz.TraficoUI;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
//import net.sourceforge.jFuzzyLogic.rule.Rule;
//import net.sourceforge.jFuzzyLogic.rule.Variable;

public class Fis_control_trafico {
    
    public static void main(String[] args) {

        TraficoUI p = new TraficoUI();
        p.setVisible(true);

    }

    public String calcularluces(double trafico, double presencia_peatones,double distancia_semaforos) {
        
        // Carga el archivo de lenguaje de control difuso 'FCL'
        String fileName = "src/fis_congrol_trafico/FCL_control_trafico.fcl";
        FIS fis = FIS.load(fileName, true);
        
        // En caso de error
        if (fis == null) {
            System.err.println("No se puede cargar el archivo: '" + fileName + "'");
            return "";
        }
        
        // Establecer las entradas del sistema
        fis.setVariable("TRAFICO", trafico);
        fis.setVariable("presencia_peatones", presencia_peatones);
        fis.setVariable("distancia_semaforos", distancia_semaforos);
        
        // Inicia el funcionamiento del sistema
        fis.evaluate();

        // Muestra los gráficos de las variables de entrada y salida
        JFuzzyChart.get().chart(fis.getFunctionBlock("trafic"));
        
        /*
        // Muestra el conjunto difuso sobre el que se calcula el COG
        Variable tip = fis.getVariable("propina");
        JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
        */
             
        // Imprime el valor concreto de salida del sistema
        double salida1 = fis.getVariable("tiempo_luces_verdes").getLatestDefuzzifiedValue();
        double salida2 = fis.getVariable("tiempo_entre_verdes").getLatestDefuzzifiedValue();

        
        // Muestra cuanto peso tiene la variable de salida en cada CD de salida
        double pertenencia_corto = fis.getVariable("tiempo_luces_verdes").getMembership("corto");
        double pertenencia_muycorto= fis.getVariable("tiempo_luces_verdes").getMembership("muycorto");
        double pertenencia_largo = fis.getVariable("tiempo_luces_verdes").getMembership("largo");
        double pertenencia_muylargo = fis.getVariable("tiempo_luces_verdes").getMembership("muylargo");
        double pertenencia_corto2= fis.getVariable("tiempo_entre_verdes").getMembership("corto2");
        double pertenencia_muycorto2 = fis.getVariable("tiempo_entre_verdes").getMembership("muycorto2");
        double pertenencia_largo2 = fis.getVariable("tiempo_entre_verdes").getMembership("largo2");
        double pertenencia_muylargo2 = fis.getVariable("tiempo_entre_verdes").getMembership("muylargo2");
        
        String recomendacion = "";
        
        if (pertenencia_muycorto >= pertenencia_corto &&
                pertenencia_muycorto >= pertenencia_largo &&
                     pertenencia_muycorto >= pertenencia_muylargo){
            
            recomendacion = "muycorto";
        } else if (pertenencia_corto >= pertenencia_muycorto &&
                pertenencia_corto>= pertenencia_largo &&
                     pertenencia_corto >= pertenencia_muylargo){
            recomendacion = "corto";
        } else if (pertenencia_largo >= pertenencia_corto &&
                pertenencia_largo >= pertenencia_muycorto &&
                     pertenencia_largo >= pertenencia_muylargo){
            recomendacion = "largo";
        } else if (pertenencia_muylargo >= pertenencia_corto &&
                pertenencia_muylargo >= pertenencia_largo &&
                     pertenencia_muylargo >= pertenencia_muycorto){
            recomendacion = "muylargo";
        } else if (pertenencia_corto2 >= pertenencia_muycorto2 &&
                pertenencia_corto2 >= pertenencia_largo2 &&
                     pertenencia_corto2 >= pertenencia_muylargo2){
            recomendacion = "corto2";
        } else if (pertenencia_largo2 >= pertenencia_corto2 &&
                pertenencia_largo2 >= pertenencia_muycorto2 &&
                     pertenencia_largo2 >= pertenencia_muylargo2){
            recomendacion = "largo2";
        } else if (pertenencia_muylargo2 >= pertenencia_corto2 &&
                pertenencia_muylargo2 >= pertenencia_largo2 &&
                     pertenencia_muylargo2 >= pertenencia_muycorto2){
            recomendacion = "muylargo2";
        }  else if (pertenencia_muycorto2 >= pertenencia_corto2 &&
                pertenencia_muycorto>= pertenencia_largo2 &&
                     pertenencia_muycorto >= pertenencia_muylargo2){
            recomendacion = "muycorto2";
        }
        
        
        // Muestra las reglas activadas y el valor de salida de cada una despues de aplicar las operaciones lógicas
        StringBuilder reglasUsadas = new StringBuilder();
        reglasUsadas.append("Reglas Usadas:\n");
        fis.getFunctionBlock("trafic").getFuzzyRuleBlock("No1").getRules().stream().filter(r -> (r.getDegreeOfSupport() > 0)).forEachOrdered(r -> {
            reglasUsadas.append(r.toString()).append("\n");
        });
        
        return ("tiempo luces verdes: " + String.format("%.1f", salida1) + "%" +
               "\n" + "tiempo entre verdes: " + String.format("%.1f", salida2) + "%" +
               "\n\n" + "el tiempo entre luces verdes es " + recomendacion +
               "\n\n" + reglasUsadas.toString());

    }
    
}
