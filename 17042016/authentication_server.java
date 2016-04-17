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
import java.util.stream.Stream;


// File name & File data

// http://www.rgagnon.com/javadetails/java-0542.html
//http://stackoverflow.com/questions/7805266/how-can-i-reopen-a-closed-inputstream-when-i-need-to-use-it-2-times
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
        InputStream is = clientSocket.getInputStream();

        // TODO : Receive client's request for public key
        // TODO : M = Ks-{"Hello, this is SecStore"}

        /** SEND SIGNED CERTIFICATE **/

        // TODO : Send signed certificate
        // String fileToSend = "C:\\Users\\surface\\Desktop\\NSproject2\\Server_SendFiles\\SignedCertificate_1001198.crt";
        String fileToSend = "C:\\Users\\surface\\Desktop\\NSproject2\\Server_SendFiles\\SignedCertificate_1001198.crt";

        File sendSignedCert = new File (fileToSend);
        byte [] bytearray  = new byte [(int)sendSignedCert.length()];                           // Create byte array of cert's size
        bufferedInputStream = new BufferedInputStream(new FileInputStream(sendSignedCert));
        bufferedInputStream.read(bytearray,0,bytearray.length);
        System.out.println(new String(bytearray));
        outputStream = clientSocket.getOutputStream();
        outputStream.write(bytearray, 0, bytearray.length);
        outputStream.flush();
        bufferedInputStream.close();
        outputStream.close();

        //clientSocket.shutdownInput();


        System.out.println("Sent Signed Certificate");


        // At this point socket is closed
        // TODO : Receive files from client
        String fileReceived = "C:\\Users\\surface\\Desktop\\NSproject2\\Server_ReceivedFiles\\received.txt";

        if (clientSocket.isClosed()){
            System.out.println("\nClient closed before able to receive data");
        }

        FileOutputStream fos = new FileOutputStream(fileReceived);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        while (is.available()!=0){
            byte[] byteArrayReceived = new byte[1024];
            while(is.read(byteArrayReceived) > 0) {
                int bytesRead = is.read(byteArrayReceived, 0, byteArrayReceived.length);
                System.out.println(new String(byteArrayReceived));
                bos.write(byteArrayReceived, 0, bytesRead);
            }
        }


        bos.close();
        fos.close();

        System.out.println("Done Receiving small text");

    }
}
