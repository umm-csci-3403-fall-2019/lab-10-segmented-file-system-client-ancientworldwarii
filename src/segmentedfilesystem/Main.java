package segmentedfilesystem;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    private static final int portNum = 6014;
    private static final String server = "csci-4409.morris.umn.edu";
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        byte[] buff = new byte[1024];
        InetAddress address = InetAddress.getByName(server);
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, portNum);
        socket.send(packet);

        packet = new DatagramPacket(buff, buff.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }

    static class DataPacket {
        byte statusByte;
        byte fileId;
        byte[] packetNumber = new byte[2];
        byte[] data;
        boolean header = false;
        boolean isLast = false;
        public DataPacket(byte[] bytes) {
            this.statusByte = bytes[0];
            if(this.statusByte % 4 == 3) {
                isLast = true;
            }
            this.fileId = bytes[1];
            if(this.statusByte % 2 == 0) {
                this.header = true;
            }
            if(this.header) {
                this.data = Arrays.copyOfRange(bytes, 2, bytes.length);
            }
            else {
                this.packetNumber[0] = bytes[2];
                this.packetNumber[1] = bytes[3];
                this.data = Arrays.copyOfRange(bytes, 4, bytes.length);
            }
        }
    }

    static class FilePackets {
        private DataPacket header;
        private HashMap<Integer, DataPacket> map;
        private int last = -1;

        public boolean isDone() {
            return(last != -1 && map.size() == last+1);
        }

        public FilePackets(DataPacket header) {
            this.header = header;
            this.map = new HashMap<>();
        }

        public void addPacket(DataPacket packet) {
            if(isDone()) {
                return;
            }
            if(packet.header){
                return;
            }else{
                int num = new BigInteger(packet.packetNumber).intValue();
                if(packet.isLast){
                    last = num;
                }
                map.put(num, packet);
            }
        }
    }

}
