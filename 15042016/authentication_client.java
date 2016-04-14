package com.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

// Problem is not about sending large files, it's about encrypting large files

// Explanation : https://msdn.microsoft.com/en-us/library/windows/desktop/aa376502(v=vs.85).aspx
// http://coding.westreicher.org/?p=23
// http://www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm
public class authentication_client {

    public static void main(String[] args) throws IOException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        /** VARIABLE DECLARATION**/
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;

        String hostName = "localhost";                          // IPv6 Address or IPv4 Address
        int portNumber = 4322;                                  // Use same portNumber as the server soclet

        Socket echoSocket = new Socket(hostName, portNumber);   // Create socket (1) with IP Address and port number

        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);                      // For sending message to socket
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())); // For reading message received
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // TODO : Request for server's public key; "Hello SecStore, please prove your identity"

        // TODO : Request for certificate signed by CA

        // TODO : Receive signed certificate from server

        String fileReceived= "C:\\Users\\surface\\Desktop\\NSproject2\\Client_ReceivedFiles\\ReceivedSignedCertificate_1001198.crt";


        InputStream is = echoSocket.getInputStream();
        FileOutputStream fos = new FileOutputStream(fileReceived);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        System.out.println("Receiving");
        byte[] mybytearray = new byte[1024];
        while(is.read(mybytearray) > 0) {
            System.out.println("Received: " + new String(mybytearray));
            bos.write(mybytearray);
            mybytearray = new byte[1024];
        }

        System.out.println("Done");
//        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
//         System.out.println(new String(mybytearray));
//        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        fos.close();

        /** TO GET SERVER'S PUBLIC KEY **/
        // TODO : Create X509Certificate object
        InputStream fis = new FileInputStream(fileReceived);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate signedCert = (X509Certificate)cf.generateCertificate(fis);

        // TODO : Extract public key from certificate
        PublicKey ServerPublickey = signedCert.getPublicKey();

        // TODO : Check (time) validity of signed certificate
        signedCert.checkValidity();         // Check if current date and time are within validity period specified in the certificate

        /** CHECKING VALIDITY OF SIGNED CERTIFICATE **/

        // TODO: Check validity of signed certificate using CA's public key
        //  Signed certificate also contains the CA's digital signature

        InputStream is_CA = new FileInputStream("C:\\Users\\surface\\Desktop\\NSproject2\\Certificates\\CA.crt");
        CertificateFactory cf_CA = CertificateFactory.getInstance("X.509");
        X509Certificate CAcert = (X509Certificate)cf_CA.generateCertificate(is_CA);
        PublicKey CAPublickey = CAcert.getPublicKey();

        // Verify(PublicKey key) method is called on both certificates with the public key of the certificate authority (UserCA) as parameter.
        signedCert.verify(CAPublickey);     // Exception if it is not the same

        System.out.println("Certificate Verified\n");


        /* Send Client Public Key to Server*/

        // TODO: generate a RSA keypair, initialize as 1024 bits, get public key and private key from this keypair.
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        Key publicKey = keyPair.getPublic();                    // This is client's public keys
        Key privateKey = keyPair.getPrivate();                  // This is client's private keys

        // TODO : Send client public key to server
        System.out.println(publicKey);
        out.print(publicKey);   // Contain a few lines
        out.flush();

        /** DO FILE ENCRYPTION **/
        // TODO : Read file name & send file name
        // TODO: Read file data

        String fileName1 = "C:\\Users\\surface\\Desktop\\NSproject2\\Client_SendFiles\\smallFile.txt";
        String data1 = "";          // Your file content
        String line1;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName1));

        while((line1= bufferedReader.readLine())!=null){
            data1 = data1 +"\n" + line1;                          // Concat all lines of the data. With "\n" for next line
        }
        byte[] totalDataBytes = data1.getBytes();

        /** SEND DATA TO SERVER **/
        // TODO: Create RSA("RSA/ECB/PKCS1Padding") cipher object and initialize is as encrypt mode, use PRIVATE key.
        // TODO: encrypt message
        // TODO: Send encrypted data to server using RSA of size 1024 bits
        
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE,privateKey);

        byte[] toEncrypt = new byte[16];
        int numOfChunk = totalDataBytes.length / 16;
        int currentChunk = 0;
        int startByte = 0;

        while(currentChunk <= numOfChunk){
            byte[] encrypted = rsaCipher.doFinal(toEncrypt);
            byte[] sendArray = new byte[(int)encrypted.length];


            bufferedInputStream.read(sendArray,startByte,sendArray.length); // destination buffer,offset at which to start storing bytes, maximum number of bytes to read
            outputStream = echoSocket.getOutputStream();
            System.out.println(new String(encrypted));
            outputStream.write(sendArray,0,sendArray.length);
            outputStream.flush();
            currentChunk++;
            startByte = startByte + 1024;
        }
        bufferedInputStream.close();
        outputStream.close();
    }
}
