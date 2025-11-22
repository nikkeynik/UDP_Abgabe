public class GoBackNServer {

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        int actNr = -1;

        System.out.println("[SERVER] Server läuft auf Port 9876");

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            Message ping = SimpleCodec.decode(receivePacket.getData());
            if(receivePacket.getLength() == 1024) {

            }
            try{
                if(actNr+1 != ping.getPacketNr()){
                    
                }
                else{
                    actNr = ping.getPacketNr();
                }

                Message pong = new GbnPong(ping.getSeq(), System.nanoTime(), ping.getPacketNr());

                InetAddress ipAdress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                sendData = SimpleCodec.encode(pong);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
                serverSocket.send(sendPacket);
            } catch (MissingPacketException e){
                System.out.println("Packet " + (actNr+1) + " fehlt. Packet wird verworfen.");
            }
        }
    }
}
