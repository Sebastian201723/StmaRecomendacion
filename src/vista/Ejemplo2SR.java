package vista;
import Manejador.Manejador;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;
import org.json.simple.parser.JSONParser;

import op.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class Ejemplo2SR {
    public static void main(String[] args) throws UnknownHostException, ParseException {
        // Punto 12 establecer umbral similitud
        double umbralSimilitud=0.2;
        // Punto 13 establecer umbral rating
        int umbralRating=3;
        Manejador man = new Manejador();
        man.obtenerColeccion(); //obtener coleccion de descripciones
        int num=man.numerodocumentos();
        System.out.println("El numero de documentos es: "+num);
        DBCollection coll=man.obtenerColeccion(); //descripciones guardadas en coll
        DBCursor cursor = coll.find();
        int i = 1;
       	Vector<Video> datos=new Vector();        
            while (cursor.hasNext()){
                    DBObject obj=cursor.next();
                    Video p=new Video();
                    p.setId((int) obj.get("_id"));
                    System.out.println(p.getId());
                    p.setNombre((String) obj.get("nombre"));
                    System.out.println(p.getNombre());
                    p.setPuntuacion((double) obj.get("puntuacion"));
                    String competencia=(String) obj.get("competencia").toString();
                    
                    String[] tokens;
                    tokens = parsearTexto(competencia); //eliminar llaves, comillas y comas
                    Vector temp=new Vector(); //vector para guardar actores

                        for (int cont = 0; cont < tokens.length; cont++){
                            temp.add(tokens[cont]);
                        }              
                    p.setCompetencia(temp);
                    
                    datos.add(p); //aquí se va incluyendo la info de los contenidos en el vector datos
                    i++;    
            }
     man.borrarDocumentos();
     calcularSimilitud(datos);
     obtenerRecomendaciones(umbralRating,umbralSimilitud);
    }
    
    
    public static void calcularSimilitud(Vector<Video> datos) throws UnknownHostException{
        int cont;
        Manejador man1=new Manejador();
        //Vector de emociones de Usuario 1 para 6 videos de db Video
        double[] VectorEmocionesArray = new double[]{1,3,3.5,2,5,5};
        
        double sim; 
        for (int i = 0; i < datos.size(); i++) {           
            System.out.println("*****************************");
            for (int j = i+1; j < datos.size(); j++) {  //comparación entre una posición del vector y la siguiente, es decir, entre la información de un contenido y el siguiente              
                cont=0;
                if(datos.get(i).getNombre().equals(datos.get(j).getNombre())){    
                cont++; //registra las similitudes entre la información de un contenido y el siguiente
                }               
                for (int k = 0; k < datos.get(i).getCompetencia().size(); k++) { //verificación cruzada entre actores, por eso un for dentro de otro
                    for (int l = 0; l < datos.get(j).getCompetencia().size(); l++) {                       
                       if(datos.get(i).getCompetencia().get(k).equals(datos.get(j).getCompetencia().get(l))){                     
                           cont++; 
                       } 
                    }
                }
                if(datos.get(i).getPuntuacion()==datos.get(j).getPuntuacion()){
                    cont++;
                }        
                if (VectorEmocionesArray[i]>3){
                    sim=(2.0*cont)/(3+datos.get(i).getCompetencia().size()+3+datos.get(j).getCompetencia().size());  
                    }
                else 
                {
                    sim=0.64321*(2.0*cont)/(3+datos.get(i).getCompetencia().size()+3+datos.get(j).getCompetencia().size());                      
                }
              System.out.println("La similitud de "+datos.get(i).getId()+"|"+datos.get(j).getId()+" es "+sim);
             man1.insertarDocumento(datos.get(i).getId(),datos.get(j).getId(),sim);   //registra las similitudes en la colección similitudes        
            }
            System.out.println("*****************************");           
        }
    }

    private static String[] parsearTexto(String actores) {
      String actores4=actores.replaceAll("\"","");
      String actores5=actores4.replaceAll(" ","");
      String actores6=actores5.replace("[","");
      String actores7=actores6.replace("]","");
      String[] tokens;
      tokens = actores7.split(","); 
      return tokens;
    }

    private static void obtenerRecomendaciones(int umbralRating, double umbralSimilitud) throws UnknownHostException {
    Manejador man=new Manejador();
    DBObject query=new BasicDBObject("rating",new BasicDBObject("$gt",umbralRating)); //cadena de consulta, identificadores por encima de umbralRating
    Vector topratings=man.obtenerTopRating(umbralRating,query); //obtenerTopRating, los identificadores de los contenidos Top se almacenan en el vector toprating
    Vector recomendaciones=new Vector();
        for (int i = 0; i < topratings.size(); i++) {           
            Vector rec=new Vector();
            DBObject clause1 = new BasicDBObject("id1", topratings.get(i));
            DBObject clause2 = new BasicDBObject("id2", topratings.get(i));
            BasicDBList or = new BasicDBList();
            or.add(clause1);
            or.add(clause2);
            DBObject query1=new BasicDBObject("$or",or).append("similitud", new BasicDBObject("$gt",umbralSimilitud)); //cadena de consulta, identificadores en id1 o id2, y similitud por encima de umbralSimilitud
            rec=man.obtenerTopSimilitudes(topratings.get(i),umbralSimilitud,query1); //obtenerTopSimilitudes
            recomendaciones.add(rec);
        }         
    }  
}
