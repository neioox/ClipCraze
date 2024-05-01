package de.neiox.utls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class requestHandler {

    private String Auth = "Bearer dh9c9di6800h6yg637svdyxv760322";
    private String CliendID = "a21zjh9htub57nqweuoe5ug197eodg";
    public String getRequest(String url) {
        try {
            URL http = new URL(url);
            HttpURLConnection con = (HttpURLConnection) http.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", CliendID);
            con.setRequestProperty("Authorization", Auth);

            int status = con.getResponseCode();
          //  if (status == 200) {


                System.out.println(
                        con.getResponseMessage());

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    System.out.println(line);
                }

                reader.close(); // Close the reader when you're done with it.
                System.out.println("Sex");
                System.out.println(response);
                return response.toString();
         //   }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);

        }
    }





}
