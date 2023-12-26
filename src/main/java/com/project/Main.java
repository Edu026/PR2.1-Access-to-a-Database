package com.project;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Aquest exemple mostra com fer una 
 * connexió a SQLite amb Java
 * 
 * A la primera crida, crea l'arxiu 
 * de base de dades hi posa dades,
 * després les modifica
 * 
 * A les següent crides ja estan
 * originalment modificades
 * (tot i que les sobreescriu cada vegada)
 */

public class Main {

    static Scanner in = new Scanner(System.in); // System.in és global, Scanner també ho a de ser

    public static void main(String[] args) throws SQLException {
        String basePath = System.getProperty("user.dir") + "/data/";
        String filePath = basePath + "database.db";
        ResultSet rs = null;

        // Si no hi ha l'arxiu creat, el crea i li posa dades
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) { initDatabase(filePath); }
        
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        boolean running = true;
        boolean mostrarTabla = false;
        boolean mostrarPersonatge = false;


        while (running){
            String menu = "Escull una opcio: " + "\n";
            menu = menu + "1) Mostrar una taula " + "\n";
            menu = menu + "2) Mostrar personatges per faccio" + "\n";
            menu = menu + "3) Mostrar millor atacant per faccio" + "\n";
            menu = menu + "4) Mostrar millor defensor per faccio" + "\n";
            menu = menu + "5) Sortir" + "\n";

            System.out.println(menu);

            int opcio = Integer.valueOf(llegirLinia("Opcio:"));

            switch (opcio) {
                case 1: mostrarTabla = true; break;
                case 2: mostrarPersonatge = true; break;
                case 3: System.out.println( mostrarMillorsAtacants(conn)); break;
                case 4: System.out.println(mostrarMillorsDefensors(conn)); break;
                case 5: running = false;  break;
            
                default: break;
            }

            if (mostrarTabla == true){
                System.out.println("1) Faccio" + "\n" + "2) Personatge" + "\n" + "3) Sortir");
                opcio = Integer.valueOf(llegirLinia("Opcio:"));

                switch (opcio) {
                    case 1: System.out.println(showTable(conn, "Faccio")); mostrarTabla = false; break;
                    case 2: System.out.println(showTable(conn, "Personatge")); mostrarTabla = false;  break;
                    case 3: mostrarTabla = false; break;
                    default: break;
                }
            }

            if (mostrarPersonatge == true){
                System.out.println("1) Knights" + "\n" + "2) Samurais" + "\n" + "3) Vikings" + "\n" + "4) Sortir");
                opcio = Integer.valueOf(llegirLinia("Opcio:"));

                switch (opcio) {
                    case 1: System.out.println(mostrarPersonatgesFaccio(conn, 1)); mostrarPersonatge = false; break;
                    case 2: System.out.println(mostrarPersonatgesFaccio(conn, 2)); mostrarPersonatge = false; break;
                    case 3: System.out.println(mostrarPersonatgesFaccio(conn, 3)); mostrarPersonatge = false; break;
                    case 4: mostrarPersonatge = false; break;
                    default: break;
                }
            }

        }

    
        // Llistar les taules
/*         ArrayList<String> taules = UtilsSQLite.listTables(conn);
        System.out.println(taules); */
        

        // Demanar informació de la taula

/*         rs = UtilsSQLite.querySelect(conn, "SELECT * FROM Faccio;");
        ResultSetMetaData rsmd = rs.getMetaData();
        System.out.println("Informacio de la taula:");
        for (int cnt = 1; cnt < rsmd.getColumnCount(); cnt = cnt + 1) { 
            // Les columnes començen a 1, no hi ha columna 0!
            String label = rsmd.getColumnLabel(cnt);
            String name = rsmd.getColumnName(cnt);
            int type = rsmd.getColumnType(cnt);
            System.out.println("    " + label + ", " + name + ", " + type);
        }

        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM Personatge;");
        System.out.println("Contingut de la taula:");
        while (rs.next()) {
            System.out.println("Personatge: " + rs.getInt("id") + ", " + rs.getString("nom"));
        } */
        

        /* // Actualitzar una fila
        UtilsSQLite.queryUpdate(conn, "UPDATE warehouses SET name=\"MediaMarkt\" WHERE id=2;");

        // Esborrar una fila
        UtilsSQLite.queryUpdate(conn, "DELETE FROM warehouses WHERE id=3;");

        // SELECT a la base de dades
        rs = UtilsSQLite.querySelect(conn, "SELECT * FROM warehouses;");
        System.out.println("Contingut de la taula modificada:");
        while (rs.next()) {
            System.out.println("    " + rs.getInt("id") + ", " + rs.getString("name"));
        } */
        
        // Desconnectar

