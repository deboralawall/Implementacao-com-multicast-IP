import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Enumeration;

public class MulticastReceiver {
  // Tornamos o mapa 'static' para que as classes internas possam acessá-lo facilmente
  static HashMap<String, Integer> mapaContagem = new HashMap<>();

  public static void main(String[] args) {
    
    // THREAD 1: RECEBER MENSAGENS
    class Thread1 extends Thread {
      public void run() {
        try {
          MulticastSocket socket = new MulticastSocket(8877);
          InetAddress address = InetAddress.getByName("224.0.0.2");
          socket.joinGroup(address);
          byte[] inBuf = new byte[256];

          while (true) {
            DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(inPacket);
            String msg = new String(inBuf, 0, inPacket.getLength());
            
            String[] parts = msg.split(",");
            if (parts.length >= 2) {
              String ipOrigem = parts[1].trim();
              
              // Lógica de contagem: se o IP já existe, soma 1, se não, começa com 1
              mapaContagem.put(ipOrigem, mapaContagem.getOrDefault(ipOrigem, 0) + 1);
              System.out.println("Recebido de: " + ipOrigem);
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    // THREAD 2: MONITORAR E CALCULAR SUSPEITO
    class Thread2 extends Thread {
      public void run() {
        int delta = 5000; // 5 segundos (mesmo do sender)
        int tempoDormir = 30000; // 30 segundos de monitoramento
        int esperado = tempoDormir / delta; // Ex: 6 entradas esperadas

        while (true) {
          try {
            Thread.sleep(tempoDormir);
            System.out.println("\n--- Relatório de Suspeitos ---");
            
            for (String ip : mapaContagem.keySet()) {
              int recebidos = mapaContagem.get(ip);
              double suspeito = 0;
              
              if (recebidos < esperado) {
                suspeito = ((double)(esperado - recebidos) / esperado) * 100;
              }
              
              System.out.println("IP: " + ip + " | Recebidos: " + recebidos + " | Suspeito: " + suspeito + "%");
              
              // Reseta a contagem para o próximo ciclo
              mapaContagem.put(ip, 0);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }

    // AQUI ESTÁ O QUE FALTAVA: Instanciar e iniciar as threads
    Thread1 t1 = new Thread1();
    Thread2 t2 = new Thread2();
    
    t1.start();
    t2.start();
  }
}