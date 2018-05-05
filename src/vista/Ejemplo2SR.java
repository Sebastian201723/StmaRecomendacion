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

import op.Pelicula;
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
       	Vector<Pelicula> datos=new Vector();        
            while (cursor.hasNext()){
                    DBObject obj=cursor.next();
                    Pelicula p=new Pelicula();
                    p.setId((int) obj.get("_id"));
                    p.setTitulo((String) obj.get("titulo"));
                    String actores=(String) obj.get("actores").toString();
                    String[] tokens;
                    tokens = parsearTexto(actores); //eliminar llaves, comillas y comas
                    Vector temp=new Vector(); //vector para guardar actores

                        for (int cont = 0; cont < tokens.length; cont++){
                            temp.add(tokens[cont]);
                        }              
                    p.setActores(temp);
                    p.setGenero((String) obj.get("genero"));
                    p.setAno((int) obj.get("ano"));
                    datos.add(p); //aquí se va incluyendo la info de los contenidos en el vector datos
                    i++;    
            }
     man.borrarDocumentos();
     calcularSimilitud(datos);
     obtenerRecomendaciones(umbralRating,umbralSimilitud);
    }
    
    public static void calcularSimilitud(Vector<Pelicula> datos) throws UnknownHostException{
        int cont;
        Manejador man1=new Manejador();
        double sim; 
        for (int i = 0; i < datos.size(); i++) {           
            System.out.println("*****************************");
            for (int j = i+1; j < datos.size(); j++) {  //comparación entre una posición del vector y la siguiente, es decir, entre la información de un contenido y el siguiente              
                cont=0;
                if(datos.get(i).getTitulo().equals(datos.get(j).getTitulo())){    
                cont++; //registra las similitudes entre la información de un contenido y el siguiente
                }               
                for (int k = 0; k < datos.get(i).getActores().size(); k++) { //verificación cruzada entre actores, por eso un for dentro de otro
                    for (int l = 0; l < datos.get(j).getActores().size(); l++) {                       
                       if(datos.get(i).getActores().get(k).equals(datos.get(j).getActores().get(l))){                     
                           cont++; 
                       } 
                    }
                }
                if(datos.get(i).getGenero().equals(datos.get(j).getGenero())){
                    cont++;
                }
                if(datos.get(i).getAno()==datos.get(j).getAno()){
                    cont++;
                }              
                sim=(2.0*cont)/(3+datos.get(i).getActores().size()+3+datos.get(j).getActores().size());              
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