        UtilsSQLite.disconnect(conn);
    }


    static public String llegirLinia (String text) {
        System.out.print(text);
        return in.nextLine();
    }

    static public String showTable(Connection conn, String tableName){
        ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT * FROM " + tableName + " ;");
        String table = "";
        System.out.println("Contingut de la taula:");
        if (tableName.equalsIgnoreCase("Faccio")){
            try {
                System.out.println(String.format("%-10s %-14s %-14s", "ID","NOM","RESUM"));
                while (rs.next()) {
                    table = table + String.format("%-10d %-14s %-14s",rs.getInt("id"), rs.getString("nom") , rs.getString("resum")) +  "\n";
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        else if (tableName.equalsIgnoreCase("Personatge")){ // id,nom,atac,defensa,idFaccio
            try {
                System.out.println(String.format("%-10s %-14s %-14s %-14s %-14s", "ID","NOM","ATAC","DEFENSA","IDFACCIO"));
                while (rs.next()) {
                    table = table + String.format("%-10s %-14s %-14f %-14f %-14d",rs.getInt("id"), rs.getString("nom") , rs.getFloat("atac"), rs.getFloat("defensa"),
                    rs.getInt("idFaccio") ) +  "\n";
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return table;
    }

    static String mostrarPersonatgesFaccio(Connection conn, int idfaccio ){

        ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT nom FROM Faccio WHERE id = " + idfaccio);
        String table = "";
        String nomFaccio = "";
        System.out.println("PERSONATGES FACCIO:");
        try {
                nomFaccio =  nomFaccio =  rs.getString("nom");
                rs = UtilsSQLite.querySelect(conn, "SELECT * FROM Personatge WHERE idFaccio = " + idfaccio);
                System.out.println(String.format("%-10s %-14s %-14s %-14s %-14s %-14s", "ID","NOM","ATAC","DEFENSA","IDFACCIO","NOMFACCIO"));
                while (rs.next()) {
                    table = table + String.format("%-10s %-14s %-14f %-14f %-14d %-14s",rs.getInt("id"), rs.getString("nom") , rs.getFloat("atac"), rs.getFloat("defensa"),
                    rs.getInt("idFaccio"), nomFaccio ) +  "\n";
                }
                
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        return table;
    }

    static String mostrarMillorsAtacants(Connection conn){

        ResultSet rs = null;
        String table = "";
        System.out.println("MILLORS ATACANTS PER FACCIO:");
        try {
            System.out.println(String.format("%-10s %-14s %-14s %-14s %-14s", "ID","NOM","ATAC","DEFENSA","IDFACCIO"));
            for (int i = 1; i <= 3; i++){
                rs = UtilsSQLite.querySelect(conn, "SELECT * FROM Personatge WHERE idFaccio = " + i + " ORDER BY atac DESC LIMIT 1");
                table = table + String.format("%-10s %-14s %-14f %-14f %-14d",rs.getInt("id"), rs.getString("nom") , rs.getFloat("atac"), rs.getFloat("defensa"),
                rs.getInt("idFaccio")) +  "\n";
            }
                
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        
        return table;

    } 

    static String mostrarMillorsDefensors(Connection conn){

    ResultSet rs = null;
    String table = "";
    System.out.println("MILLORS DEFENSORS PER FACCIO:");
    try {
        System.out.println(String.format("%-10s %-14s %-14s %-14s %-14s", "ID","NOM","ATAC","DEFENSA","IDFACCIO"));
        for (int i = 1; i <= 3; i++){
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM Personatge WHERE idFaccio = " + i + " ORDER BY defensa DESC LIMIT 1");
            table = table + String.format("%-10s %-14s %-14f %-14f %-14d",rs.getInt("id"), rs.getString("nom") , rs.getFloat("atac"), rs.getFloat("defensa"),
            rs.getInt("idFaccio")) +  "\n";
        }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    return table;

} 

    static void initDatabase (String filePath) {
        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar la taula (per si existeix)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Faccio;");
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Personatge;");

        // Crear una nova taula
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS Faccio ("
                                    + "	id integer PRIMARY KEY AUTOINCREMENT,"
                                    + "	nom VARCHAR(15) NOT NULL,"
                                    + "resum VARCHAR(500));");

        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS Personatge ("
                                    + "id integer PRIMARY KEY AUTOINCREMENT,"
                                    + "nom VARCHAR(15) NOT NULL,"
                                    + "atac DOUBLE," 
                                    + "defensa DOUBLE," 
                                    + "idFaccio int,"
                                    + "FOREIGN KEY (idFaccio) REFERENCES Faccio(id)"
                                    + ");");
                

        // Afegir elements a la taula Faccio
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (id,nom,resum) VALUES (1,\"Knights\",\"It is their belief that many, if not all of the ancient ruins were constructed by their ancestors.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (id,nom,resum) VALUES (2,\"Samurai\",\" Previously thought to have vanished, the Vikings have returned - in great numbers - from across the sea.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (id,nom,resum) VALUES (3,\"Viking\",\"These people come from a land, far to the East and they tell a tale of a homeland and an Emperor that were lost to sea and fire during the great cataclysm. \");");

        // Afegir elements a la taula Personatge 
        //KNIGHTS 
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (1,\"Warden\"," + 75.0 + ", " + 80.0 + ", " + 1 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (2,\"Conqueror\"," + 70.0 + ", " + 90.0 + ", " + 1 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (3,\"Pacekeeper\"," + 78.0 + ", " + 60.0 + ", " + 1 +");");

        //SAMURAIS 
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (4,\"Kensei\"," + 85.0 + ", " + 70.0 + ", " + 2 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (5,\"Shugoki\"," + 65.0 + ", " + 100.0 + ", " + 2 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (6,\"Orochi\"," + 65.0 + ", " + 70.0 + ", " + 2 +");");

        //VIKINGS  
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (7,\"Raider\"," + 96.0 + ", " + 70.0 + ", " + 3 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (8,\"Warlord\"," + 55.0 + ", " + 120.0 + ", " + 3 +");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (id,nom,atac,defensa,idFaccio) VALUES (9,\"Berserker\"," + 80.0 + ", " + 40.0 + ", " + 3 +");");

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
}