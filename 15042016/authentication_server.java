package com.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


// File name & File data

// http://www.rgagnon.com/javadetails/java-0542.html
public class authentication_server {
    public static void main(String[] args) throws Exception {

        /** VARIABLES DECLARATION **/
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        ServerSocket serverSocket;
        Socket clientSocket;


        /** SOCKETS **/
        serverSocket = new ServerSocket(4322);    // Open up port number, ready to take input. One port, one server
        System.out.println("(... Waiting for clients...)");

        clientSocket = serverSocket.accept();
        System.out.println("\nNew Client Connection From :" + clientSocket.getRemoteSocketAddress());

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        // TODO : Receive client's request for public key
        // TODO : M = Ks-{"Hello, this is SecStore"}

        /** SEND SIGNED CERTIFICATE **/

        // TODO : Send signed certificate
        // String fileToSend = "C:\\Users\\surface\\Desktop\\NSproject2\\Server_SendFiles\\SignedCertificate_1001198.crt";
        String fileToSend = "C:\\Users\\surface\\Desktop\\NSproject2\\Server_SendFiles\\SignedCertificate_1001198.crt";

        File sendSignedCert = new File (fileToSend);
        byte [] bytearray  = new byte [(int)sendSignedCert.length()];                           // Create byte array of cert's size
        bufferedInputStream = new BufferedInputStream(new FileInputStream(sendSignedCert));
        int abc = bufferedInputStream.read(bytearray,0,bytearray.length);
         System.out.println(new String(bytearray));
        outputStream = clientSocket.getOutputStream();
        outputStream.write(bytearray,0,bytearray.length);
        outputStream.flush();
        bufferedInputStream.close();
        outputStream.close();
        //clientSocket.shutdownInput();


        System.out.println("Sent Signed Certificate");

        // TODO : Receive files from client

        boolean AbleToReceive = true;

        /*InputStream is = clientSocket.getInputStream();
        //FileOutputStream fos = new FileOutputStream(fileReceived);
        //BufferedOutputStream bos = new BufferedOutputStream(fos);

        System.out.println("Receiving");

        byte[] mybytearray = new byte[1024];
        while(is.read(mybytearray) > 0) {
            System.out.println("Received: " + new String(mybytearray));
            // System.out.println("Received length: " + mybytearray.length);
            //bos.write(mybytearray);
            mybytearray = new byte[1024];
        }

        System.out.println("Done Receiving small text");
//        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
//         System.out.println(new String(mybytearray));
//        bos.write(mybytearray, 0, bytesRead);
        //bos.close();
        //fos.close();
*/

    }
}
