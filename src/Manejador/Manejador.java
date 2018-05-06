
package Manejador;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 *
 * @author diego
 */
public class Manejador {
    
    public DB getConexion() throws UnknownHostException{
        MongoClient MongoClient=new MongoClient("localhost",27017);
        DB db=MongoClient.getDB("video");
        return db;
    }
    
    public DBCollection obtenerColeccion() throws UnknownHostException{
    DBCollection coll=getConexion().getCollection("videos");
   return coll;    
    }
    
    public int numerodocumentos() throws UnknownHostException{
       DBCollection coll=getConexion().getCollection("videos");
       int num=(int) coll.count();
               return num; 
    }
    
    public void borrarDocumentos() throws UnknownHostException{
    
    DBCollection coll=getConexion().getCollection("similitudes");
    coll.drop();
        }
    
    
    
    public void insertarDocumento(int id1, int id2, double sim) throws UnknownHostException {
        //System.out.println("entro al metodo");
        BasicDBObject doc=new BasicDBObject();
        doc.append("id1", id1);
        doc.append("id2", id2);
        doc.append("similitud", sim);
        DBCollection coll1=getConexion().getCollection("similitudes");
        coll1.insert(doc);
        
    }
    
    public Vector obtenerTopRating(int umbralRating,DBObject query) throws UnknownHostException {
    DBCollection coll1=getConexion().getCollection("ratings");
    DBCursor cursor=coll1.find(query);
    Vector topratings=new Vector();    
    int i = 1;
    while (cursor.hasNext()){
        DBObject obj=cursor.next();
        topratings.add(obj.get("idPelicula"));
        i++;
    }    
    return topratings;
    }

    public Vector obtenerTopSimilitudes(Object idPelicula, double umbralSimilitud, DBObject query) throws UnknownHostException {
        DBCollection coll1=getConexion().getCollection("similitudes");    
        DBCursor cursor=coll1.find(query,new BasicDBObject("id2", true).append("id1", true).append("_id", false)); //de los docmentos encontrados, eliminamos _id de cada documento porque no lo necesitamos, y dejamos sólo id1 e id2
        Vector topsimilitudes=new Vector();
        int i=0;
        while (cursor.hasNext()){
            DBObject obj=cursor.next();
            System.out.println("Recomendacion: "+obj);
            topsimilitudes.add(obj);
            i++;
        }  
        return topsimilitudes;
    }
    
    
    
}
