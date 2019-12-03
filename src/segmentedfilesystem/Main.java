package segmentedfilesystem;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    private final int portNum = 6014;
    private final String server = "csci-4409.morris.umn.edu";
    public static void main(String[] args) {

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
        private int last;
        public FilePackets(DataPacket header) {
            this.header = header;
        }
        public void addPacket(DataPacket packet) {
            if(packet.header){
                header = packet;
            }else{
                int num = new BigInteger(packet.packetNumber).intValue();
                if(packet.isLast){
                    last = num;
                }
                map.put(num, packet);
            }
            if(map.size() == last+1){
                //we're done
            }
        }
    }

}
