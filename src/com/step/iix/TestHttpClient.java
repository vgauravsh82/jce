package com.step.iix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;

public class TestHttpClient {
    // Reference: http://boredwookie.net/index.php/blog/how-to-use-bouncy-castle-lightweight-api-s-tlsclient/
    //            bcprov-jdk15on-153.tar\src\org\bouncycastle\crypto\tls\test\TlsClientTest.java
    public static void main(String[] args) throws Exception {
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        Socket socket = new Socket(java.net.InetAddress.getByName("fancyssl.hboeck.de"), 443);
        TlsClientProtocol protocol = new TlsClientProtocol(socket.getInputStream(), socket.getOutputStream(),secureRandom);
        DefaultTlsClient client = new DefaultTlsClient() {
            public TlsAuthentication getAuthentication() throws IOException {
                TlsAuthentication auth = new TlsAuthentication() {
                    // Capture the server certificate information!
                    public void notifyServerCertificate(org.bouncycastle.crypto.tls.Certificate serverCertificate) throws IOException {
                    }

                    public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
                        return null;
                    }
                };
                return auth;
            }
        };
        protocol.connect(client);

        java.io.OutputStream output = protocol.getOutputStream();
        output.write("GET / HTTP/1.1\r\n".getBytes("UTF-8"));
        output.write("Host: www.google.com\r\n".getBytes("UTF-8"));
        output.write("Connection: close\r\n".getBytes("UTF-8")); // So the server will close socket immediately.
        output.write("\r\n".getBytes("UTF-8")); // HTTP1.1 requirement: last line must be empty line.
        output.flush();

        java.io.InputStream input = protocol.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;

        while ((line = reader.readLine()) != null)
        {
            System.out.println(line);
        }
    }
}