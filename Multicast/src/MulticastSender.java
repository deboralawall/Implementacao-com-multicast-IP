import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class MulticastSender {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        final int PORT = 8877;
        final String MULTICAST_ADDR = "224.0.0.2";
        final int DELTA_MS = 5000; // Intervalo de 5 segundos

        try {
            socket = new DatagramSocket();
            String ipLocal = buscarIpLocal();
            System.out.println("Iniciando Sender no IP: " + ipLocal);

            while (true) {
                long agora = System.currentTimeMillis();
                
                // Formato: timestamp,ip
                String msg = agora + "," + ipLocal;
                byte[] outBuf = msg.getBytes();

                InetAddress address = InetAddress.getByName(MULTICAST_ADDR);
                DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
                
                socket.send(outPacket);
                System.out.println("Enviado: " + msg);

                try {
                    Thread.sleep(DELTA_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException ioe) {
            System.err.println("Erro no Sender: " + ioe.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // Método auxiliar para capturar o IP real da máquina (evitando loopback)
    private static String buscarIpLocal() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback()) continue;

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address) {
                    return addr.getHostAddress();
                }
            }
        }
        return "127.0.0.1"; // Fallback
    }
}