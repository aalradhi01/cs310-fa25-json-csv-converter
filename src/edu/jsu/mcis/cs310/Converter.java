package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Parse CSV from the input string using OpenCSV
            CSVReader reader = new CSVReader(new java.io.StringReader(csvString));
            java.util.List<String[]> rows = reader.readAll();
            if (rows == null || rows.isEmpty()) return result;

            // Header row -> "ColHeadings"
            String[] headers = rows.get(0);
            JsonArray colHeadings = new JsonArray();
            for (String h : headers) colHeadings.add(h);

            // Build "ProdNums" and "Data"
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            for (int r = 1; r < rows.size(); r++) {
                String[] row = rows.get(r);
                if (row == null || row.length == 0) continue;

                // First column is ProdNum
                prodNums.add(row[0]);

                // Remaining columns (typed) go into a row array
                JsonArray outRow = new JsonArray();
                // Title
                outRow.add(row[1]);  
                // Season (int)
                outRow.add(Integer.parseInt(row[2].trim()));  
                // Episode (int)
                outRow.add(Integer.parseInt(row[3].trim())); 
                // Stardate
                outRow.add(row[4]); 
                // OriginalAirdate
                outRow.add(row[5]);                                
                // RemasteredAirdate
                outRow.add(row[6]);                                   
                data.add(outRow);
            }

            // Create the final JSON object and serialize it utilizing the library.
            JsonObject root = new JsonObject();
            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);

            result = Jsoner.serialize(root);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        // default return value; replace later!
        String result = ""; 
        
        try {
            //Utilizing json-simple, parse JSON content into objects or arrays.
            JsonObject root = Jsoner.deserialize(jsonString, new JsonObject());
            JsonArray prodNums    = (JsonArray) root.get("ProdNums");
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray data        = (JsonArray) root.get("Data");

            // Create a CSV writer that normalizes line endings and quotes all fields.
            java.io.StringWriter sw = new java.io.StringWriter();
            CSVWriter writer = new CSVWriter(
                sw, ',', '"', CSVWriter.DEFAULT_ESCAPE_CHARACTER, "\n"
            );

            // Header row (quote all fields to ensure exact match with given CSV)
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) header[i] = colHeadings.get(i).toString();
            writer.writeNext(header, true); // applyQuotesToAll = true

           // ProdNum + remaining fields in data rows; episode zero-padded to two digits
            for (int i = 0; i < data.size(); i++) {
                JsonArray row = (JsonArray) data.get(i);
                String[] out = new String[header.length];

                int k = 0;
                // ProdNum
                out[k++] = prodNums.get(i).toString();          
                // Title
                out[k++] = row.get(0).toString();              

                long season  = ((Number) row.get(1)).longValue();
                long episode = ((Number) row.get(2)).longValue();
                out[k++] = Long.toString(season);               
                out[k++] = String.format("%02d", episode);     
                 // Stardate
                out[k++] = row.get(3).toString();              
                // OriginalAirdate
                out[k++] = row.get(4).toString();               
               // RemasteredAirdate
                out[k++] = row.get(5).toString();               

                // quote all fields
                writer.writeNext(out, true); // quote all fields
            }

            writer.flush();
            result = sw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
