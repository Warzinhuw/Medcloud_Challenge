package lib.DAO;

import lib.Models.Patient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PatientDAO {

    private static Connection conn = ConnectionDB.getConn();
    private static Connection replicaConn = ReplicaDB.getConn();
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    private static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public static void insertPatient(Patient patient) {
        try{
            String statement = "INSERT INTO patient(name, birth_date, email, address) VALUES (?,str_to_date(?,'%d/%m/%Y'),?,?)";
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement(statement);
                stmt.setString(1, patient.getName());
                stmt.setString(2, patient.getBirthDate());
                stmt.setString(3, patient.getEmail());
                stmt.setString(4, patient.getAddress());
                stmt.execute();
            }catch (SQLException e){
                e.printStackTrace();
            }
            finally {
                assert stmt != null;
                stmt.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void generateXML(){
        ResultSet result;
        PreparedStatement stmt;
        try{
            stmt = conn.prepareStatement("SELECT * FROM `patient`");
            result = stmt.executeQuery();
            Element root = new Element("PatientsList");
            Document doc = new Document();
            while(result.next()){
                Element header = new Element("patient").setAttribute("id", String.valueOf(result.getInt("id")));

                Element elemName = new Element("name");
                elemName.addContent(result.getString("Name"));
                header.addContent(elemName);

                Element elemEmail = new Element("email");
                elemEmail.addContent(result.getString("Email"));
                header.addContent(elemEmail);

                Element eleBirthDate = new Element("birthDate");
                Date sqlDate = sqlDateFormat.parse(result.getString("birth_date"));
                eleBirthDate.addContent(newDateFormat.format(sqlDate)); //convert data from yyyy-MM-dd to dd/MM/yyyy
                header.addContent(eleBirthDate);

                Element eleAddress = new Element("address");
                eleAddress.addContent(result.getString("address"));
                header.addContent(eleAddress);

                root.addContent(header);
            }
            doc.setRootElement(root);
            XMLOutputter outter = new XMLOutputter();
            outter.setFormat(Format.getPrettyFormat());
            outter.output(doc, new FileWriter("patients.xml"));

            System.out.println(ANSI_GREEN + "XML generated successfully" + ANSI_RESET);

            stmt.close();
        } catch (SQLException | IOException | ParseException throwables) {
            throwables.printStackTrace();
        }
        replicateData();
    }

    private static void replicateData(){
        String fileName = "patients.xml";

        try {
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(new File(fileName));

            Element rootNode = doc.detachRootElement();
            List<Element> list = rootNode.getChildren("patient");

            for (Element target : list){
                String id = target.getAttributeValue("id");
                String name = target.getChildText("name");
                Date date = newDateFormat.parse(target.getChildText("birthDate"));
                Long timestamp = date.getTime();
                insertReplicaData(id, name, timestamp);
            }

            System.out.println(ANSI_GREEN+"Replica data stored successfully"+ANSI_RESET+"\n");

        } catch (IOException | JDOMException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void insertReplicaData(String id, String name, Long timestamp){
        try{
            String statement = "REPLACE INTO patient(id, name, timestamp) VALUES (?,?,?)";
            PreparedStatement stmt = null;
            try {
                stmt = replicaConn.prepareStatement(statement);
                stmt.setInt(1, Integer.parseInt(id));
                stmt.setString(2, name);
                stmt.setLong(3, timestamp);
                stmt.execute();
            }catch (SQLException e){
                e.printStackTrace();
            }
            finally {
                assert stmt != null;
                stmt.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}